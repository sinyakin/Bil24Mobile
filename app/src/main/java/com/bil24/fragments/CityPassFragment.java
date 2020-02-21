package com.bil24.fragments;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.*;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.*;
import com.bil24.R;
import com.bil24.adapter.*;
import com.bil24.dialog.Dialogs;
import com.bil24.myelement.*;
import server.net.*;
import server.net.listener.*;
import server.net.obj.*;
import server.net.obj.extra.*;

import java.util.*;

/**
 * User: SVV
 * Date: 24.05.2015.
 */
public class CityPassFragment extends MyDialogFragment implements SwipeRefreshLayout.OnRefreshListener, GetVenueTypesListener, GetVenuesListener {
  private static final String ARGUMENT_CITY_ID = "ARGUMENT_CITY_ID";

  @BindView(R.id.cityPassFragmentSpinnerVenueTypes)
  MySpinner<ExtraVenueType> spinnerVenuesType;
  @BindView(R.id.cityPassFragmentListView)
  ListView listView;
  @BindView(R.id.cityPassFragmentRefresh)
  SwipeRefreshLayout refresh;
  @BindView(R.id.cityPassFragmentTvEmpty)
  TextView tvEmpty;
  @BindView(R.id.cityPassFragmentLoadPanel)
  MyLoadingPanel loadPanel;

  VenueTypesSpinnerAdapter adapterVenueTypes;
  VenueCityPassAdapter adapterVenue;
  long cityId = -1;
  private Unbinder unbinder;

  public static CityPassFragment newInstance(long cityId) {
    CityPassFragment fragment = new CityPassFragment();
    Bundle bundle = new Bundle();
    bundle.putLong(ARGUMENT_CITY_ID, cityId);
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_city_pass, container, false);
    unbinder = ButterKnife.bind(this, view);

    tvEmpty.setVisibility(View.GONE);
    refresh.setOnRefreshListener(this);
    loadPanel.setText("");
    loadPanel.setVisibility(View.GONE);

    cityId = getArguments().getLong(ARGUMENT_CITY_ID, -1);

    if (adapterVenueTypes == null) {
     adapterVenueTypes = new VenueTypesSpinnerAdapter(getActivity(), new ArrayList<ExtraVenueType>());
    }
    if (adapterVenue == null) {
      adapterVenue = new VenueCityPassAdapter(getActivity(), new ArrayList<ExtraVenue>());
    }

    spinnerVenuesType.setAdapter(adapterVenueTypes);
    listView.setAdapter(adapterVenue);

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
    spinnerVenuesType.setEnabled(false);
    NetManager.getVenueTypes(this);
  }

  @Override
  public void onGetVenueTypes(GetVenueTypesClient clientData) {
    if (!isAdded()) return;

    if (clientData.getVenueTypeList().isEmpty()) {
      tvEmpty.setVisibility(View.VISIBLE);
      spinnerVenuesType.setVisibility(View.GONE);
      listView.setVisibility(View.GONE);
    } else {
      tvEmpty.setVisibility(View.GONE);
      spinnerVenuesType.setVisibility(View.VISIBLE);
      listView.setVisibility(View.VISIBLE);
    }

    refresh.setRefreshing(false);
    spinnerVenuesType.setEnabled(true);

    List<ExtraVenueType> list = new ArrayList<>();
    ExtraVenueType venueTypeFirst = null;
    for (ExtraVenueType venueType : clientData.getVenueTypeList()) {
      if (venueType.getVenueTypeId() == 0) continue;
      if (venueType.getVenueTypeId() == 3) venueTypeFirst = venueType;
      list.add(venueType);
    }

    spinnerVenuesType.setData(list, venueTypeFirst, new MySpinner.OnItemSelectedListener() {
      @Override
      public void onItemSelected(MySpinner spinner, View view, int position, long l) {
        ExtraVenueType venueType = spinnerVenuesType.getSelectedItem();
        if (venueType == null) return;
        spinnerVenuesType.setEnabled(false);
        loadPanel.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);
        NetManager.getVenues(CityPassFragment.this, cityId , venueType.getVenueTypeId());
      }
    });
  }

  @Override
  public void onGetVenueTypesFailed(NetException e) {
    if (!isAdded()) return;
    refresh.setRefreshing(false);
    spinnerVenuesType.setEnabled(true);
    if (e.isUserMessage()) Dialogs.showSnackBar(getActivity(), e.getMessage());
  }

  @Override
  public void onGetVenues(GetVenuesClient clientData) {
    spinnerVenuesType.setEnabled(true);
    loadPanel.setVisibility(View.GONE);
    adapterVenue.clear();
    adapterVenue.addAll(clientData.getVenueList());

    if (clientData.getVenueList().isEmpty()) {
      tvEmpty.setVisibility(View.VISIBLE);
    } else {
      tvEmpty.setVisibility(View.GONE);
    }
  }

  @Override
  public void onGetVenuesFailed(NetException e) {
    if (!isAdded()) return;
    spinnerVenuesType.setEnabled(true);
    loadPanel.setVisibility(View.GONE);
    if (e.isUserMessage()) Dialogs.showSnackBar(getActivity(), e.getMessage());
  }
}