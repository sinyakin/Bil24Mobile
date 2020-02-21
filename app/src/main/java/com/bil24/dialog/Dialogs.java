package com.bil24.dialog;

import android.app.*;
import android.content.*;
import android.support.annotation.*;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import android.widget.*;

import com.bil24.*;
import com.bil24.myelement.MyLoadingPanel;
import com.bil24.utils.Utils;
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.*;
import com.rey.material.widget.SnackBar;

import server.net.*;
import server.net.listener.*;
import server.net.obj.*;

import java.util.ArrayList;
import java.util.List;

/**
 * User: SVV
 * Date: 28.05.2015.
 */
public class Dialogs {

  public static void showSnackBar(Activity activity, String text) {
    showSnackBar(activity, text, 1500);
  }

  public static void showSnackBar(Activity activity, String text, int duration) {
    SnackBar snackBar = SnackBar.make(activity);
    snackBar.applyStyle(R.style.Material_Widget_SnackBar_Tablet_MultiLine);
    snackBar.singleLine(false).actionText("Закрыть").actionClickListener(new SnackBar.OnActionClickListener() {
      @Override
      public void onActionClick(SnackBar snackBar, int i) {
        snackBar.dismiss();
      }
    });
    snackBar.duration(duration);
    snackBar.text(text);
    snackBar.show(activity);
  }

  public static void showSnackBarNotAutoClose(Activity activity, String text) {
    SnackBar snackBar = SnackBar.make(activity);
    snackBar.applyStyle(R.style.Material_Widget_SnackBar_Tablet_MultiLine);
    snackBar.singleLine(false).actionText("Закрыть").actionClickListener(new SnackBar.OnActionClickListener() {
      @Override
      public void onActionClick(SnackBar snackBar, int i) {
        snackBar.dismiss();
      }
    });
    snackBar.text(text);
    snackBar.show(activity);
  }

  public static void toastLong(String text) {
    Toast.makeText(Bil24Application.getContext(), text, Toast.LENGTH_LONG).show();
  }

  public static void toastShort(String text) {
    Toast.makeText(Bil24Application.getContext(), text, Toast.LENGTH_SHORT).show();
  }

  public static void alert(Context context, String msg) {
    alert(context, "", msg, null);
  }

  public static void alert(Context context, String msg, @Nullable final AlertCloseInterface alertCloseInterface) {
    alert(context, "", msg, alertCloseInterface);
  }

  public static void alert(Context context, String caption, String msg, @Nullable final AlertCloseInterface alertCloseInterface) {
    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    builder.setTitle(caption).setMessage(msg).setNegativeButton("ОК",
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            dialog.cancel();
          }
        }
    );
    builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
      @Override
      public void onCancel(DialogInterface dialogInterface) {
        if (alertCloseInterface != null) alertCloseInterface.close();
      }
    });
    /*AlertDialog alert = */builder.show();
//    TextView messageText = alert.findViewById(android.R.id.message);
    //messageText.setGravity(Gravity.CENTER);
//    alert.show();
  }

  public interface AlertCloseInterface {
    void close();
  }

  private static ProgressDialog progressDialog;
  public static void startProcess(Context context, String text) {
    progressDialog = ProgressDialog.show(context, "", text, true, false);
  }

  static void setTextProcessBar(String text) {
    if (progressDialog != null) progressDialog.setMessage(text);
  }

  public static void stopProcess() {
    if (progressDialog != null) progressDialog.dismiss();
  }

  public static void showUpdateDialog(android.support.v4.app.FragmentManager fragmentManager, String info, String url, boolean isUpdateOnly) {
    UpdateDialog dialog = new UpdateDialog();
    dialog.setData(info, url, !isUpdateOnly);
    dialog.show(fragmentManager, "");
  }

  public static void showCityDialog(android.support.v4.app.FragmentManager fragmentManager) {
    CityDialog dialog = new CityDialog();
    dialog.show(fragmentManager, "");
  }

  public static void showAdminDialog(android.support.v4.app.FragmentManager fragmentManager) {
    AdminDialog dialog = new AdminDialog();
    dialog.show(fragmentManager, "");
  }

  public static void showRmkDialog(android.support.v4.app.FragmentManager fragmentManager) {
    RmkDialog dialog = new RmkDialog();
    dialog.show(fragmentManager, "");
  }

  public static void showEmailConfirmationDialog(android.support.v4.app.FragmentManager fragmentManager, @Nullable String textInfo) {
    ConfirmationEmailDialog dialog = ConfirmationEmailDialog.create(textInfo);
    dialog.show(fragmentManager, "");
  }

  public static void showPrintCheckDialog(android.support.v4.app.FragmentManager fragmentManager, long orderId, int status) {
    PrintCheckDialog dialog = PrintCheckDialog.create(orderId, status);
    dialog.show(fragmentManager, "");
  }

  public static void showFullNameDialog(android.support.v4.app.FragmentManager fragmentManager) {
    FullNameDialog dialog = new FullNameDialog();
    dialog.show(fragmentManager, "");
  }

  public static void showKdpDialog(android.support.v4.app.FragmentManager fragmentManager, KdpDialog.KdpSuccessListener kdpSuccessListener, long actionId) {
    KdpDialog dialog = new KdpDialog();
    dialog.setData(kdpSuccessListener, actionId);
    dialog.show(fragmentManager, "");
  }

  private static FilterDialogFragment dialogFragment;
  public static synchronized void showFilterDialog(android.support.v4.app.FragmentManager fragmentManager, FilterDialogFragment.ChangeFilterListener changeFilterListener) {
    if (dialogFragment != null && dialogFragment.getDialog() != null && dialogFragment.getDialog().isShowing()) return;
    dialogFragment = new FilterDialogFragment();
    dialogFragment.setChangeFilterListener(changeFilterListener);
    dialogFragment.show(fragmentManager, "filterDialogFragment");
  }

  public static void showConfirmDialog(android.support.v4.app.FragmentManager fragmentManager, String text, final ConfirmListener confirmListener) {
    SimpleDialog.Builder builder = new SimpleDialog.Builder() {

      @Override
      public void onPositiveActionClicked(DialogFragment fragment) {
        confirmListener.positiveConfirm();
        super.onPositiveActionClicked(fragment);
      }

      @Override
      public void onNegativeActionClicked(DialogFragment fragment) {
        confirmListener.negativeConfirm();
        super.onNegativeActionClicked(fragment);
      }
    };
    builder.message(text).positiveAction("ОК").negativeAction("ОТМЕНА");
    DialogFragment dialogFragment = DialogFragment.newInstance(builder);
    dialogFragment.setCancelable(false);
    dialogFragment.show(fragmentManager, null);
  }

  public static void showInfoDialog(android.support.v4.app.FragmentManager fragmentManager, String title, String text) {
    SimpleDialog.Builder builder = new SimpleDialog.Builder() {

      @Override
      public void onPositiveActionClicked(DialogFragment fragment) {
        super.onPositiveActionClicked(fragment);
      }
    };
    builder.message(text).positiveAction("ОК").title(title);
    DialogFragment dialogFragment = DialogFragment.newInstance(builder);
    dialogFragment.setCancelable(false);
    dialogFragment.show(fragmentManager, null);
  }

  public static void showTicketsToEmailDialog(final Activity activity, android.support.v4.app.FragmentManager fragmentManager, final List<Long> ticketIdList) {
    SimpleDialog.Builder builder = new SimpleDialog.Builder() {
      MyLoadingPanel myLoadingPanel;
      EditText emailText;
      Button buttonOk;

      @Override
      public void onPositiveActionClicked(final DialogFragment fragment) {
        final String email = emailText.getText().toString().trim();
        if (email.isEmpty() || Utils.isValidEmail(email)) {
          Utils.hideKeyboard(activity);
          buttonOk.setEnabled(false);
          myLoadingPanel.setVisibility(View.VISIBLE);
          NetManager.sendTicketsToEmail(new SendTicketsToEmailListener() {
            @Override
            public void onSendTicketsToEmail(SendTicketsToEmailClient sendTicketsToEmailClient) {
              if (!fragment.isAdded()) return;
              myLoadingPanel.setVisibility(View.GONE);
              buttonOk.setEnabled(true);
              Dialogs.showSnackBar(activity, "Билеты отправлены на почту");
              fragment.dismiss();
            }

            @Override
            public void onSendTicketsToEmailFailed(NetException ex) {
              if (!fragment.isAdded()) return;
              myLoadingPanel.setVisibility(View.GONE);
              buttonOk.setEnabled(true);
              if (ex.isUserMessage()) Dialogs.showSnackBar(activity, ex.getMessage());
            }
          }, email, ticketIdList);
        } else {
          Dialogs.showSnackBar(activity, "неверный формат эл. почты");
        }
      }

      @Override
      public void onNegativeActionClicked(DialogFragment fragment) {
        Utils.hideKeyboard(activity);
        super.onNegativeActionClicked(fragment);
      }

      @Override
      protected void onBuildDone(Dialog dialog) {
        super.onBuildDone(dialog);
        emailText = ((EditText) dialog.findViewById(R.id.sendTicketToEmailEV));
        myLoadingPanel = ((MyLoadingPanel) dialog.findViewById(R.id.sendTicketToEmailLoadingPanel));
        buttonOk = (Button) dialog.findViewById(Dialog.ACTION_POSITIVE);
        myLoadingPanel.setVisibility(View.GONE);
        myLoadingPanel.goneTextView();
      }
    };

    builder.positiveAction("Отправить")
        .negativeAction("Отмена")
        .contentView(R.layout.send_ticket_to_email_view);
    DialogFragment dialogFragment = DialogFragment.newInstance(builder);
    dialogFragment.setCancelable(true);
    dialogFragment.show(fragmentManager, null);
  }

  public static void showCreateOrderRmkDialog(final Activity activity, android.support.v4.app.FragmentManager fragmentManager, final CreateOrderRmkListener createOrderRmkListener) {
    SimpleDialog.Builder builder = new SimpleDialog.Builder() {
      EditText emailText;
      EditText phoneText;

      @Override
      public void onPositiveActionClicked(final DialogFragment fragment) {
        String email = emailText.getText().toString().trim();
        String phone = phoneText.getText().toString().trim();
        if (email.isEmpty() && phone.isEmpty()) {
          Dialogs.showSnackBar(activity, "введите почту или телефон");
          return;
        }
        boolean validEmail = Utils.isValidEmail(email);
        boolean validPhone = phone.length() >= 7;
        if (!email.isEmpty() && !validEmail) {
          Dialogs.showSnackBar(activity, "неверный формат эл. почты");
          return;
        }
        if (!phone.isEmpty() && !validPhone) {
          Dialogs.showSnackBar(activity, "неверный формат телефона");
          return;
        }
        if (email.isEmpty()) email = null;
        if (phone.isEmpty()) phone = null;
        Utils.hideKeyboard(activity);
        if (createOrderRmkListener != null) createOrderRmkListener.onCreateOrderRmkSuccess(email, phone);
        fragment.dismiss();
      }

      @Override
      protected void onBuildDone(Dialog dialog) {
        super.onBuildDone(dialog);
        emailText = dialog.findViewById(R.id.createOrderViewRmkEmailTextView);
        phoneText = dialog.findViewById(R.id.createOrderViewRmkPhoneTextView);
      }
    };

    builder.positiveAction("Оплатить заказ")
        .title("Введите данные покупателя")
        .contentView(R.layout.create_order_view_rmk);
    DialogFragment dialogFragment = DialogFragment.newInstance(builder);
    dialogFragment.setCancelable(true);
    dialogFragment.show(fragmentManager, null);
  }

  public static void addPromoCodes(@NonNull final DialogAddPromoCodesListener dialogAddPromoCodesListener, final Activity activity, final android.support.v4.app.Fragment fragment, String promoCodes) {
    final List<String> promoCodesList;
    try {
      promoCodesList = getPromoCodeList(promoCodes);
    } catch (Exception ex) {
      Dialogs.alert(activity, "Промокод не соответствует формату. Возможные разделители - запятая, точка с запятой, пробел.");
      return;
    }

    if (promoCodesList.isEmpty()) {
      dialogAddPromoCodesListener.onSuccess(false);
      return;
    }

    String text = promoCodesList.size() == 1 ? "Добавление промокода..." : "Добавление промокодов...";
    Dialogs.startProcess(activity, text);
    NetManager.addPromoCodes(new AddPromoCodesListener() {
      @Override
      public void onAddPromoCodes(final AddPromoCodesClient clientData) {
        if (fragment != null && !fragment.isAdded()) return;
        if (fragment == null && activity.isFinishing()) return;
        Dialogs.stopProcess();
        if (clientData.getNewPromoCodeList().isEmpty() && clientData.getExistPromoCodeList().isEmpty()) {
          Dialogs.alert(activity, "Нет верных или действующих промокодов. Проверьте корректность ввода.");
        } else {
          StringBuilder text = new StringBuilder();
          List<String> resultList = new ArrayList<>();
          resultList.addAll(clientData.getNewPromoCodeList());
          resultList.addAll(clientData.getExistPromoCodeList());
          if (resultList.size() == 1) text.append("Промокод '");
          else text.append("Промокоды '");
          for (String promoCode : resultList) {
            text.append(promoCode).append(", ");
          }
          String resText = text.toString().substring(0, text.toString().length() - 2);
          Dialogs.alert(activity,
              resText + "' успешно " + (resultList.size() == 1 ? "добавлен" : "добавлены") + " в раздел Мои промокоды. При покупке билетов промокоды применяются автоматически. Размер скидки и промокод отображаются в корзине.",
              new AlertCloseInterface() {
                @Override
                public void close() {
                  dialogAddPromoCodesListener.onSuccess(!clientData.getNewPromoCodeList().isEmpty());
                }
              });
        }
      }

      @Override
      public void onAddPromoCodesFailed(NetException e) {
        if (fragment != null && !fragment.isAdded()) return;
        if (fragment == null && activity.isFinishing()) return;
        Dialogs.stopProcess();
        if (e.isUserMessage()) Dialogs.alert(activity, e.getMessage(), null);
        if (e.isUserMessageConfirmEmail()) Dialogs.showEmailConfirmationDialog(((AppCompatActivity)activity).getSupportFragmentManager(), e.getMessage());
      }
    }, promoCodesList);
  }

  private static List<String> getPromoCodeList(String promoCodes) {
    String[] tmp;
    if (promoCodes.contains(",")) {
      promoCodes = promoCodes.replace(";", ",");
      promoCodes = promoCodes.replace(" ", ",");
      tmp = promoCodes.split(",");
    } else if (promoCodes.contains(";")) {
      promoCodes = promoCodes.replace(" ", ";");
      tmp = promoCodes.split(";");
    } else {
      tmp = promoCodes.split(" ");
    }
    List<String> resultList = new ArrayList<>();
    for (String str : tmp) {
      String promoCode = str.trim();
      if (promoCode.isEmpty()) continue;
      resultList.add(promoCode);
    }
    return resultList;
  }


  public interface CreateOrderRmkListener {
    void onCreateOrderRmkSuccess(String email, String phone);
  }

  public interface DialogAddPromoCodesListener {
    void onSuccess(boolean newPromo);
  }
}