package com.bil24.dialog;

import android.app.Dialog;
import android.content.*;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.*;
import android.widget.*;
import butterknife.*;
import com.bil24.R;

public class UpdateDialog extends DialogFragment {
  private static final String TEXT = "TEXT";
  private static final String URL = "URL";
  private static final String CANCELABLE = "CANCELABLE";

  @BindView(R.id.tvUpdateApplication)
  TextView tvUpdateApplication;
  @BindView(R.id.btnOk)
  Button btnOk;

  private String text = "";
  private String url;
  private boolean canelable;

  public UpdateDialog() {
  }

  public static UpdateDialog create(@NonNull String text, @NonNull String url, boolean cancelable) {
    UpdateDialog dialog = new UpdateDialog();
    Bundle bundle = new Bundle();
    bundle.putString(TEXT, text);
    bundle.putString(URL, url);
    bundle.putBoolean(CANCELABLE, cancelable);
    dialog.setArguments(bundle);
    return dialog;
  }

  public void setData(String text, String url, boolean cancelable) {
    this.text = text;
    this.url = url;
    this.canelable = cancelable;
    setCancelable(cancelable);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.dialog_update, container);
    ButterKnife.bind(this, view);
    tvUpdateApplication.setText(text);
    btnOk.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
      }
    });

    return view;
  }

  @Override
  @NonNull
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    final Dialog dialog = super.onCreateDialog(savedInstanceState);
    dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    dialog.setOnKeyListener(new Dialog.OnKeyListener() {

      @Override
      public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
          if (!canelable) getActivity().finish();
          else dialog.dismiss();
        }
        return true;
      }
    });


    return dialog;
  }

  @Override
  public void onStart() {
    super.onStart();
    Dialog dialog = getDialog();
    if (dialog != null) {
      dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
  }
}