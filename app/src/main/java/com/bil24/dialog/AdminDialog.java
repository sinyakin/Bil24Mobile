package com.bil24.dialog;

import android.app.*;
import android.content.*;
import android.os.*;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.*;
import android.widget.CompoundButton;
import butterknife.*;
import com.bil24.R;
import com.bil24.activity.SplashScreenActivity;
import com.bil24.storage.SettingsCommon;
import com.bil24.utils.Utils;

public class AdminDialog extends DialogFragment {

  @BindView(R.id.dialogAdminRBReal)
  com.rey.material.widget.RadioButton rbReal;
  @BindView(R.id.dialogAdminRBTest)
  com.rey.material.widget.RadioButton rbTest;
  @BindView(R.id.dialogAdminCBServiceInfo)
  com.rey.material.widget.CheckBox cbServiceInfo;

  @BindView(R.id.adminDialogButtonCancel)
  com.rey.material.widget.Button buttonCancel;
  @BindView(R.id.adminDialogButtonOk)
  com.rey.material.widget.Button buttonOk;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.dialog_admin, container);
    ButterKnife.bind(this, view);

    Utils.hideKeyboard(getActivity());

    CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {

      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
          rbReal.setChecked(rbReal == buttonView);
          rbTest.setChecked(rbTest == buttonView);
        }
      }
    };
    rbReal.setOnCheckedChangeListener(listener);
    rbTest.setOnCheckedChangeListener(listener);

    buttonCancel.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dismiss();
      }
    });
    buttonOk.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        boolean oldIsRealZone = SettingsCommon.isRealZone();
        if (oldIsRealZone && rbTest.isChecked()) {
          SettingsCommon.setZone(false);
          restart(getActivity());
        } else if (!oldIsRealZone && rbReal.isChecked()) {
          SettingsCommon.setZone(true);
          restart(getActivity());
        }
        dismiss();
      }
    });

    if (SettingsCommon.isRealZone()) rbReal.setChecked(true);
    else rbTest.setChecked(true);
    cbServiceInfo.setChecked(SettingsCommon.isVisibleServiceInfo());
    cbServiceInfo.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        SettingsCommon.setVisibleServiceInfo(cbServiceInfo.isChecked());
      }
    });
    return view;
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    Dialog dialog = super.onCreateDialog(savedInstanceState);
    Window window = dialog.getWindow();
    if (window != null) window.requestFeature(Window.FEATURE_NO_TITLE);
    return dialog;
  }

  public static void restart(final Activity activity) {
    Dialogs.startProcess(activity, "Перезапуск...");
    new Handler().postDelayed(new Runnable() {

      @Override
      public void run() {
        Dialogs.stopProcess();
        Intent mStartActivity = new Intent(activity, SplashScreenActivity.class);
        PendingIntent mPendingIntent = PendingIntent.getActivity(activity, 0, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 300, mPendingIntent);
        System.exit(0);
      }
    }, 2000);
  }
}