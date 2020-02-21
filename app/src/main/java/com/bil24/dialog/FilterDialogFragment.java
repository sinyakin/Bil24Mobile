package com.bil24.dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.*;
import android.widget.*;
import butterknife.*;
import com.bil24.*;
import com.bil24.R;
import com.bil24.adapter.*;
import com.bil24.fragments.action.dialog.DayDialog;
import com.bil24.fragments.action.filter.*;
import com.bil24.fragments.action.listener.DayChangeListener;
import com.bil24.myelement.*;
import com.bil24.storage.Settings;
import com.bil24.storage.sql.DataBase;
import com.bil24.utils.Utils;
import com.rey.material.app.Dialog;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import server.net.*;
import server.net.listener.GetFilterListener;
import server.net.obj.GetFilterClient;

import java.util.*;

/**
 * Created by SVV on 11.04.2016
 */
public class FilterDialogFragment extends MyDialogFragment implements DayChangeListener, GetFilterListener {
  private static boolean firstStart = true;

  @BindView(R.id.filterDialogFragmentTextViewTitle)
  TextView textViewTitle;

  @BindView(R.id.filterDialogFragmentLoadingPanel)
  MyLoadingPanel loadingPanel;
  @BindView(R.id.filterDialogFragmentDataPanel)
  LinearLayout dataPanel;
  @BindView(R.id.filterDialogFragmentDatePanel)
  RelativeLayout datePanel;
  @BindView(R.id.filterDialogFragmentCitySpinner)
  MySpinner<City> citySpinner;
  @BindView(R.id.filterDialogFragmentVenueSpinner)
  MySpinner<Venue> venueSpinner;
  @BindView(R.id.filterDialogFragmentKindSpinner)
  MySpinner<Kind> kindSpinner;
  @BindView(R.id.filterDialogFragmentTextViewDate)
  TextView textViewDate;

  private CitySpinnerAdapter cityAdapter;
  private KindSpinnerAdapter kindAdapter;
  private VenueSpinnerAdapter venueAdapter;
  private DataBase db;
  private String selectedDay;
  private ChangeFilterListener changeFilterListener;

  public void setChangeFilterListener(ChangeFilterListener changeFilterListener) {
    this.changeFilterListener = changeFilterListener;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.dialog_fragment_filter, container, false);
    ButterKnife.bind(this, view);

    try {
      textViewTitle.setVisibility(View.GONE);

      if (cityAdapter == null) cityAdapter = new CitySpinnerAdapter(getContext(), new ArrayList<City>());
      if (kindAdapter == null) kindAdapter = new KindSpinnerAdapter(getContext(), new ArrayList<Kind>());
      if (venueAdapter == null) venueAdapter = new VenueSpinnerAdapter(getContext(), new ArrayList<Venue>());

      citySpinner.setAdapter(cityAdapter);
      venueSpinner.setAdapter(venueAdapter);
      kindSpinner.setAdapter(kindAdapter);

      db = DataBase.getInstance(getContext());
      loadingPanel.setText("Загрузка фильтра...");
      loadingPanel.setVisibility(View.VISIBLE);
      dataPanel.setVisibility(View.GONE);

      if (firstStart) NetManager.getFilter(this);
      else setData(db.city.getCitiesList(), db.kind.getKindList());

      datePanel.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          DatePickerDialog datePickerDialog = DayDialog.create(getContext(), FilterDialogFragment.this);
          datePickerDialog.show(getActivity().getFragmentManager(), "datePickerDialog");
        }
      });

      selectedDay = Settings.getFilterDateFrom();
    } catch (Exception ex) {
      MyUncaughtExceptionHandler.sendError(ex);
    }
    return view;
  }

  private void setData(List<City> cityList, List<Kind> kindList) {
    loadingPanel.setVisibility(View.GONE);
    dataPanel.setVisibility(View.VISIBLE);
    citySpinner.setData(cityList, db.city.getSelectedCity(), new MySpinner.OnItemSelectedListener() {
      @Override
      public void onItemSelected(MySpinner spinner, View view, int position, long l) {
        City city = cityAdapter.getItem(position);
        if (city == null) return;
        List<Venue> venueList = db.venue.getVenueListByCityId(city.getCityId());
        venueSpinner.setData(venueList, db.venue.getSelectedVenue());
      }
    });
    kindSpinner.setData(kindList, db.kind.getSelectedKind(), null);
    textViewDate.setText(Settings.getFilterDateFrom());
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    Dialog dialog = super.onCreateDialog(savedInstanceState);
    dialog.setTitle("Фильтр");
    dialog.positiveAction("Сохранить");
    dialog.negativeAction("Отмена");
    dialog.positiveActionClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        //сравним выбор
        City newCity = citySpinner.getSelectedItem();
        Venue newVenue = venueSpinner.getSelectedItem();
        Kind newKind = kindSpinner.getSelectedItem();

        City citySelected = db.city.getSelectedCity();
        Venue venueSelected = db.venue.getSelectedVenue();
        Kind kindSelected = db.kind.getSelectedKind();

        if (newCity != null && newVenue != null && newKind != null &&
            citySelected != null && venueSelected != null && kindSelected != null &&
            (!newCity.equals(citySelected) || !newVenue.equals(venueSelected) || !newKind.equals(kindSelected) || !Settings.getFilterDateFrom().equals(selectedDay))) {
          db.city.setSelectedCity(newCity);
          db.venue.setSelectedVenue(newVenue);
          db.kind.setSelectedKind(newKind);
          Settings.setFilterDateFrom(selectedDay);
          Controller.getInstance().actionUpdate(!newCity.equals(citySelected));
          if (changeFilterListener != null) changeFilterListener.onChangeFilter(newCity, newVenue, selectedDay);
        }
        dismiss();
      }
    });
    dialog.negativeActionClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dismiss();
      }
    });
//    saveButton = (Button) dialog.findViewById(Dialog.ACTION_POSITIVE);
    return dialog;
  }

  @Override
  public void dayChange(Date date) {
    selectedDay = Utils.dateFormatDDMMYYYY(date);
    textViewDate.setText(selectedDay);
  }

  @Override
  public void onGetFilter(final GetFilterClient clientData) {
    if (!isAdded()) return;
    new Thread(new Runnable() {
      @Override
      public void run() {
        DataBase.getInstance(getContext()).updateFilterData(clientData.getCityList(), clientData.getKindList());
        if (firstStart) firstStart = false;
        if (!isAdded()) return;
        final List<City> cityList = db.city.getCitiesList();
        final List<Kind> kindList = db.kind.getKindList();
        citySpinner.post(new Runnable() {
          @Override
          public void run() {
            setData(cityList, kindList);
          }
        });
      }
    }).start();
  }

  @Override
  public void onGetFilterFailed(NetException e) {
    if (!isAdded()) return;
    NetManager.getFilter(this);
  }

  public interface ChangeFilterListener {
    void onChangeFilter(@NonNull City city, @NonNull Venue venue, @NonNull String dateFrom);
  }
}