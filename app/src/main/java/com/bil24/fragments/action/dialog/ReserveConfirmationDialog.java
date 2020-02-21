package com.bil24.fragments.action.dialog;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import com.bil24.Controller;
import com.bil24.dialog.Dialogs;
import com.bil24.storage.Settings;
import com.bil24.utils.Utils;
import com.rey.material.app.*;
import com.rey.material.widget.Button;
import server.net.*;
import server.net.listener.ReservationListener;
import server.net.obj.ReservationClient;
import server.net.obj.extra.ExtraCategoryPriceExt;

import java.math.BigDecimal;
import java.util.*;

/**
 * Виктор
 * 11.08.2015.
 */
@SuppressLint("ParcelCreator")
public class ReserveConfirmationDialog extends SimpleDialog.Builder implements ReservationListener {

  public static void showMEC(Fragment fragment, Button buttonReserve, ExtraCategoryPriceExt categoryPriceExt, long actionId) {
    show(fragment, buttonReserve, Collections.singletonMap(categoryPriceExt, 1), null, actionId, true);
  }

  public static void show(Fragment fragment, Button buttonReserve, @NonNull Map<ExtraCategoryPriceExt, Integer> categoryQuantityMap, SuccessReservationListener successReservationListener, long actionId, boolean mec) {
    DialogFragment dialogFragment = DialogFragment.newInstance(new ReserveConfirmationDialog(fragment, buttonReserve, categoryQuantityMap, successReservationListener, actionId, mec));
    dialogFragment.show(fragment.getFragmentManager(), null);
  }

  private Fragment fragment;
  private com.rey.material.widget.Button buttonReserve;
  private Map<Long, Integer> categoryQuantityMap = new HashMap<>();
  private BigDecimal totalSum = BigDecimal.ZERO;
  private Integer totalQuantity = 0;
  private SuccessReservationListener successReservationListener;
  private long actionId;
  private boolean mec = false;

  private ReserveConfirmationDialog(Fragment fragment, Button buttonReserve, @NonNull Map<ExtraCategoryPriceExt, Integer> categoryQuantityMap, SuccessReservationListener successReservationListener, long actionId, boolean mec) {
    this.fragment = fragment;
    this.buttonReserve = buttonReserve;
    this.successReservationListener = successReservationListener;
    this.actionId = actionId;
    this.mec = mec;
    StringBuilder builder = new StringBuilder();
    for (Map.Entry<ExtraCategoryPriceExt, Integer> entry : categoryQuantityMap.entrySet()) {
      ExtraCategoryPriceExt categoryPrice = entry.getKey();
      Integer quantity = entry.getValue();
      builder.append(categoryPrice.getCategoryPriceName()).append(" - ").append(quantity).append("\n");
      totalSum = totalSum.add(BigDecimal.valueOf(quantity).multiply(categoryPrice.getPrice()));
      totalQuantity += quantity;
      this.categoryQuantityMap.put(categoryPrice.getCategoryPriceId(), quantity);
    }

    String text = "Итого билетов: ";
    if (mec) text = "Итого карт: ";

    message("Зарезервировать: \n" + builder.toString() + text + totalQuantity + " на сумму " + Utils.sumFormat.format(totalSum) + " руб.")
        .title("Подтвердите выбор")
        .positiveAction("ОК")
        .negativeAction("ОТМЕНА");
  }

  @Override
  public void onPositiveActionClicked(DialogFragment fragment) {
    buttonReserve.setEnabled(false);
    NetManager.reserve(this, categoryQuantityMap, Settings.getActionIdKdp(actionId));
    super.onPositiveActionClicked(fragment);
  }

  @Override
  public void onNegativeActionClicked(DialogFragment fragment) {
    buttonReserve.setEnabled(true);
    super.onNegativeActionClicked(fragment);
  }

  @Override
  public void onReservation(ReservationClient clientData) {
    if (!fragment.isAdded()) return;
    Settings.addSeatInReserve(totalQuantity);
    Controller.getInstance().showBasketFragment();
    buttonReserve.setEnabled(true);
    if (successReservationListener != null) successReservationListener.onSuccessReservation();
    String text = "Зарезервировано билетов: ";
    if (mec) text = "Зарезервировано карт: ";
    Dialogs.showSnackBar(fragment.getActivity(), text + totalQuantity + " на сумму " + Utils.sumFormat.format(totalSum) + " руб.");
  }

  @Override
  public void onReservationFailed(NetException e) {
    if (!fragment.isAdded()) return;
    buttonReserve.setEnabled(true);
    if (e.isUserMessageConfirmEmail()) Dialogs.showEmailConfirmationDialog(fragment.getFragmentManager(), e.getMessage());
    else if (e.isUserMessage()) Dialogs.showSnackBar(fragment.getActivity(), e.getMessage());
  }

  public interface SuccessReservationListener {
    void onSuccessReservation();
  }
}