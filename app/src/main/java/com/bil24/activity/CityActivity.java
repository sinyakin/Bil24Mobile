package com.bil24.activity;

import android.content.*;
import android.support.v7.app.*;
import android.os.Bundle;
import android.support.v7.widget.*;
import android.view.View;
import android.widget.AdapterView;
import butterknife.*;

import com.bil24.InstallReferrerReceiver;
import com.bil24.R;
import com.bil24.adapter.CityAdapter;
import com.bil24.dialog.Dialogs;
import com.bil24.fragments.action.filter.City;
import com.bil24.myelement.MyLoadingPanel;
import com.bil24.storage.FrontendData;
import com.bil24.storage.sql.DataBase;
import server.net.*;
import server.net.listener.*;
import server.net.obj.*;

import java.util.*;

import static com.bil24.Bil24Application.getContext;

public class CityActivity extends AppCompatActivity implements GetFilterListener {
  private List<City> cityList = new ArrayList<>();
  private City selectedCity;

  @BindView(R.id.cityActivityLoadingPanel)
  MyLoadingPanel loadingPanel;
  @BindView(R.id.cityActivityRecyclerView)
  android.support.v7.widget.RecyclerView recyclerView;
  @BindView(R.id.cityActivityButtonOk)
  com.rey.material.widget.Button nextButton;

  public static Intent getStartIntent(Context context) {
    return new Intent(context, CityActivity.class);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_city);
    ButterKnife.bind(this);

    loadingPanel.setVisibility(View.VISIBLE);
    nextButton.setVisibility(View.GONE);
    loadingPanel.setText("Загрузка городов...");
    recyclerView.setHasFixedSize(true);
    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(layoutManager);
    setTitle("  Выбор города");

    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
      actionBar.setDisplayUseLogoEnabled(true);
      actionBar.setLogo(R.mipmap.logo_toolbar);
    }

    final DataBase db = DataBase.getInstance(getContext());
    nextButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (selectedCity == null) return;
        Dialogs.startProcess(CityActivity.this, "Загрузка...");
        if (db.mainData.isReferrerAuth()) {
          FrontendData frontendData = db.mainData.getFrontendData();
          InstallReferrerReceiver.authFrontend(CityActivity.this, frontendData.getFid(), frontendData.getToken(), new InstallReferrerReceiver.ReferrerAuthListener() {
            @Override
            public void referrerFinish() {
              next();
            }
          });
        } else next();
      }
    });

    if (db.mainData.isReferrerAuth()) {
      FrontendData frontendData = db.mainData.getFrontendData();
      InstallReferrerReceiver.authFrontend(CityActivity.this, frontendData.getFid(), frontendData.getToken(), new InstallReferrerReceiver.ReferrerAuthListener() {
        @Override
        public void referrerFinish() {
          NetManager.getFilter(CityActivity.this);
        }
      });
    } else NetManager.getFilter(this);
  }

  private void next() {
    Dialogs.stopProcess();
    if (selectedCity == null) return;
    DataBase db = DataBase.getInstance(getContext());
    db.city.addCities(cityList);
    db.city.setSelectedCity(selectedCity);
    db.venue.addVenueDefault(selectedCity);
    db.kind.addKindDefault();
    nextButton.setEnabled(false);
    CityActivity.this.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        Intent intent = new Intent(CityActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
      }
    });
  }

  @Override
  public void onGetFilter(GetFilterClient clientData) {
    if (isFinishing()) return;
    this.cityList.clear();
    this.cityList.addAll(City.convertFromExtraFilterCity(clientData.getCityList()));

    City citySochi;
    //залипущно выбираем город Сочи
    for (City city : cityList) {
      if (city.getCityName().toLowerCase().contains("сочи")) {
        citySochi = city;
        this.cityList.remove(citySochi);
        this.cityList.add(0, citySochi);
        break;
      }
    }

    loadingPanel.setVisibility(View.GONE);
    final CityAdapter cityAdapter = new CityAdapter(this, cityList);
    cityAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (cityAdapter.getList().isEmpty()) return;
        nextButton.setVisibility(View.VISIBLE);
        selectedCity = cityAdapter.getList().get(position);
      }
    });
    recyclerView.setAdapter(cityAdapter);
  }

  @Override
  public void onGetFilterFailed(NetException e) {
    if (isFinishing()) return;
    if (e.isUserMessage()) {
      Dialogs.toastLong(e.getMessage());
      if (e.getMessage().toLowerCase().contains("ошибка доступа")) finish();
    } else {
      NetManager.getFilter(this);
      loadingPanel.setVisibility(View.GONE);
    }
  }
}
