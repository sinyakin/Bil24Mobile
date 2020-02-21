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
import com.bil24.fragments.action.listener.DayChangeListener;
import com.bil24.myelement.*;
import com.bil24.storage.*;
import com.bil24.storage.sql.DataBase;
import com.bil24.utils.Utils;
import com.squareup.picasso.Picasso;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import server.net.*;
import server.net.listener.GetActionExtListener;
import server.net.obj.GetActionExtClient;
import server.net.obj.extra.*;

import java.util.*;

/**
 * User: SVV
 * Date: 24.05.2015.
 */
public class EventSeatFragment extends Fragment implements DayChangeListener, SwipeRefreshLayout.OnRefreshListener, MyNumberPicker.ReservationCategoryListener, ReserveConfirmationDialog.SuccessReservationListener, GetActionExtListener {
  @BindView(R.id.eventSeatMainPanel)
  RelativeLayout seatMainPanel;
  @BindView(R.id.eventSeatCategoryPricePanel)
  RelativeLayout categoryPricePanel;

  @BindView(R.id.eventSeatSpinnerVenue)
  MySpinner<Venue> venueSpinner;
  @BindView(R.id.eventSeatCategoryPriceSpinner)
  MySpinner<ExtraCategoryPriceExt> categoryPriceSpinner;

  private VenueSpinnerAdapter venueAdapter;
  private CategoryPriceSpinnerAdapter categoryPriceAdapter;

  @BindView(R.id.eventSeatImageViewPoster2)
  ImageView imageViewPoster2;

  @BindView(R.id.eventSeatDayPanel)
  RelativeLayout dayPanel;
  @BindView(R.id.eventSeatTextViewDayOfWeek)
  TextView textViewDayOfWeek;
  @BindView(R.id.eventSeatTextViewMonth)
  TextView textViewMonth;
  @BindView(R.id.eventSeatTextViewDayOfMonth)
  TextView textViewDayOfMonth;
  @BindView(R.id.eventSeatTextViewYear)
  TextView textViewYear;

  @BindView(R.id.eventSeatTextViewFullName)
  TextView textViewFullName;
  @BindView(R.id.eventSeatTextViewDuration)
  TextView textViewDuration;
  @BindView(R.id.eventSeatTextViewETicket)
  TextView textViewETicket;
  @BindView(R.id.eventSeatTextViewDescription)
  TextView textViewDescription;

  @BindView(R.id.eventSeatTextViewCategoryInfo)
  TextView textViewCategoryInfo;
  @BindView(R.id.eventSeatButtonLimit)
  com.rey.material.widget.Button buttonLimit;
  @BindView(R.id.eventSeatButtonReserve)
  com.rey.material.widget.Button buttonReserve;

  @BindView(R.id.eventSeatSeatingPlanPanel)
  RelativeLayout seatingPlanPanel;
  @BindView(R.id.eventSeatButtonSeatingPlan)
  com.rey.material.widget.Button buttonSeatingPlan;

  @BindView(R.id.eventSeatPromoEditText)
  EditText promoEditText;
  @BindView(R.id.eventSeatButtonPromoCode)
  com.rey.material.widget.Button buttonPromoCode;

  @BindView(R.id.eventSeatNumberPicker)
  MyNumberPicker numberPicker;

  @BindView(R.id.eventSeatRefresh)
  SwipeRefreshLayout refresh;

  @BindView(R.id.radioGroup1)
  MultiRowsRadioGroup multiRowsRadioGroup;
  private Unbinder unbinder;

  @NonNull
  private String selectedDay = "16.05.2016";

  @SuppressWarnings("NullableProblems")
  @NonNull
  private ExtraActionV2 action;
  private Map<Long, Map<String, List<ExtraActionEventExt>>> venueActionEventMap = new LinkedHashMap<>();
  private Map<ExtraCategoryPriceExt, Integer> categoryQuantityMap = new HashMap<>();

  private static String ARGUMENT_ACTION = "ARGUMENT_ACTION";

  public static EventSeatFragment newInstance(ExtraActionV2 extraAction) {
    EventSeatFragment fragment = new EventSeatFragment();
    Bundle bundle = new Bundle();
    bundle.putSerializable(ARGUMENT_ACTION, extraAction);
    fragment.setArguments(bundle);
    return fragment;
  }

  @SuppressWarnings("ConstantConditions")
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_event_seat, container, false);
    unbinder = ButterKnife.bind(this, view);

    action = (ExtraActionV2) getArguments().getSerializable(ARGUMENT_ACTION);

    refresh.setOnRefreshListener(this);

    if (venueAdapter == null) venueAdapter = new VenueSpinnerAdapter(getActivity(), new ArrayList<Venue>());
    if (categoryPriceAdapter == null)
      categoryPriceAdapter = new CategoryPriceSpinnerAdapter(getActivity(), new ArrayList<ExtraCategoryPriceExt>());

    venueSpinner.setAdapter(venueAdapter);
    categoryPriceSpinner.setAdapter(categoryPriceAdapter);

    buttonReserve.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (!action.isKdp()) {
          reservation();
        } else {
          Dialogs.showKdpDialog(getFragmentManager(), new KdpDialog.KdpSuccessListener() {
            @Override
            public void onKdpSuccess(int kdp) {
              reservation();
            }
          }, action.getActionId());
        }
      }
    });
    numberPicker.setData(getActivity(), this, categoryPriceAdapter);

    DisplayMetrics displayMetrics = new DisplayMetrics();
    getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
    imageViewPoster2.getLayoutParams().width = displayMetrics.widthPixels / 2;

    multiRowsRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(RadioGroup group, int checkedId) {
        CompoundButton cb = group.findViewById(checkedId);
        if (cb != null && (cb instanceof MyRadioButton) && cb.isChecked()) {
          timeChange(((MyRadioButton) cb).getActionEvent());
        }
      }
    });
    imageViewPoster2.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Controller.getInstance().showImageActivity(action.getBigPosterUrl());
      }
    });

    buttonPromoCode.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Dialogs.addPromoCodes(new Dialogs.DialogAddPromoCodesListener() {
          @Override
          public void onSuccess(boolean newPromo) {
            promoEditText.setText("");
          }
        }, getActivity(), EventSeatFragment.this, promoEditText.getText().toString());
      }
    });

    selectedDay = Utils.dateFormatDDMMYYYY(new Date());
    onRefresh();
    return view;
  }

  public void reservation() {
    ReserveConfirmationDialog.show(EventSeatFragment.this, buttonReserve, categoryQuantityMap, EventSeatFragment.this, action.getActionId(), false);
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
//    venueSpinner.setOnItemSelectedListener(null);

    long cityId = DataBase.getInstance(getContext()).city.getSelectedCityId();
    Session session = DataBase.getSession(getContext());
    NetManager.getActionExt(this, cityId, action.getActionId(), session.getUserId());
  }

  private void createContent() {
    try {
      Venue venue = venueAdapter.getItem(venueSpinner.getSelectedItemPosition());
      Map<String, List<ExtraActionEventExt>> map = venueActionEventMap.get(venue.getVenueId());
      final List<String> dateList = new ArrayList<>(map.keySet());

      textViewDayOfWeek.setText("");
      textViewMonth.setText("");
      textViewDayOfMonth.setText("");
      textViewYear.setText("");
      selectedDay = dateList.get(0);
      multiRowsRadioGroup.removeAllViews();
      onSuccessReservation();
      buttonReserve.setEnabled(false);

      final Date dateBegin = Utils.dateFormatDDMMYYYY(dateList.get(0));
      updateDayPanel(dateBegin);
      List<ExtraActionEventExt> eventList = map.get(selectedDay);
      multiRowsRadioGroup.addView(new EventSeatRadioGroup(getActivity(), multiRowsRadioGroup, eventList));
      final Calendar[] calendars = new Calendar[dateList.size()];
      int i = 0;
      for (String date : dateList) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(Utils.dateFormatDDMMYYYY(date));
        calendars[i++] = calendar;
      }

      dayPanel.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          DatePickerDialog datePickerDialog = DayDialog.create(getContext(), EventSeatFragment.this, calendars, selectedDay);
          datePickerDialog.show(getActivity().getFragmentManager(), "datePickerDialog");
        }
      });
    } catch (Exception ignored) {
      ignored.printStackTrace();
    }
  }

  @Override
  public void dayChange(Date date) {
    onSuccessReservation();
    selectedDay = Utils.dateFormatDDMMYYYY(date);
    multiRowsRadioGroup.removeAllViews();
    updateDayPanel(date);
    if (Controller.getInstance().openTwoPage()) {
      //если открыта вторая страница, то обновим панель времени сеансов
      Venue venue = venueAdapter.getItem(venueSpinner.getSelectedItemPosition());
      List<ExtraActionEventExt> eventList = venueActionEventMap.get(venue.getVenueId()).get(selectedDay);

      if (eventList == null || eventList.isEmpty()) {
        Dialogs.showSnackBar(getActivity(), ("Сеансов " + selectedDay + " нет."));
        return;
      }
      multiRowsRadioGroup.addView(new EventSeatRadioGroup(getActivity(), multiRowsRadioGroup, eventList));
    }
  }

  public void timeChange(@NonNull final ExtraActionEventExt actionEvent) {
    onSuccessReservation();

    //// TODO: 21.07.2016
    try {
      actionEvent.getCategoryLimitList();
    } catch (Exception ex) {
      return;
    }

    if (actionEvent.iseTicket()) {
      textViewETicket.setVisibility(View.VISIBLE);
    } else {
      textViewETicket.setVisibility(View.GONE);
    }
    if (actionEvent.getPlacementUrl() == null) {
      seatingPlanPanel.setVisibility(View.GONE);
      categoryPricePanel.setVisibility(View.VISIBLE);
      setCategoryDataModel(actionEvent.getCategoryLimitList());
    } else {
      if (actionEvent.getCategoryLimitList().isEmpty()) {
        seatingPlanPanel.setVisibility(View.VISIBLE);
        categoryPricePanel.setVisibility(View.GONE);
      } else {
        seatingPlanPanel.setVisibility(View.VISIBLE);
        categoryPricePanel.setVisibility(View.VISIBLE);
        setCategoryDataModel(actionEvent.getCategoryLimitList());
      }

      buttonSeatingPlan.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          final String actionEventTime = actionEvent.getDay() + " " + actionEvent.getTime();
          if (!action.isKdp()) {
            Controller.getInstance().showSeatingPlanActivity(actionEvent.getPlacementUrl(), action.getActionId(), action.getActionName(), actionEvent.getActionEventId(), actionEventTime);
          } else {
            Dialogs.showKdpDialog(getFragmentManager(), new KdpDialog.KdpSuccessListener() {
              @Override
              public void onKdpSuccess(int kdp) {
                Controller.getInstance().showSeatingPlanActivity(actionEvent.getPlacementUrl() + "&kdp=" + kdp, action.getActionId(), action.getActionName(), actionEvent.getActionEventId(), actionEventTime);
              }
            }, action.getActionId());
          }
        }
      });
    }
  }

  private void setCategoryDataModel(List<ExtraCategoryPriceLimitExt> modelList) {
    final StringBuilder builder = new StringBuilder();
    boolean needShowLimitButton = false;
    List<ExtraCategoryPriceExt> extraCategoryPriceExtList = new ArrayList<>();
    for (ExtraCategoryPriceLimitExt extraCategoryPriceLimitExt : modelList) {
      Integer remainder = extraCategoryPriceLimitExt.getRemainder();
      if (remainder != null) needShowLimitButton = true;
      String remainderAsString = (remainder == null) ? "-" : String.valueOf(remainder);
      for (ExtraCategoryPriceExt extraCategoryPriceExt : extraCategoryPriceLimitExt.getCategoryList()) {
        builder.append(extraCategoryPriceExt.getCategoryPriceName()).append("\n");
        extraCategoryPriceExtList.add(extraCategoryPriceExt);
      }
      builder.append("Общий остаток мест для категорий: ").append(remainderAsString).append("\n\n");
    }
    categoryPriceSpinner.setData(extraCategoryPriceExtList, new MySpinner.OnItemSelectedListener() {
      @Override
      public void onItemSelected(MySpinner spinner, View view, int position, long l) {
        Integer quantity = categoryQuantityMap.get(categoryPriceAdapter.getItem(position));
        numberPicker.setNumber(quantity);
      }
    });
    //кнопка лимитов
    //показывать кнопку только если не ОМП
    if (needShowLimitButton && Controller.getInstance().getFrontendData(getContext()).getFrontendType() != FrontendType.ANDROID) {
      buttonLimit.setVisibility(View.VISIBLE);
      buttonLimit.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Dialogs.showInfoDialog(getFragmentManager(), "Лимиты", builder.toString());
        }
      });
    } else {
      buttonLimit.setVisibility(View.GONE);
    }

    if (modelList.isEmpty()) {
      textViewCategoryInfo.setText("Все входные билеты без мест ПРОДАНЫ");
    } else {
      textViewCategoryInfo.setText("Входные билеты без мест:");
    }
  }

  //установим дату на панели
  private void updateDayPanel(Date date) {
    GregorianCalendar calendar = new GregorianCalendar();
    calendar.setTime(date);
    textViewDayOfWeek.setText(calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()));
    textViewMonth.setText(calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()));
    textViewDayOfMonth.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
    textViewYear.setText(String.valueOf(calendar.get(Calendar.YEAR)));
  }

  @Override
  public void onReservationQuantityPlace(int quantity) {
    ExtraCategoryPriceExt categoryPrice = categoryPriceSpinner.getSelectedItem();
    if (quantity > 0) categoryQuantityMap.put(categoryPrice, quantity);
    else categoryQuantityMap.remove(categoryPrice);
    buttonReserve.setEnabled(!categoryQuantityMap.isEmpty());
  }

  @Override
  public void onSuccessReservation() {
    categoryQuantityMap.clear();
    numberPicker.setNumber(0);
    buttonReserve.setEnabled(false);
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
    for (ExtraVenueExt venue : clientData.getAction().getVenueList()) {
      Map<String, List<ExtraActionEventExt>> map = new LinkedHashMap<>();
      for (ExtraActionEventExt actionEvent : venue.getActionEventList()) {
        List<ExtraActionEventExt> list = map.get(actionEvent.getDay());
        if (list == null) {
          list = new ArrayList<>();
          map.put(actionEvent.getDay(), list);
        }
        list.add(actionEvent);
      }
      venueActionEventMap.put(venue.getVenueId(), map);
    }

    List<Venue> venueList = new ArrayList<>();
    for (ExtraVenueExt extraVenueExt : clientData.getAction().getVenueList()) {
      venueList.add(new Venue(extraVenueExt.getVenueId(), extraVenueExt.getVenueName(), 0, ""));
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

    textViewDuration.setText("Продолжительность: " + action.getDuration() + " мин.");

    seatMainPanel.setVisibility(View.VISIBLE);
    categoryPricePanel.setVisibility(View.GONE);
    seatingPlanPanel.setVisibility(View.GONE);
    textViewETicket.setVisibility(View.GONE);

    createContent();
  }

  @Override
  public void onGetActionExtFailed(NetException e) {
    if (!isAdded()) return;
    refresh.setRefreshing(false);
    if (e.isUserMessage()) Dialogs.showSnackBar(getActivity(), e.getMessage());
  }
}