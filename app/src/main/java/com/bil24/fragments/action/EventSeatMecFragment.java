package com.bil24.fragments.action;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.view.*;
import android.widget.*;
import butterknife.*;
import com.bil24.*;
import com.bil24.R;
import com.bil24.adapter.*;
import com.bil24.dialog.*;
import com.bil24.fragments.action.dialog.*;
import com.bil24.fragments.action.filter.Venue;
import com.bil24.myelement.*;
import com.bil24.storage.*;
import com.bil24.storage.sql.DataBase;
import com.squareup.picasso.Picasso;
import server.net.*;
import server.net.listener.GetActionExtListener;
import server.net.obj.GetActionExtClient;
import server.net.obj.extra.*;

import java.util.*;

/**
 * User: SVV
 * Date: 24.05.2015.
 */
public class EventSeatMecFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, GetActionExtListener {
  @BindView(R.id.eventSeatMecMainPanel)
  RelativeLayout seatMainPanel;

  @BindView(R.id.eventSeatMecSpinnerVenue)
  MySpinner<Venue> venueSpinner;
  @BindView(R.id.eventSeatMecCategoryPriceSpinner)
  MySpinner<ExtraCategoryPriceExt> categoryPriceSpinner;

  private VenueSpinnerAdapter venueAdapter;
  private CategoryPriceSpinnerAdapter categoryPriceAdapter;

  @BindView(R.id.eventSeatMecImageViewPoster2)
  ImageView imageViewPoster2;

  @BindView(R.id.eventSeatMecTextViewFullName)
  TextView textViewFullName;
  @BindView(R.id.eventSeatMecTextViewDescription)
  TextView textViewDescription;

  @BindView(R.id.eventSeatMecTextViewCategoryInfo)
  TextView textViewCategoryInfo;
  @BindView(R.id.eventSeatMecButtonReserve)
  com.rey.material.widget.Button buttonReserve;
  @BindView(R.id.eventSeatMecButtonCityPass)
  com.rey.material.widget.Button buttonCityPass;

  @BindView(R.id.eventSeatMecRefresh)
  SwipeRefreshLayout refresh;
  private Unbinder unbinder;

  @SuppressWarnings("NullableProblems")
  @NonNull
  private ExtraActionV2 action;
  private Map<Long, ExtraActionEventExt> venueActionEventMap = new LinkedHashMap<>();

  private static String ARGUMENT_ACTION = "ARGUMENT_ACTION";

  public static EventSeatMecFragment netInstance(ExtraActionV2 extraAction) {
    Bundle bundle = new Bundle();
    bundle.putSerializable(ARGUMENT_ACTION, extraAction);
    EventSeatMecFragment eventSeatFragment = new EventSeatMecFragment();
    eventSeatFragment.setArguments(bundle);
    return eventSeatFragment;
  }

  @SuppressWarnings("ConstantConditions")
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_event_seat_mec, container, false);
    unbinder = ButterKnife.bind(this, view);

    action = (ExtraActionV2) getArguments().getSerializable(ARGUMENT_ACTION);

    refresh.setOnRefreshListener(this);

    if (venueAdapter == null) {
      venueAdapter = new VenueSpinnerAdapter(getActivity(), new ArrayList<Venue>());
    }
    if (categoryPriceAdapter == null) {
      categoryPriceAdapter = new CategoryPriceSpinnerAdapter(getActivity(), new ArrayList<ExtraCategoryPriceExt>());
    }

    venueSpinner.setAdapter(venueAdapter);
    categoryPriceSpinner.setAdapter(categoryPriceAdapter);

    buttonReserve.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (categoryPriceAdapter.isEmpty()) return;
        final ExtraCategoryPriceExt categoryPrice = categoryPriceSpinner.getSelectedItem();
        if (!action.isKdp()) {
          ReserveConfirmationDialog.showMEC(EventSeatMecFragment.this, buttonReserve, categoryPrice, action.getActionId());
        } else {
          Dialogs.showKdpDialog(getFragmentManager(), new KdpDialog.KdpSuccessListener() {
            @Override
            public void onKdpSuccess(int kdp) {
              ReserveConfirmationDialog.showMEC(EventSeatMecFragment.this, buttonReserve, categoryPrice, action.getActionId());
            }
          }, action.getActionId());
        }
      }
    });

    DisplayMetrics displayMetrics = new DisplayMetrics();
    getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
    imageViewPoster2.getLayoutParams().width = displayMetrics.widthPixels / 2;
    imageViewPoster2.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Controller.getInstance().showImageActivity(action.getBigPosterUrl());
      }
    });

    onRefresh();
    return view;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    if (unbinder != null) unbinder.unbind();
  }

  @Override
  public void onRefresh() {
    refresh.setRefreshing(true);
    seatMainPanel.setVisibility(View.GONE);

    long cityId = DataBase.getInstance(getContext()).city.getSelectedCityId();
    Session session = DataBase.getSession(getContext());
    NetManager.getActionExt(this, cityId, action.getActionId(), session.getUserId());
  }

  private void createContent() {
    try {
      Venue venue = venueAdapter.getItem(venueSpinner.getSelectedItemPosition());
      ExtraActionEventExt actionEvent = venueActionEventMap.get(venue.getVenueId());

      List<ExtraCategoryPriceExt> extraCategoryPriceExtList = new ArrayList<>();
      for (ExtraCategoryPriceLimitExt extraCategoryPriceLimitExt : actionEvent.getCategoryLimitList()) {
        for (ExtraCategoryPriceExt extraCategoryPriceExt : extraCategoryPriceLimitExt.getCategoryList()) {
          extraCategoryPriceExtList.add(extraCategoryPriceExt);
        }
      }
      categoryPriceSpinner.setData(extraCategoryPriceExtList, new MySpinner.OnItemSelectedListener() {
        @Override
        public void onItemSelected(MySpinner spinner, View view, int position, long l) {
        }
      });

      if (venue.isCityPass()) {
        buttonCityPass.setVisibility(View.VISIBLE);
        buttonCityPass.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Controller.getInstance().showCityPassFragment(DataBase.getInstance(getContext()).city.getSelectedCityId());
          }
        });
      }

      if (actionEvent.getCategoryLimitList().isEmpty()) {
        textViewCategoryInfo.setText("Все карты ПРОДАНЫ");
        categoryPriceSpinner.setVisibility(View.GONE);
        buttonReserve.setEnabled(false);
      } else {
        textViewCategoryInfo.setText("Вид карты:");
        categoryPriceSpinner.setVisibility(View.VISIBLE);
        buttonReserve.setEnabled(true);
      }
    } catch (Exception ignored) {
      ignored.printStackTrace();
    }
  }

  @SuppressLint("SetTextI18n")
  @Override
  public void onGetActionExt(GetActionExtClient clientData) {
    if (!isAdded()) return;
    refresh.setRefreshing(false);

    ExtraActionExt action = clientData.getAction();
    textViewDescription.setText(action.getDescription());
    textViewFullName.setText(action.getPosterName());

    //если мероприятие уже не продается
    if (clientData.getAction().getVenueList().isEmpty()) {
      if (Controller.getInstance().getPage() == 1) {
        Controller.getInstance().actionDelete(action.getActionId());
        Controller.getInstance().openPage1(true);
        Dialogs.showSnackBar(getActivity(), "продажи завершены");
      }
      return;
    }

    //заполним кэш сеансов
    venueActionEventMap.clear();
    List<Venue> venueList = new ArrayList<>();
    for (ExtraVenueExt venue : clientData.getAction().getVenueList()) {
      venueActionEventMap.put(venue.getVenueId(), venue.getActionEventList().get(0));
      venueList.add(new Venue(venue.getVenueId(), venue.getVenueName(), 0, "", venue.isCityPass()));
    }

    venueSpinner.setData(venueList, DataBase.getInstance(getContext()).venue.getSelectedVenue(), new MySpinner.OnItemSelectedListener() {
      @Override
      public void onItemSelected(MySpinner spinner, View view, int position, long l) {
        createContent();
      }
    });

    Picasso.with(getActivity())
        .load(action.getBigPosterUrl())
        .placeholder(R.drawable.poster2)
        .error(R.drawable.poster2).fit()
        .into(imageViewPoster2);

    seatMainPanel.setVisibility(View.VISIBLE);
    createContent();
  }

  @Override
  public void onGetActionExtFailed(NetException e) {
    if (!isAdded()) return;
    refresh.setRefreshing(false);
    if (e.isUserMessage()) Dialogs.showSnackBar(getActivity(), e.getMessage());
  }
}