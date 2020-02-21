package com.bil24.dialog;

import android.app.*;
import android.content.*;
import android.os.*;
import android.support.annotation.*;
import android.support.v4.app.DialogFragment;
import android.view.*;
import android.widget.*;
import butterknife.*;
import com.bil24.*;
import com.bil24.R;
import com.bil24.activity.SplashScreenActivity;
import com.bil24.myelement.*;
import com.bil24.storage.*;
import com.bil24.storage.sql.DataBase;
import com.bil24.utils.*;
import server.net.*;
import server.net.listener.*;
import server.net.obj.*;

/**
 * Created by SVV on 01.09.2016
 */
public class ConfirmationEmailDialog extends DialogFragment implements BindEmailListener, ConfirmEmailListener {
  private static final String TEXT_INFO = "EXTRA_TEXT_INFO";

  @BindView(R.id.confirmationEmailDialogRootPanel)
  FrameLayout rootPanel;
  @BindView(R.id.confirmationEmailDialogPanel1)
  RelativeLayout panel1;
  @BindView(R.id.confirmationEmailDialogEditTextEmail)
  EditText editTextEmail;
  @BindView(R.id.confirmationEmailDialogButtonBindEmail)
  com.rey.material.widget.Button buttonBindEmail;
  @BindView(R.id.confirmationEmailDialogButtonCancel1)
  com.rey.material.widget.Button buttonCancel1;

  @BindView(R.id.confirmationEmailDialogPanel2)
  RelativeLayout panel2;
  @BindView(R.id.confirmationEmailDialogEditTextCode)
  EditText editTextCode;
  @BindView(R.id.confirmationEmailDialogButtonConfirmEmail)
  com.rey.material.widget.Button buttonConfirmEmail;
  @BindView(R.id.confirmationEmailDialogButtonCancel2)
  com.rey.material.widget.Button buttonCancel2;

  @BindView(R.id.confirmationEmailDialogLoadingPanel)
  MyLoadingPanel loadingPanel;

  @BindView(R.id.confirmationEmailDialogTextView)
  com.rey.material.widget.TextView tvInfo;

  public static ConfirmationEmailDialog create(@Nullable  String textInfo) {
    ConfirmationEmailDialog confirmationEmailDialog = new ConfirmationEmailDialog();
    Bundle bundle = new Bundle();
    bundle.putString(TEXT_INFO, textInfo);
    confirmationEmailDialog.setArguments(bundle);
    return confirmationEmailDialog;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.dialog_confirmation_email, container);
    ButterKnife.bind(this, view);

    String textInfo = getArguments().getString(TEXT_INFO, null);
    if (textInfo != null) tvInfo.setText(textInfo);

    step1();
    buttonBindEmail.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        final String email = editTextEmail.getText().toString().trim();
        if (Utils.isValidEmail(email)) {
          Session session = DataBase.getSession(getContext());
          String oldEmail = session.getEmail();
          if (oldEmail.equals(email)) {
            Dialogs.toastLong("Эта почта уже подтверждена");
            editTextEmail.selectAll();
          } else {
            Utils.hideKeyboard(getActivity());
            step2("проверка эл. почты...");
            NetManager.bindEmail(ConfirmationEmailDialog.this, email);
          }
        } else {
          Dialogs.showSnackBar(getActivity(), "неверный формат эл. почты");
        }
      }
    });

    buttonCancel1.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dismiss();
      }
    });

    buttonCancel2.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dismiss();
      }
    });

    buttonConfirmEmail.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String code = editTextCode.getText().toString().trim();
        try {
          Utils.hideKeyboard(getActivity());
          NetManager.confirmEmail(ConfirmationEmailDialog.this, Integer.parseInt(code));
          step2("проверка кода...");
        } catch (Exception ex) {
          Dialogs.showSnackBar(getActivity(), "неверный формат кода");
        }
      }
    });

    return view;
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    Dialog dialog = super.onCreateDialog(savedInstanceState);
    dialog.setCancelable(false);
    Window window = dialog.getWindow();
    if (window != null) window.requestFeature(Window.FEATURE_NO_TITLE);
    return dialog;
  }

  private void step1() {
    loadingPanel.setVisibility(View.GONE);
    panel1.setVisibility(View.VISIBLE);
    panel2.setVisibility(View.GONE);
  }

  private void step2(String text) {
    loadingPanel.setVisibility(View.VISIBLE);
    loadingPanel.setText(text);
    panel1.setVisibility(View.GONE);
    panel2.setVisibility(View.GONE);
  }

  private void step3() {
    loadingPanel.setVisibility(View.GONE);
    panel1.setVisibility(View.GONE);
    panel2.setVisibility(View.VISIBLE);
    editTextCode.requestFocus();
  }

  @Override
  public void onBindEmail(BindEmailClient clientData) {
    if (!isAdded()) return;
    step3();
  }

  @Override
  public void onBindEmailFailed(NetException ex) {
    if (!isAdded()) return;
    step1();
    if (ex.isUserMessage()) Dialogs.toastLong(ex.getMessage());
  }

  @Override
  public void onConfirmEmail(ConfirmEmailClient clientData) {
    if (!isAdded()) return;
    //перезагрузить устройство с новыми данными
    restart(getActivity(), clientData.getUserId(), clientData.getSessionId(), clientData.getEmail());
    Utils.hideKeyboard(getActivity());
    dismiss();
  }

  @Override
  public void onConfirmEmailFailed(NetException ex) {
    if (!isAdded()) return;
    step3();
    if (ex.isUserMessage()) Dialogs.toastLong(ex.getMessage());
  }

  private static void restart(final Activity activity, final long userId, final String sessionId, @NonNull final String email) {
    try {
      DataBase db = DataBase.getInstance(activity);
      Session session = db.mainData.getSession();
      String oldEmail = session.getEmail();
      if (session.getUserId() != userId) {
        if (!db.removeUserData()) throw new Exception("error while remove user data");
        if (!db.mainData.updateSession(userId, sessionId)) throw new Exception("error while updateSession");
        if (!db.mainData.updateEmail(email)) throw new Exception("error while updateEmail");
        //сохраним данные для переключения между зонами
        LastUserData lastUserData = Settings.getLastUserData();
        Settings.removeAllData();
        Settings.setLastUserData(lastUserData);
      }
      Settings.setNeedSentToEmail(true);
      if (!email.equals(oldEmail)) Controller.getInstance().resetLeftMenu();
      Controller.getInstance().changeEmail(email);
      Dialogs.showSnackBar(activity, "Почта успешно подтверждена");
    } catch (Exception ex) {
      MyUncaughtExceptionHandler.sendError(ex);
    }
  }
}