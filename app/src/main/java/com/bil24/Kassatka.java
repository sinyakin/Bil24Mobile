package com.bil24;

import android.content.Context;
import android.support.annotation.NonNull;

import java.math.BigDecimal;

import ru.kassatka.comepay_sdk.callBack.CallBackCloseCheck;
import server.net.obj.GetOrderInfoClient;
import server.net.obj.extra.ExtraOrderInfo;

public class Kassatka {

  public static void print(Context context, @NonNull GetOrderInfoClient clientData, @NonNull CallBackCloseCheck callBackCloseCheck) {
    KassaClient kassaClient = new KassaClient(context);
    kassaClient.openReceipt(1, "ИП Синякин", "Лузана 23", "235435646456");
    kassaClient.setOrderTaxation(2);
    BigDecimal totalSum = BigDecimal.ZERO;
//    kassaClient.printLine("middle");
    for (ExtraOrderInfo orderInfo : clientData.getOrderInfoList()) {
      totalSum = totalSum.add(orderInfo.getSum());
      kassaClient.addPosition(orderInfo.getActionName(), orderInfo.getSum().doubleValue(), orderInfo.getQuantity(), 2, 4, 1);
    }

//    kassaClient.printLine("middle");
    kassaClient.printEmpty(1);
    kassaClient.addCheckPayment(1, totalSum.doubleValue());
    kassaClient.printEmpty(1);
    kassaClient.closeReceipt(callBackCloseCheck);

    /*PrintCommand printCommand = new PrintCommand();
    Extra extra = new Extra();
    extra.putExtra("ACTION", "PRINT_STRING");
    extra.putExtra("STRING", "Some text...");
    printCommand.printEmtyString(extra);
    printCommand.startCommand(context, new CallBackPrint() {
      @Override
      public void result(Boolean isPrint) {
        Log.d("!!!!!", isPrint + "");
      }
    });*/
    /*printCommand.startCommand(context, new CallBackCloseCheck() {
      @Override
      public void getStatus(StatusPrint statusPrint) {
        Log.d("!!!!!", statusPrint.id + " " + statusPrint.checkID + " " + statusPrint.docType + " " + statusPrint.status + " " + statusPrint.text + " " + statusPrint.error);
      }
    });*/


    /*CheckCommand command = new CheckCommand();
    Extra extra = new Extra();
    extra.putExtra("ACTION", "CREATE_CHECK");
    extra.putExtra("DOC_TYPE", "SALE");
    extra.putExtra("ORGANISATION_NAME", "ИП Синякин");
    extra.putExtra("ORGANISATION_ADDRESS", "Лузана 23");
    extra.putExtra("ORGANISATION_INN", "235435646456");
//    extra.putExtra("", "");
//    extra.putExtra("", "");
    command.openCheckCommand(extra);
    command.startCommand(context, new CallBackCloseCheck() {
      @Override
      public void getStatus(StatusPrint statusPrint) {
        Log.d("!!!!!", statusPrint.id + " " + statusPrint.checkID + " " + statusPrint.docType + " " + statusPrint.status + " " + statusPrint.text + " " + statusPrint.error);
      }
    });*/
  }
}
