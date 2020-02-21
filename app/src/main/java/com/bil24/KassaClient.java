package com.bil24;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import java.util.ArrayList;

import ru.kassatka.comepay_sdk.PrintCommand;
import ru.kassatka.comepay_sdk.ShiftCommand;
import ru.kassatka.comepay_sdk.callBack.CallBackCloseCheck;
import ru.kassatka.comepay_sdk.check.CheckCommand;
import ru.kassatka.comepay_sdk.model.Extra;
import ru.kassatka.comepay_sdk.model.ProductItems;

public class KassaClient {
  private Context m_context;
  ProductItems.TaxMode tax_type;

  public KassaClient(Context c) {
    m_context = c;
  }

  public void getXReport() {
    ShiftCommand command = new ShiftCommand();
    Extra extra = new Extra();
    extra.putExtra("ACTION", "X_REPORT");
    command.PrintXReport(extra);
    command.startCommand(m_context);
  }

  // Печать баркода
  public void printBarcode(String code) {
    m_context.sendBroadcast(new Intent("ru.kassa.service.print.receivers.EAN13Receiver.PRINT_EAN13").putExtra("EAN_13", code));
    m_context.sendBroadcast(new Intent("ru.kassa.service.print.receivers.CUT_PAPER"));

  }

  // Печать пустых строк
  public void printEmpty(int count) {
    PrintCommand command = new PrintCommand();
    Extra extra = new Extra();
    extra.putExtra("ACTION", "PRINT_EMPTY");
    extra.putExtra("COUNT", count); //Количество строк
    command.printEmtyString(extra);
    command.startCommand(m_context);
    m_context.sendBroadcast(new Intent("ru.kassa.service.print.receivers.CUT_PAPER"));
  }

  public void printText(String text) {
    PrintCommand command = new PrintCommand();
    Extra extra = new Extra();
    extra.putExtra("ACTION", "PRINT_STRING");
    extra.putExtra("STRING", text);
    command.printEmtyString(extra);
    command.startCommand(m_context);

    m_context.sendBroadcast(new Intent("ru.kassa.service.print.receivers.CUT_PAPER"));
  }

  public void printLine(String type) {
    String lineText;
    if (type.equals("middle"))
      lineText = "----------------------------------------------";
    else
      lineText = "______________________________________________";

    lineText = "**********************************************";
    PrintCommand command = new PrintCommand();
    Extra extra = new Extra();
    extra.putExtra("ACTION", "PRINT_STRING");
    extra.putExtra("STRING", lineText);
    command.printString(extra);
    command.startCommand(m_context);

    m_context.sendBroadcast(new Intent("ru.kassa.service.print.receivers.CUT_PAPER"));
  }

  public void setOrderTaxation(int tax) {
    switch (tax) {
      case 1:
        tax_type = ProductItems.TaxMode.COMMON;
        break;
      case 2:
        tax_type = ProductItems.TaxMode.SINGLE_IMPUTED_INCOME;
        break;
      case 3:
        tax_type = ProductItems.TaxMode.SINGLE_AGRICULTURE;
        break;
      case 4:
        tax_type = ProductItems.TaxMode.PATENT;
        break;
      case 5:
        tax_type = ProductItems.TaxMode.SIMPLIFIED_INCOME;
        break;
      case 6:
        tax_type = ProductItems.TaxMode.SIMPLIFIED_INCOME_OUTCOME;
        break;
    }
  }

  public void lineFeed() {
    PrintCommand command = new PrintCommand();
    Extra extra = new Extra();
    extra.putExtra("ACTION", "PRINT_STRING");
    extra.putExtra("STRING", "");
    command.printEmtyString(extra);
    command.startCommand(m_context);

    m_context.sendBroadcast(new Intent("ru.kassa.service.print.receivers.CUT_PAPER"));
  }

  public void setClient(String contact, String Name, String INN) {
    CheckCommand command = new CheckCommand();
    Extra extra = new Extra();
    extra.putExtra("ACTION", "ADD_CUSTOMER");
    if (contact.contains("@")) {
      extra.putExtra("EMAIL", contact);
    } else {
      extra.putExtra("PHONE", contact);
    }
    extra.putExtra("NAME", Name);
    extra.putExtra("INN", INN);
    command.addExtraCommand(extra);
    command.startCommand(m_context);
  }

  // 1 - sell, 2 - sell_return
  public void openReceipt(int type, @Nullable String organisationName,
                          @Nullable String organisationAddress,
                          @Nullable String organisationInn) {
    CheckCommand command = new CheckCommand();
    Extra extra = new Extra();
    extra.putExtra("ACTION", "CREATE_CHECK");
    if (type == 1) {
      extra.putExtra("DOC_TYPE", "SALE");
    } else {
      extra.putExtra("DOC_TYPE", "RETURN");
    }
    if (organisationName != null) {
      extra.putExtra("ORGANISATION_NAME", organisationName);
    }
    if (organisationAddress != null) {
      extra.putExtra("ORGANISATION_ADDRESS", organisationAddress);
    }
    if (organisationInn != null) {
      extra.putExtra("ORGANISATION_INN", organisationInn);
    }
    command.openCheckCommand(extra);
    command.startCommand(m_context);
  }

  public void addCheckPayment(int type, double sum) {
    CheckCommand command = new CheckCommand();
    Extra extra = new Extra();
    extra.putExtra("ACTION", "ADD_PAYMENT");
    if (type == 1) {
      extra.putExtra("CASH", sum * 100);
    } else if (type == 2) {
      extra.putExtra("CASHLESS", sum * 100);
    } else if (type == 3) {
      extra.putExtra("ADVANCE_PAYMENT", sum * 100);//АВАНС, ПРЕДОПЛАТА
    } else if (type == 4) {
      extra.putExtra("CREDIT", sum * 100);
    } else if (type == 5) {
      extra.putExtra("CONSIDERATION", sum * 100);
    }
    command.CloseCheckCommand(extra);
    command.startCommand(m_context);
  }

  public void closeReceipt(CallBackCloseCheck callBackCloseCheck) {
    CheckCommand command = new CheckCommand();
    Extra extra = new Extra();
    extra.putExtra("ACTION", "CLOSE_CHECK");
    extra.putExtra("PAY_TYPE", "CASH");
    command.addExtraCommand(extra);
    command.startCommand(m_context, callBackCloseCheck);
  }


  //Отмена чека, очистка корзины с товарами
  public void cancelReceipt() {
    CheckCommand command = new CheckCommand();
    Extra extra = new Extra();
    extra.putExtra("ACTION", "CANCEL");
    command.CloseCheckCommand(extra);
    command.startCommand(m_context);
  }

  //Внесение
  public void cashOutcome(Double val) {
    CheckCommand command = new CheckCommand();
    Extra extra = new Extra();
    extra.putExtra("ACTION", "REMOVE_MONEY");
    extra.putExtra("MONEY", val);
    command.addExtraCommand(extra);
    command.startCommand(m_context);
  }

  //Изъятие
  public int cashIncome(Double val) {
    CheckCommand command = new CheckCommand();
    Extra extra = new Extra();
    extra.putExtra("ACTION", "ADD_MONEY");
    extra.putExtra("MONEY", val);
    command.addExtraCommand(extra);
    command.startCommand(m_context);
    return 0;
  }

  public void closeShift() {
    ShiftCommand command = new ShiftCommand();
    Extra extra = new Extra();
    extra.putExtra("ACTION", "SHIFT_CLOSE");
    command.getStatus(extra);
    command.startCommand(m_context);
  }

  public void addPosition(String name, double price, double qty, int vat, int pay_method, int pay_subj) {
    CheckCommand command = new CheckCommand();
    Extra extra = new Extra();
    extra.putExtra("ACTION", "ADD_ITEM");
    ArrayList<ProductItems> changes = new ArrayList<>();
    ProductItems productItems = new ProductItems();
    productItems.name = name;
    productItems.count = (int) qty * 1000;
    productItems.price = price * 100;
    productItems.taxMode = tax_type;

    productItems.payAttributesType = setPayAtribute(pay_method);
    productItems.vatType = setVatType(vat);
    productItems.goodType = setGoodType(pay_subj);
    changes.add(productItems);
    command.addItemsCommand(changes, extra);
    command.startCommand(m_context);
  }

  private ProductItems.GoodAttributesType setGoodType(int type) {
    switch (type) {
      case 1:
        return ProductItems.GoodAttributesType.PRODUCT;
      case 2:
        return ProductItems.GoodAttributesType.EXCISABLE_GOODS;
      case 3:
        return ProductItems.GoodAttributesType.WORK;
      case 4:
        return ProductItems.GoodAttributesType.SERVICE;
      case 5:
        return ProductItems.GoodAttributesType.BET_GAMBLING;
      case 6:
        return ProductItems.GoodAttributesType.WIN_GAMBLING;
      case 7:
        return ProductItems.GoodAttributesType.LOTTERY_TICKET;
      case 8:
        return ProductItems.GoodAttributesType.WINNING_LOTTERY;
      case 9:
        return ProductItems.GoodAttributesType.INTELLECTUAL_PROPERTY_RESULTS;
      case 10:
        return ProductItems.GoodAttributesType.PAYMENT_OR_PAYOUT;
      case 11:
        return ProductItems.GoodAttributesType.AGENT_S_COMMISSION;
      case 12:
        return ProductItems.GoodAttributesType.COMPOSITE;
      case 13:
        return ProductItems.GoodAttributesType.OTHER;
    }
    return ProductItems.GoodAttributesType.PRODUCT;
  }

  private ProductItems.VatType setVatType(int type) {
    switch (type) {
      case 1:
        return ProductItems.VatType.NONE;

      case 2:
        return ProductItems.VatType.VAT_0;

      case 3:
        return ProductItems.VatType.VAT_10;

      case 4:
        return ProductItems.VatType.VAT_20;

      case 5:
        return ProductItems.VatType.VAT_20;

      case 6:
        return ProductItems.VatType.VAT_10_110;

      case 7:
        return ProductItems.VatType.VAT_20_120;

      case 8:
        return ProductItems.VatType.VAT_20_120;

    }
    return ProductItems.VatType.NONE;

  }

  private ProductItems.PayAttributesType setPayAtribute(int type) {
    switch (type) {
      case 1:
        return ProductItems.PayAttributesType.PREPAYMENT_100;

      case 2:
        return ProductItems.PayAttributesType.PREPAYMENT;

      case 3:
        return ProductItems.PayAttributesType.ADVANCE;

      case 4:
        return ProductItems.PayAttributesType.FULL_PAY;

      case 5:
        return ProductItems.PayAttributesType.PARTIAL_PAY;

      case 6:
        return ProductItems.PayAttributesType.CREDIT;

      case 7:
        return ProductItems.PayAttributesType.CREDIT_CLOSING;
    }
    return ProductItems.PayAttributesType.FULL_PAY;
  }

}
