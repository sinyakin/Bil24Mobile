package com.bil24.myelement;

import android.content.Context;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * A RadioGroup allow multiple rows layout, will use the RadioButton's OnCheckedChangeListener
 */
public class MultiRowsRadioGroup extends RadioGroup {
  private List<RadioButton> radioButtonList = new ArrayList<>();

  public MultiRowsRadioGroup(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public MultiRowsRadioGroup(Context context) {
    super(context);
    init();
  }

  public void init() {
    setOnHierarchyChangeListener(new OnHierarchyChangeListener() {
      public void onChildViewRemoved(View parent, View child) {
        if (parent == MultiRowsRadioGroup.this && child instanceof ViewGroup) {
          for (RadioButton radioButton : getRadioButtonFromGroup((ViewGroup) child)) {
            radioButton.setOnCheckedChangeListener(null);
          }
        }
      }

      @Override
      public void onChildViewAdded(View parent, View child) {
        if (parent == MultiRowsRadioGroup.this && child instanceof ViewGroup) {
          for (final RadioButton radioButton : getRadioButtonFromGroup((ViewGroup) child)) {
            int id = radioButton.getId();
            // generates an id if it's missing
            if (id == View.NO_ID) {
              if (VERSION.SDK_INT >= 17) id = View.generateViewId();
              else id = radioButton.hashCode();
              radioButton.setId(id);
            }
            if (radioButton.isChecked()) {
              check(id);
            }

            radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
              public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                  radioButton.setOnCheckedChangeListener(null);
                  check(buttonView.getId());
                  radioButton.setOnCheckedChangeListener(this);
                }
              }
            });

          }
        }
      }
    });
  }

  private boolean checking = false;

  @Override
  public void check(int id) {
    if (checking) return;
    checking = true;
    super.check(id);
    checking = false;
  }

  private ArrayList<RadioButton> getRadioButtonFromGroup(ViewGroup group) {
    if (group == null) return new ArrayList<>();
    ArrayList<RadioButton> list = new ArrayList<>();
    getRadioButtonFromGroup(group, list);
    return list;
  }

  private void getRadioButtonFromGroup(ViewGroup group, ArrayList<RadioButton> list) {
    for (int i = 0, count = group.getChildCount(); i < count; i++) {
      View child = group.getChildAt(i);
      if (child instanceof RadioButton) {
        radioButtonList.add((RadioButton) child);
        list.add((RadioButton) child);

      } else if (child instanceof ViewGroup) {
        getRadioButtonFromGroup((ViewGroup) child, list);
      }
    }
  }

  @Override
  public void removeAllViews() {
    radioButtonList.clear();
    super.removeAllViews();
  }

  @Override
  public void setEnabled(boolean enabled) {
    for (RadioButton radioButton : radioButtonList) {
      radioButton.setEnabled(enabled);
    }
    super.setEnabled(enabled);
  }
}
