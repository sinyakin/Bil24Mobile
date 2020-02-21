package com.bil24.myelement;

import android.content.Context;
import android.widget.RadioButton;
import server.net.obj.extra.ExtraActionEventExt;

/**
 * Created by SVV on 17.09.2015
 */
public class MyRadioButton extends RadioButton {
  private ExtraActionEventExt actionEvent;

  public MyRadioButton(Context context, ExtraActionEventExt actionEvent) {
    super(context);
    this.actionEvent = actionEvent;
  }

  public ExtraActionEventExt getActionEvent() {
    return actionEvent;
  }
}
