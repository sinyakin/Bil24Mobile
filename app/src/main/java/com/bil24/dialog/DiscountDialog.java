package com.bil24.dialog;

import android.annotation.SuppressLint;
import android.support.v4.app.FragmentManager;
import com.bil24.*;
import com.bil24.storage.FrontendType;
import com.bil24.utils.Utils;
import com.rey.material.app.*;
import com.rey.material.widget.Button;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by SVV on 16.08.2016
 */
public class DiscountDialog {
  private static final String WITHOUT_DISCOUNT = "Без скидки";
  private static List<String> ticketOfficeList = new ArrayList<>();
  private static List<String> invitationList = Collections.singletonList("100%");

  private SimpleDialog.Builder builder;
  private Button button;

  static {
    ticketOfficeList.add(WITHOUT_DISCOUNT);
    ticketOfficeList.add("5%");
    ticketOfficeList.add("10%");
    ticketOfficeList.add("15%");
    ticketOfficeList.add("20%");
    ticketOfficeList.add("25%");
    ticketOfficeList.add("30%");
    ticketOfficeList.add("35%");
    ticketOfficeList.add("40%");
    ticketOfficeList.add("45%");
    ticketOfficeList.add("50%");
    for (int i = 1; i <= 50; i++) {
      ticketOfficeList.add(i + "%");
    }
  }

  public DiscountDialog(final DiscountListener discountListener, final BigDecimal totalSum) {
    FrontendType frontendType = Controller.getInstance().getFrontendData(Bil24Application.getContext()).getFrontendType();
    List<String> itemList = new ArrayList<>();
    if (frontendType == FrontendType.TICKET_OFFICE) {
      itemList = ticketOfficeList;
    } else if (frontendType == FrontendType.INVITATION) {
      itemList = invitationList;
    }

    builder = new SimpleDialog.Builder() {

      @Override
      protected void onBuildDone(Dialog dialog) {
        button = (Button) dialog.findViewById(Dialog.ACTION_POSITIVE);
        super.onBuildDone(dialog);
      }

      @SuppressLint("SetTextI18n")
      @Override
      public void onSelectionChanged(int index, boolean selected) {
        BigDecimal discount = getDiscount(getSelectedValue().toString());
        BigDecimal total = totalSum;
        if (discount != null) {
          total = totalSum.subtract(totalSum.multiply(discount).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP));
        }
        button.setText("OK (" + Utils.formatedRub(total) + ")");
        super.onSelectionChanged(index, selected);
      }

      @Override
      public void onPositiveActionClicked(DialogFragment fragment) {
        BigDecimal discount = getDiscount(getSelectedValue().toString());
        if (discountListener != null) discountListener.onDiscount(discount);
        super.onPositiveActionClicked(fragment);


      }

      @Override
      public void onNegativeActionClicked(DialogFragment fragment) {
        super.onNegativeActionClicked(fragment);
      }
    };

    String[] items = new String[itemList.size()];
    items = itemList.toArray(items);
    builder.items(items, 0)
        .title("Установите скидку")
        .positiveAction("OK (" + Utils.formatedRub(totalSum) + ")")
        .negativeAction("ОТМЕНА");
  }

  private BigDecimal getDiscount(String discountString) {
    try {
      if (!discountString.equals(WITHOUT_DISCOUNT)) {
        discountString = discountString.replace("%", "");
        return new BigDecimal(discountString);
      }
    } catch (Exception ignored) {}
    return null;
  }

  public void show(FragmentManager fragmentManager) {
    DialogFragment fragment = DialogFragment.newInstance(builder);
    fragment.show(fragmentManager, null);
  }

  public interface DiscountListener {
    void onDiscount(BigDecimal discount);
  }
}