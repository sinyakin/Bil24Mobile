package com.bil24.fragments.action;

import android.app.Activity;
import android.content.Context;
import android.view.*;
import android.widget.*;
import com.bil24.R;
import com.bil24.myelement.*;
import server.net.obj.extra.ExtraActionEventExt;

import java.util.List;

/**
 * Created by SVV on 17.09.2015
 */
public class EventSeatRadioGroup extends GridLayout {
  private List<ExtraActionEventExt> actionEventList;
  private MultiRowsRadioGroup multiRowsRadioGroup;
  private int margin = 10;

  public EventSeatRadioGroup(Context context, final MultiRowsRadioGroup multiRowsRadioGroup, List<ExtraActionEventExt> actionEventList) {
    super(context);
    this.multiRowsRadioGroup = multiRowsRadioGroup;
    this.actionEventList = actionEventList;

    multiRowsRadioGroup.removeAllViews();
    setOrientation(GridLayout.HORIZONTAL);

    if (actionEventList.isEmpty()) return;

    final RadioButton radioButton = radioButton(actionEventList.get(0));
    radioButton.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
      @Override
      public void onGlobalLayout() {
        if (radioButton.getHeight() > 0) {
          radioButton.getViewTreeObserver().removeGlobalOnLayoutListener(this);
          EventSeatRadioGroup.this.removeView(radioButton);
          int parentWidth = ((View) EventSeatRadioGroup.this.getParent().getParent()).getWidth();
          multiRowsRadioGroup.removeAllViews();
          start(radioButton.getWidth(), parentWidth);
        }
      }
    });

    addView(radioButton);
  }

  private void start(final int width, final float screenWidth) {
    if (actionEventList.isEmpty()) return;
    new Thread(new Runnable() {
      @Override
      public void run() {
        int result = 2;
        for (int i = 2; i < 50; i++) {
          int w = i * width + (i - 1) * margin;
          if (w > screenWidth) {
            result = i - 1;
            break;
          }
        }
        final int finalResult = result;
        EventSeatRadioGroup.this.run(finalResult);
      }
    }).start();
  }

  private void run(int column) {
    if (column > actionEventList.size()) column = actionEventList.size();
    int row = actionEventList.size() / column;
    if (actionEventList.size() % column != 0) row++;

    setColumnCount(column);
    setRowCount(row);

    for (int i = 0, c = 0, r = 0; i < actionEventList.size(); i++, c++) {

      if (c == column) {
        c = 0;
        r++;
      }

      GridLayout.LayoutParams param = new GridLayout.LayoutParams();
      param.height = GridLayout.LayoutParams.WRAP_CONTENT;
      param.width = GridLayout.LayoutParams.WRAP_CONTENT;
      if ((i + 1) % column != 0) param.rightMargin = margin;
      param.topMargin = margin;
      param.setGravity(Gravity.CENTER);
      param.columnSpec = GridLayout.spec(c);
      param.rowSpec = GridLayout.spec(r);

      final MyRadioButton radioButton = radioButton(actionEventList.get(i));
      radioButton.setLayoutParams(param);
      if (i == 0) radioButton.toggle();

      ((Activity) getContext()).runOnUiThread(new Runnable() {
        @Override
        public void run() {
          addView(radioButton);
        }
      });
    }

    ((Activity) getContext()).runOnUiThread(new Runnable() {
      @Override
      public void run() {
        multiRowsRadioGroup.addView(EventSeatRadioGroup.this);
      }
    });
  }

  private MyRadioButton radioButton(ExtraActionEventExt actionEvent) {
    MyRadioButton radioButton = new MyRadioButton(getContext(), actionEvent);
    radioButton.setBackgroundResource(R.drawable.radio_button_bg);
    radioButton.setButtonDrawable(getContext().getResources().getDrawable(android.R.color.transparent));
    radioButton.setPadding(10, 5, 10, 5);
    radioButton.setText(actionEvent.getTime());
    return radioButton;
  }
}
