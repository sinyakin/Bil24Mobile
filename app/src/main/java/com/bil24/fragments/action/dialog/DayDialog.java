package com.bil24.fragments.action.dialog;

import android.content.Context;
import com.bil24.R;
import com.bil24.fragments.action.listener.DayChangeListener;
import com.bil24.utils.Utils;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;

/**
 * User: SVV
 * Date: 16.06.2015.
 */
public class DayDialog {

  public static DatePickerDialog create(Context context, final DayChangeListener listener, Calendar[] calendars, String selectedDate) {
    Calendar selectDate = Calendar.getInstance();
    selectDate.setTime(Utils.dateFormatDDMMYYYY(selectedDate));
    DatePickerDialog dayDialog = DatePickerDialog.newInstance(
        new DatePickerDialog.OnDateSetListener() {
          @Override
          public void onDateSet(DatePickerDialog datePickerDialog, int year, int monthOfYear, int dayOfMonth) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, monthOfYear, dayOfMonth);
            if (listener != null) listener.dayChange(calendar.getTime());
          }
        },
        selectDate.get(Calendar.YEAR),
        selectDate.get(Calendar.MONTH),
        selectDate.get(Calendar.DAY_OF_MONTH)
    );

    dayDialog.setSelectableDays(calendars);
    dayDialog.setHighlightedDays(calendars);
    dayDialog.setAccentColor(context.getResources().getColor(R.color.material_date_picker));
    return dayDialog;
  }

  public static DatePickerDialog create(Context context, final DayChangeListener listener) {
    Calendar selectDate = Calendar.getInstance();
    DatePickerDialog dayDialog = DatePickerDialog.newInstance(
        new DatePickerDialog.OnDateSetListener() {
          @Override
          public void onDateSet(DatePickerDialog datePickerDialog, int year, int monthOfYear, int dayOfMonth) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, monthOfYear, dayOfMonth);
            if (listener != null) listener.dayChange(calendar.getTime());
          }
        },
        selectDate.get(Calendar.YEAR),
        selectDate.get(Calendar.MONTH),
        selectDate.get(Calendar.DAY_OF_MONTH)
    );

//    dayDialog.setMinDate(Calendar.getInstance());
    dayDialog.setAccentColor(context.getResources().getColor(R.color.material_date_picker));
    return dayDialog;
  }
}