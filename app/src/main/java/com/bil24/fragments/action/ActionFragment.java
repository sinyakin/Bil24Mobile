package com.bil24.fragments.action;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.*;
import android.widget.*;
import butterknife.*;
import com.bil24.*;
import com.bil24.R;
import com.bil24.adapter.ActionAdapter;
import com.bil24.dialog.Dialogs;
import com.bil24.fragments.action.filter.*;
import com.bil24.fragments.action.listener.*;
import com.bil24.storage.*;
import com.bil24.storage.sql.*;
import com.bil24.utils.Utils;
import server.net.*;
import server.net.listener.*;
import server.net.obj.*;
import server.net.obj.extra.*;

import java.util.*;

/**
 * User: SVV
 * Date: 24.05.2015.
 */
public class ActionFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, GetActionsV2Listener, ActionUpdateListener, FilterListener {

  @BindView(R.id.actionFragmentTopPanel)
  LinearLayout topPanel;
  @BindView(R.id.actionFragmentFilter)
  TextView tvFilter;
  @BindView(R.id.actionFragmentListView)
  ListView listView;
  @BindView(R.id.refreshAction)
  SwipeRefreshLayout refreshAction;
  @BindView(R.id.tvActionEmptyText)
  TextView tvActionEmptyText;
  @BindView(R.id.actionFragmentTestZoneTextView)
  TextView actionFragmentTestZoneTextView;
  private Unbinder unbinder;

  ActionAdapter actionAdapter;

  private Map<Long, ExtraActionV2> actionMap = new LinkedHashMap<>();
  private List<ExtraActionV2> filterActionList = new ArrayList<>();

  private String filter = "";

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_action, container, false);
    unbinder = ButterKnife.bind(this, view);

    Controller.getInstance().setFilterListener(this);
    tvActionEmptyText.setVisibility(View.GONE);
    refreshAction.setOnRefreshListener(this);
    Controller.getInstance().setActionUpdateListener(this);

    if (SettingsCommon.isRealZone()) actionFragmentTestZoneTextView.setVisibility(View.GONE);
    else actionFragmentTestZoneTextView.setVisibility(View.VISIBLE);

    if (actionAdapter != null) {
      listView.setAdapter(actionAdapter);
    }

    tvFilter.setText("Фильтр: ");
    topPanel.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Dialogs.showFilterDialog(getFragmentManager(), null);
      }
    });

    //проверим город
    DataBase db = DataBase.getInstance(getContext());
    City citySelected = db.city.getSelectedCity();
    Venue venueSelected = db.venue.getSelectedVenue();
    Kind kindSelected = db.kind.getSelectedKind();
    //todo
    /*if (citySelected == null) {
      Controller.getInstance().showCityActivity();
//      Dialogs.showCityDialog(getFragmentManager());
    }
    else {*/
    updateFilterText(citySelected, venueSelected, kindSelected);
    if (actionAdapter == null) refreshAction.setRefreshing(true);
    getActions();
//    }
    return view;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    if (unbinder != null) unbinder.unbind();
  }

  @SuppressLint("SetTextI18n")
  @SuppressWarnings("ConstantConditions")
  public void updateFilterText(City citySelected, Venue venueSelected, Kind kindSelected) {
    if (citySelected == null || venueSelected == null || kindSelected == null) return;
    tvFilter.setText("Фильтр: " + citySelected.getCityName() + ", " + kindSelected.getKindName() + ", " + venueSelected.getVenueName() + ", c " + Settings.getFilterDateFrom());
  }

  @Override
  public void onRefresh() {
    refreshAction.setRefreshing(true);
    getActions();
  }

  @Override
  public void actionUpdate(boolean changeCity) {
    DataBase db = DataBase.getInstance(getContext());
    City citySelected = db.city.getSelectedCity();
    Venue venueSelected = db.venue.getSelectedVenue();
    Kind kindSelected = db.kind.getSelectedKind();
    updateFilterText(citySelected, venueSelected, kindSelected);
    if (changeCity) onRefresh();
    else updateFromCash();
  }

  private void updateFromCash() {
    new Thread(new Runnable() {
      @Override
      public void run() {
        final List<ExtraActionV2> resultList = new ArrayList<>();
        DataBase db = DataBase.getInstance(getContext());
        City city = db.city.getSelectedCity();
        Venue venue = db.venue.getSelectedVenue();
        Kind kind = db.kind.getSelectedKind();

        String dateFromMemory = Settings.getFilterDateFrom();
        Date date = Utils.dateFormatDDMMYYYY(dateFromMemory);
        if (city != null && venue != null && kind != null) {
          for (ExtraActionV2 actionV2 : actionMap.values()) {
            if (actionV2.getCityId() == city.getCityId() &&
                (venue.getVenueId() == TableVenue.allVenueId || actionV2.getVenueMap().containsKey(venue.getVenueId())) &&
                (kind.getKindId() == TableKind.allKindId || actionV2.getKindId() == kind.getKindId()) &&
                Utils.dateFormatDDMMYYYY(actionV2.getFirstEventDate()).compareTo(date) >= 0) {
              resultList.add(actionV2);
            }
          }
        }
        filterActionList.clear();
        filterActionList.addAll(resultList);
        if (!isAdded()) return;
        getActivity().runOnUiThread(new Runnable() {
          @Override
          public void run() {
            refreshAction.setRefreshing(false);
            if (!filter.isEmpty()) actionFilter(filter);
            else updateActionAdapter(resultList);

          }
        });
      }
    }).start();
  }

  @Override
  public void actionDelete(long actionId) {
    if (!isAdded()) return;
    ExtraActionV2 extraActionV2 = actionMap.get(actionId);
    actionMap.remove(actionId);
    filterActionList.remove(extraActionV2);
    if (actionAdapter != null) actionAdapter.remove(extraActionV2);
  }

  private void updateActionAdapter(List<ExtraActionV2> actionList) {
    if (actionList.isEmpty())
      tvActionEmptyText.setVisibility(View.VISIBLE);
    else {
      tvActionEmptyText.setVisibility(View.GONE);
    }

    actionAdapter = new ActionAdapter(getActivity(), new ArrayList<>(actionList));
    listView.setAdapter(actionAdapter);
  }

  private void getActions() {
    DataBase db = DataBase.getInstance(getContext());
    long cityId = db.city.getSelectedCityId();
    NetManager.getActionsV2(ActionFragment.this, cityId);
  }

  @Override
  public synchronized void actionFilter(String text) {
    this.filter = text;
    //поиск
    new SearchRunnable(getActivity(), filterActionList, new SearchRunnable.ResultListener() {
      @Override
      public void searchResult(List<ExtraActionV2> actionList) {
        updateActionAdapter(actionList);
      }
    }).execute(filter.split(" "));
  }

  @Override
  public void setFilterText(String text) {
    this.filter = text;
  }

  @Override
  public void onGetActionsV2(GetActionsV2Client clientData) {
    if (!isAdded()) return;
    for (ExtraActionV2 extraActionV2 : clientData.getActionList()) {
      actionMap.put(extraActionV2.getActionId(), extraActionV2);
    }
    updateFromCash();

    //только после первой загрузки данных начнем работу с gcm, чтобы не тормозить трафик
    Controller.getInstance().processWithGCM();
  }

  @Override
  public void onGetActionsV2Failed(NetException e) {
    if (!isAdded()) return;
    refreshAction.setRefreshing(false);
    if (e.isUserMessage() && isAdded()) Dialogs.toastLong(e.getMessage());
  }
}