package com.bil24.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

import com.bil24.Kassatka;
import com.bil24.R;
import com.bil24.myelement.MyLoadingPanel;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.kassatka.comepay_sdk.callBack.CallBackCloseCheck;
import ru.kassatka.comepay_sdk.callBack.StatusPrint;
import server.en.OrderStatus;
import server.net.NetException;
import server.net.NetManager;
import server.net.listener.GetOrderInfoListener;
import server.net.obj.GetOrderInfoClient;

/**
 * Created by SVV on 01.09.2016
 */
public class PrintCheckDialog extends DialogFragment implements GetOrderInfoListener {
  private static final String ORDER_ID = "ORDER_ID";
  private static final String ORDER_STATUS = "ORDER_STATUS";

  @BindView(R.id.dialogPrintRootPanel)
  FrameLayout rootPanel;
  @BindView(R.id.dialogPrintLoadingPanel)
  MyLoadingPanel loadingPanel;

  private long orderId;
  private OrderStatus status;

  public static PrintCheckDialog create(long orderId, int status) {
    PrintCheckDialog dialog = new PrintCheckDialog();
    Bundle bundle = new Bundle();
    bundle.putLong(ORDER_ID, orderId);
    bundle.putInt(ORDER_STATUS, status);
    dialog.setArguments(bundle);
    return dialog;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.dialog_print_check, container);
    ButterKnife.bind(this, view);

    orderId = getArguments().getLong(ORDER_ID, 0);
    status = OrderStatus.getStatusById(getArguments().getInt(ORDER_STATUS, 0));

    loadingPanel.setText("Проверка статуса заказа...");

    getOrderInfo(0);
    return view;
  }

  private void checkStatus(@Nullable GetOrderInfoClient clientData, int delayGetOrderInfo) {
    if (status == OrderStatus.PAID) {
      //печать чека
      loadingPanel.setText("Печать чека...");
      new Handler().postDelayed(() -> {
        //todo
        Kassatka.print(getContext(), clientData, new CallBackCloseCheck() {
          @Override
          public void getStatus(StatusPrint statusPrint) {
            loadingPanel.setText("Результат печати " + statusPrint.id + "(id) " + statusPrint.checkID + "(checkId) " + statusPrint.status + "(status) ");
            new Handler().postDelayed(new Runnable() {
              @Override
              public void run() {
                PrintCheckDialog.this.dismiss();
              }
            }, 3000);
          }
        });
      }, 500);
    } else {
      //проверка статуса чека
      getOrderInfo(delayGetOrderInfo);
    }
  }

  private void getOrderInfo(int delay) {
    new Handler().postDelayed(() -> NetManager.getOrderInfo(PrintCheckDialog.this, orderId), delay);
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

  @Override
  public void onGetOrderInfo(GetOrderInfoClient clientData) {
    if (!isAdded()) return;
    status = OrderStatus.getStatusById(clientData.getStatusExtInt());
    checkStatus(clientData, 3000);
  }

  @Override
  public void onGetOrderInfoFailed(NetException e) {
    if (!isAdded()) return;
    checkStatus(null, 3000);
  }
}