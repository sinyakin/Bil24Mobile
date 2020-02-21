package com.bil24.myelement;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.view.*;
import android.widget.*;
import com.bil24.R;
import com.bil24.adapter.CategoryPriceSpinnerAdapter;
import com.bil24.dialog.Dialogs;
import com.bil24.utils.Utils;
import com.rey.material.widget.Button;

/**
 * Виктор
 * 14.08.2015.
 */
public class MyNumberPicker extends LinearLayout {
  private EditText numberPickerEditText;
  private final int defaultValue = 0;
  private ReservationCategoryListener reservationCategoryListener;
  private CategoryPriceSpinnerAdapter categoryPriceAdapter;
  private FragmentActivity activity;

  public MyNumberPicker(Context context) {
    super(context);
    init(context);
  }

  public MyNumberPicker(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context);
  }

  public MyNumberPicker(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context);
  }

  public void setData(FragmentActivity activity, ReservationCategoryListener reservationCategoryListener, CategoryPriceSpinnerAdapter categoryPriceAdapter) {
    this.activity = activity;
    this.reservationCategoryListener = reservationCategoryListener;
    this.categoryPriceAdapter = categoryPriceAdapter;
  }

  private void init(final Context context) {
    LayoutInflater mInflater = LayoutInflater.from(context);
    mInflater.inflate(R.layout.number_picker, this, true);
    numberPickerEditText = (EditText) findViewById(R.id.numberPickerEditText);
    Button numberPickerButtonM = (Button) findViewById(R.id.numberPickerButtonM);
    Button numberPickerButtonP = (Button) findViewById(R.id.numberPickerButtonP);
    numberPickerEditText.setText(String.valueOf(defaultValue));
    numberPickerEditText.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        v.setFocusable(true);
        v.setFocusableInTouchMode(true);
        return false;
      }
    });

    numberPickerButtonM.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        if (categoryPriceAdapter.isEmpty()) {
          Dialogs.showInfoDialog(activity.getSupportFragmentManager(), "Внимание", "Все билеты проданы");
          return;
        }
        Integer value = getValue();
        if (value > defaultValue) numberPickerEditText.setText(String.valueOf(--value));
        if (reservationCategoryListener != null) reservationCategoryListener.onReservationQuantityPlace(value);
        numberPickerEditText.setFocusable(false);
        Utils.hideSoftKeyboard((Activity) context);
      }
    });

    numberPickerButtonP.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        if (categoryPriceAdapter.isEmpty()) {
          Dialogs.showInfoDialog(activity.getSupportFragmentManager(), "Внимание", "Все билеты проданы");
          return;
        }
        Integer value = getValue();
        numberPickerEditText.setText(String.valueOf(++value));
        if (reservationCategoryListener != null) reservationCategoryListener.onReservationQuantityPlace(value);
        numberPickerEditText.setFocusable(false);
        Utils.hideSoftKeyboard((Activity) context);
      }
    });
  }

  public Integer getValue() {
    try {
      Integer value = Integer.valueOf(numberPickerEditText.getText().toString());
      if (value < defaultValue) value = defaultValue;
      return value;
    } catch (Exception ex) {
      numberPickerEditText.setText(String.valueOf(defaultValue));
      return defaultValue;
    }
  }

  public void setNumber(@Nullable Integer quantity) {
    if (quantity == null || quantity < defaultValue) numberPickerEditText.setText(String.valueOf(defaultValue));
    else numberPickerEditText.setText(String.valueOf(quantity));
  }

  public interface ReservationCategoryListener {
    void onReservationQuantityPlace(int quantity);
  }
}