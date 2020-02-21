package com.bil24.myelement;

import android.content.DialogInterface;
import com.rey.material.app.DialogFragment;

/**
 * Created by SVV on 23.12.2015
 */
public class MyDialogFragment extends DialogFragment {

  @Override
  public void onCancel(DialogInterface dialog) {
    try {
      super.onCancel(dialog);
    } catch (Exception ignored) {}
  }

  @Override
  public void onDismiss(DialogInterface dialog) {
    try {
      super.onDismiss(dialog);
    } catch (Exception ignored) {}
  }
}
