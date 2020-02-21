package com.bil24.dialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.*;
import android.widget.*;
import butterknife.*;
import com.bil24.*;
import com.bil24.R;
import com.bil24.storage.Settings;
import com.bil24.utils.Utils;

public class FullNameDialog extends DialogFragment {

  @BindView(R.id.fullNameDialogEditTextKdp)
  EditText etFullName;

  @BindView(R.id.fullNameDialogButtonCancel1)
  com.rey.material.widget.Button btnCancel;
  @BindView(R.id.fullNameDialogButtonOk)
  com.rey.material.widget.Button btnOk;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.dialog_full_name, container);
    ButterKnife.bind(this, view);

    btnCancel.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Utils.hideKeyboard(getActivity());
        dismiss();
      }
    });

    btnOk.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String fullName = etFullName.getText().toString();
        String[] tmp = fullName.split(" ");
        if (fullName.isEmpty() || tmp.length < 2) {
          Dialogs.showSnackBar(getActivity(), "Поле 'Фамилия и имя' не заполнено");
          return;
        }
        Utils.hideKeyboard(getActivity());
        Settings.setLastFullName(fullName);
        Controller.getInstance().onSuccessFullName(fullName);
        dismiss();
      }
    });
    etFullName.setText(Settings.getLastFullName());
    return view;
  }
}