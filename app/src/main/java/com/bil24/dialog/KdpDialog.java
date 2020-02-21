package com.bil24.dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.*;
import android.widget.*;
import butterknife.*;
import com.bil24.R;
import com.bil24.myelement.*;
import com.bil24.storage.Settings;
import com.rey.material.app.Dialog;
import com.rey.material.widget.Button;
import server.net.*;
import server.net.listener.CheckKdpListener;
import server.net.obj.CheckKDPClient;

public class KdpDialog extends MyDialogFragment implements CheckKdpListener {

  @BindView(R.id.kdpDialogTextView)
  TextView infoTextView;
  @BindView(R.id.kdpDialogEditTextKdp)
  EditText kdpEditText;
  @BindView(R.id.kdpDialogLoadingPanel)
  MyLoadingPanel loadingPanel;

  private Button button;
  private KdpSuccessListener kdpSuccessListener;
  private long actionId;
  private Integer kdp;

  public void setData(KdpSuccessListener kdpSuccessListener, long actionId) {
    this.kdpSuccessListener = kdpSuccessListener;
    this.actionId = actionId;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.dialog_kdp, container);
    ButterKnife.bind(this, view);

    loadingPanel.setText("Проверка КДП...");
    loadingPanel.setVisibility(View.GONE);

    if (Settings.existKdp(actionId)) {
      checkKdp(Settings.getActionIdKdp(actionId));
    }
    return view;
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    Dialog dialog = super.onCreateDialog(savedInstanceState);
    dialog.setTitle("Введите КДП");
    dialog.positiveAction("ОК");
    dialog.positiveActionClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        try {
          kdp = Integer.parseInt(kdpEditText.getText().toString());
        } catch (Exception ex) {
          Dialogs.showSnackBar(getActivity(), "Ошибка при вводе КДП");
          return;
        }
        checkKdp(kdp);
      }
    });
    button = (Button) dialog.findViewById(Dialog.ACTION_POSITIVE);
    return dialog;
  }

  private void checkKdp(Integer kdp) {
    this.kdp = kdp;
    loadingPanel.setVisibility(View.VISIBLE);
    kdpEditText.setVisibility(View.GONE);
    infoTextView.setVisibility(View.GONE);
    button.setVisibility(View.GONE);

    NetManager.checkKdp(KdpDialog.this, actionId, kdp);
  }

  @Override
  public void onResume() {
    super.onResume();
    if (kdpSuccessListener == null) dismiss();
  }

  @Override
  public void onCheckKdp(CheckKDPClient checkKDPClient) {
    if (!isAdded()) return;
    Settings.setActionIdKdp(actionId, kdp);
    dismiss();
    if (kdpSuccessListener != null) kdpSuccessListener.onKdpSuccess(kdp);
  }

  @Override
  public void onCheckKdpFailed(NetException e) {
    if (!isAdded()) return;
    Settings.resetActionIdKdp(actionId);
    loadingPanel.setVisibility(View.GONE);
    kdpEditText.setVisibility(View.VISIBLE);
    infoTextView.setVisibility(View.VISIBLE);
    button.setVisibility(View.VISIBLE);
    if (e.isUserMessage()) Dialogs.showSnackBar(getActivity(), e.getMessage());
  }

  public interface KdpSuccessListener {
    void onKdpSuccess(int kdp);
  }
}