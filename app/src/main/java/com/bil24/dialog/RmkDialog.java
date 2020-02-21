package com.bil24.dialog;

import android.app.*;
import android.content.*;
import android.os.*;
import android.support.annotation.*;
import android.support.v4.app.DialogFragment;
import android.view.*;
import android.widget.EditText;
import android.widget.RelativeLayout;

import butterknife.*;

import com.bil24.*;
import com.bil24.R;
import com.bil24.activity.*;
import com.bil24.gcm.*;
import com.bil24.storage.*;
import com.bil24.storage.sql.*;
import com.bil24.utils.Utils;

import server.net.*;
import server.net.listener.*;
import server.net.obj.*;

public class RmkDialog extends DialogFragment implements AuthFrontendListener {
  private static final String TAG = "RmkDialog";

  @BindView(R.id.dialogRmkFidPanel)
  RelativeLayout fidPanel;
  @BindView(R.id.dialogRmkFidTVInfo)
  com.rey.material.widget.TextView fidTVInfo;
  @BindView(R.id.dialogRmkFidET)
  EditText fidET;
  @BindView(R.id.dialogRmkFidETToken)
  EditText fidETToken;

  @BindView(R.id.rmkDialogButtonCancel)
  com.rey.material.widget.Button buttonCancel;
  @BindView(R.id.rmkDialogButtonOk)
  com.rey.material.widget.Button buttonOk;
  @BindView(R.id.rmkDialogButtonDefault)
  com.rey.material.widget.Button buttonResetFid;

  private FrontendData oldFrontendData = FrontendData.getDefault();
  private long newFid;
  private String newToken;
  private int newFrontendType;
  private boolean defaultFid = false;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.dialog_rmk, container);
    ButterKnife.bind(this, view);
    Utils.hideKeyboard(getActivity());

    oldFrontendData = Controller.getInstance().getFrontendData(getContext());

    String yourFir = getString(R.string.fid);
    fidTVInfo.setText(String.format(yourFir, oldFrontendData.getFid() + ""));
    if (Controller.getInstance().isDefaultFrontend(getContext())) {
      fidPanel.setVisibility(View.VISIBLE);
      buttonResetFid.setVisibility(View.GONE);
    } else {
      fidPanel.setVisibility(View.GONE);
      buttonResetFid.setVisibility(View.VISIBLE);
    }

    if (Controller.getInstance().changeFrontendTypeInKassatka(getContext())) {
      buttonCancel.setVisibility(View.GONE);
    } else {
      buttonCancel.setVisibility(View.VISIBLE);
      buttonCancel.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          dismiss();
        }
      });
    }

    buttonOk.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        changeFid();
      }
    });

    buttonResetFid.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        defaultFid();
      }
    });
    return view;
  }

  private void defaultFid() {
    Dialogs.showConfirmDialog(getFragmentManager(), "Вернуться к стандартному FID?", new ConfirmListener() {
      @Override
      public void positiveConfirm() {
        Dialogs.startProcess(getActivity(), "Настройка приложения...");
        FrontendData frontendData = DataBase.getInstance(getContext()).mainData.getFrontendData();
        newFid = frontendData.getDefaultFid();
        newToken = frontendData.getDefaultToken();
        newFrontendType = frontendData.getDefaultFrontendType().getId();
        defaultFid = true;
        restartGCM();
      }

      @Override
      public void negativeConfirm() {
      }
    });
  }

  private void changeFid() {
    try {
      Utils.hideKeyboard(getActivity());
      long fid = Long.valueOf(fidET.getText().toString());
      String token = fidETToken.getText().toString();

      if (fid == 0 || token.isEmpty()) {
        failChangeToken("Заполните поля newFid и token");
        return;
      }

      if (fid == oldFrontendData.getFid()) {
        failChangeToken("Вы уже привязаны к newFid " + fid);
        return;
      }

      this.newFid = fid;
      this.newToken = token;

      fidET.setEnabled(false);
      fidETToken.setEnabled(false);
      NetManager.authFrontend(RmkDialog.this, fid, token);
      Dialogs.startProcess(getContext(), "Проверка данных...");
    } catch (Exception ex) {
      failChangeToken("Заполните поля newFid и token");
    }
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    Dialog dialog = super.onCreateDialog(savedInstanceState);
    Window window = dialog.getWindow();
    if (window != null) window.requestFeature(Window.FEATURE_NO_TITLE);
    setCancelable(!Controller.getInstance().changeFrontendTypeInKassatka(getContext()));
    return dialog;
  }

  @Override
  public void onAuthFrontend(final AuthFrontendClient clientData) {
    if (!isAdded()) return;
    this.newFrontendType = clientData.getType();
    restartGCM();
  }

  @Override
  public void onAuthFrontendFailed(NetException ex) {
    if (!isAdded()) return;
    Dialogs.stopProcess();
    String message = ex.getMessage();
    if (ex.isAuthFrontendFail()) message = "неверный newFid или token";
    failChangeToken(message);
  }

  private void failChangeToken(String text) {
    fidET.setEnabled(true);
    fidETToken.setEnabled(true);
    Dialogs.toastShort(text);
  }

  private void restartGCM() {
    Dialogs.setTextProcessBar("Настройка приложения...");

    if (Utils.checkPlayServices(getContext(), getActivity())) {
      //отписать со старого фида
      new GCMDoRequest().execute(new GCMRequest(getContext(), oldFrontendData.getFid(), GCMCommand.UN_SUBSCRIBE, new GCMResponseListener() {
        @Override
        public void onGCMSuccess(GCMCommand command, String token) {
          //подписать на новый фид
          new GCMDoRequest().execute(new GCMRequest(getContext(), newFid, GCMCommand.SUBSCRIBE, new GCMResponseListener() {
            @Override
            public void onGCMSuccess(GCMCommand command, String token) {
              if (!RmkDialog.this.isAdded()) return;
              Dialogs.stopProcess();
              restartApplication(getActivity());
            }

            @Override
            public void onGCMFail(GCMCommand command) {
              failChangeToken("Не удалось перепривязать newFid. Попробуйте позже");
            }
          }));
        }

        @Override
        public void onGCMFail(GCMCommand command) {
          failChangeToken("Не удалось перепривязать newFid. Попробуйте позже");
        }
      }));
    } else {
      Dialogs.stopProcess();
      restartApplication(getActivity());
    }
  }

  private void restartApplication(final Activity activity) {
    Dialogs.startProcess(activity, "Перезапуск...");
    try {
      DataBase db = DataBase.getInstance(activity);
      Session session = db.mainData.getSession();
      LastUserData newLastUserData = Settings.getLastUserData();
      LastUserData savingLastUserData = new LastUserData(session.getUserId(), session.getSessionId(), session.getEmail());
      if (!db.removeUserData()) throw new Exception("error while remove user data");
      if (!db.mainData.updateSession(newLastUserData.getUserId(), newLastUserData.getSessionId()))
        throw new Exception("error while updateSession");
      if (!db.mainData.updateEmail(newLastUserData.getEmail()))
        throw new Exception("error while updateEmail");
      Settings.removeAllData();
      Settings.setLastUserData(savingLastUserData);
      Settings.setNeedSentToEmail(true);
      if (!defaultFid) db.mainData.setNewFrontendData(newFid, newToken, FrontendType.get(newFrontendType));
      else db.mainData.resetNewFrontendData();
    } catch (Exception ex) {
      MyUncaughtExceptionHandler.sendError(ex);
      Dialogs.stopProcess();
      return;
    }

    new Handler().postDelayed(new Runnable() {

      @Override
      public void run() {
        try {
          Dialogs.stopProcess();
          Intent mStartActivity = new Intent(activity, SplashScreenActivity.class);
          PendingIntent mPendingIntent = PendingIntent.getActivity(activity, 0, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
          AlarmManager mgr = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
          mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 300, mPendingIntent);
          System.exit(0);
        } catch (Exception ex) {
          MyUncaughtExceptionHandler.sendError(ex);
          Dialogs.stopProcess();
        }
      }
    }, 2000);
  }
}