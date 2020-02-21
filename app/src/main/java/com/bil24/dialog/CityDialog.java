package com.bil24.dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.*;
import butterknife.*;
import com.bil24.*;
import com.bil24.R;
import com.bil24.adapter.CitySpinnerAdapter;
import com.bil24.fragments.action.filter.City;
import com.bil24.myelement.*;
import com.bil24.storage.sql.DataBase;
import com.rey.material.app.Dialog;
import com.rey.material.widget.Button;
import server.net.*;
import server.net.listener.GetCitiesListener;
import server.net.obj.GetCitiesClient;

import java.util.*;

public class CityDialog extends MyDialogFragment implements GetCitiesListener {

  @BindView(R.id.cityDialogSpinner)
  MySpinner<City> citySpinner;
  @BindView(R.id.cityDialogLoadingPanel)
  MyLoadingPanel loadingPanel;

  private Button button;
  private CitySpinnerAdapter adapter;

  public CityDialog() {
    setCancelable(false);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.dialog_city, container);
    ButterKnife.bind(this, view);

    if (adapter == null) adapter = new CitySpinnerAdapter(getContext(), new ArrayList<City>());
    citySpinner.setAdapter(adapter);

    loadingPanel.setText("Загрузка городов...");
    citySpinner.setVisibility(View.GONE);
    loadingPanel.setVisibility(View.VISIBLE);

    NetManager.getCities(this);
    return view;
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    Dialog dialog = super.onCreateDialog(savedInstanceState);
    dialog.setTitle("Выбор города");
    dialog.positiveAction("ОК");
    dialog.positiveActionClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (adapter != null && !adapter.isEmpty()) {
          City city = adapter.getItem(citySpinner.getSelectedItemPosition());
          DataBase.getInstance(getContext()).city.setSelectedCity(city);
          Controller.getInstance().actionUpdate(true);
        }
        dismiss();
      }
    });
    button = (Button) dialog.findViewById(Dialog.ACTION_POSITIVE);
    button.setVisibility(View.GONE);
    return dialog;
  }

  @Override
  public void onGetCities(GetCitiesClient clientData) {
    if (!isAdded()) return;
    button.setVisibility(View.VISIBLE);
    DataBase db = DataBase.getInstance(getContext());
    List<City> cityList = City.convertFromExtraCity(clientData.getCityList());
    City citySelected = null;
    //залипущно выбираем город Сочи
    for (City city : cityList) {
      if (city.getCityName().toLowerCase().contains("сочи")) {
        citySelected = city;
        break;
      }
    }
    db.city.addCities(cityList);
    if (citySelected != null) db.city.setSelectedCity(citySelected);
    db.venue.addVenueDefault(db.city.getSelectedCity());
    db.kind.addKindDefault();
    citySpinner.setData(cityList, db.city.getSelectedCity());
    citySpinner.setVisibility(View.VISIBLE);
    loadingPanel.setVisibility(View.GONE);
  }

  @Override
  public void onGetCitiesFailed(NetException e) {
    if (!isAdded()) return;
    loadingPanel.setVisibility(View.GONE);
    if (e.isUserMessage()) {
      Dialogs.showSnackBar(getActivity(), e.getMessage());
      if (e.getMessage().toLowerCase().contains("ошибка доступа")) dismiss();
    }
  }
}