package com.bil24.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import butterknife.*;
import com.bil24.*;
import com.bil24.R;
import com.bil24.dialog.*;
import com.bil24.fragments.action.filter.*;
import com.bil24.gcm.*;
import com.bil24.storage.*;
import com.bil24.storage.sql.DataBase;
import com.rey.material.widget.TextView;

/**
 * User: SVV
 * Date: 24.05.2015.
 */
public class SettingFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, SettingFragmentListener {

  @BindView(R.id.settingFragmentCityPanel)
  LinearLayout settingFragmentCityPanel;
  @BindView(R.id.settingFragmentTextViewCity)
  TextView settingFragmentTextViewCity;

  @BindView(R.id.settingFragmentEmailPanel)
  LinearLayout settingFragmentEmailPanel;
  @BindView(R.id.settingFragmentTextViewEmail)
  TextView settingFragmentTextViewEmail;

  @BindView(R.id.settingFragmentNotificationPanel)
  RelativeLayout notificationPanel;
  @BindView(R.id.settingFragmentNotificationNewsSwitch)
  Switch notificationNewsSwicth;

  @BindView(R.id.settingFragmentTextViewFid)
  TextView settingFragmentTextViewFid;
  private Unbinder unbinder;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_setting, container, false);
    unbinder = ButterKnife.bind(this, view);

    City city = DataBase.getInstance(getContext()).city.getSelectedCity();
    Venue venue = DataBase.getInstance(getContext()).venue.getSelectedVenue();
    if (city != null && venue != null) {
      changeFilterText(city, venue, Settings.getFilterDateFrom());
    }

    String email = DataBase.getSession(getContext()).getEmail();
    settingFragmentTextViewEmail.setText(email);

    settingFragmentCityPanel.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Dialogs.showFilterDialog(getFragmentManager(), new FilterDialogFragment.ChangeFilterListener() {
          @Override
          public void onChangeFilter(@NonNull City city, @NonNull Venue venue, @NonNull String dateFrom) {
            changeFilterText(city, venue, dateFrom);
          }
        });
      }
    });

    settingFragmentEmailPanel.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Dialogs.showEmailConfirmationDialog(getFragmentManager(), null);
      }
    });

    long fid = Controller.getInstance().getFrontendData(getContext()).getFid();
    settingFragmentTextViewFid.setText("FID " + fid);

    Boolean notNews = SettingsCommon.isNotificationNews(fid);
    if (notNews != null) {
      notificationPanel.setVisibility(View.VISIBLE);
      notificationNewsSwicth.setChecked(notNews);
      notificationNewsSwicth.setOnCheckedChangeListener(SettingFragment.this);
    } else {
      notificationPanel.setVisibility(View.GONE);
    }
    Controller.getInstance().setSettingFragmentListener(this);
    return view;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    if (unbinder != null) unbinder.unbind();
  }

  @SuppressLint("SetTextI18n")
  private void changeFilterText(City city, Venue venue, String dateFrom) {
    settingFragmentTextViewCity.setText(city.getCityName() + ", " + venue.getVenueName() + ", c " + dateFrom);
  }

  @Override
  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    notificationNewsSwicth.setEnabled(false);
    GCMCommand command = notificationNewsSwicth.isChecked() ? GCMCommand.SUBSCRIBE : GCMCommand.UN_SUBSCRIBE;
    FrontendData frontendData = Controller.getInstance().getFrontendData(getContext());
    new GCMDoRequest().execute(new GCMRequest(getContext(), frontendData.getFid(), command, new GCMResponseListener() {
      @Override
      public void onGCMSuccess(GCMCommand command, String token) {
        notificationNewsSwicth.setEnabled(true);
      }

      @Override
      public void onGCMFail(GCMCommand command) {
        notificationNewsSwicth.setEnabled(true);
        notificationNewsSwicth.setOnCheckedChangeListener(null);
        notificationNewsSwicth.toggle();
        notificationNewsSwicth.setOnCheckedChangeListener(SettingFragment.this);
      }
    }));
  }

  @Override
  public void changeEmail(@NonNull String email) {
    if (!isAdded()) return;
    settingFragmentTextViewEmail.setText(email);
  }
}