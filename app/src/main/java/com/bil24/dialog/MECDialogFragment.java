package com.bil24.dialog;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.*;
import android.widget.*;
import butterknife.*;
import com.bil24.*;
import com.bil24.R;
import com.bil24.myelement.*;
import com.bil24.utils.Utils;
import com.makeramen.roundedimageview.RoundedImageView;
import server.net.obj.extra.ExtraMEC;

/**
 * User: SVV
 * Date: 24.05.2015.
 */
public class MECDialogFragment extends MySwipeDialogFragment {
  private static final String ACTION_NAME = "ACTION_NAME";
  private static final String VENUE_NAME = "VENUE_NAME";
  private static final String MEC = "MEC";

  @BindView(R.id.mecDialogFragmentRootPanel)
  RelativeLayout rootPanel;

  @BindView(R.id.mecDialogFragmentTextViewTitle)
  TextView textViewTitle;

  @BindView(R.id.mecDialogFragmentTextViewAction)
  TextView textViewAction;

  @BindView(R.id.mecDialogFragmentTextViewAddress)
  TextView textViewAddress;

  @BindView(R.id.mecDialogFragmentTextViewSum)
  TextView textViewSum;

  @BindView(R.id.mecDialogFragmentTextViewEventSeat)
  TextView textViewEventSeat;

  @BindView(R.id.mecDialogFragmentQrCode)
  RoundedImageView imageViewQrCode;

  @BindView(R.id.mecDialogFragmentBarCode)
  RoundedImageView imageViewBarCode;

  public static MECDialogFragment create(@NonNull ExtraMEC extraMEC, @NonNull String actionName, @NonNull String venueName) {
    MECDialogFragment ticketDialogFragment = new MECDialogFragment();
    Bundle bundle = new Bundle();
    bundle.putSerializable(MEC, extraMEC);
    bundle.putString(ACTION_NAME, actionName);
    bundle.putString(VENUE_NAME, venueName);
    ticketDialogFragment.setArguments(bundle);
    return ticketDialogFragment;
  }

  @SuppressLint("SetTextI18n")
  @SuppressWarnings("ConstantConditions")
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.dialog_fragment_mec, container, false);
    ButterKnife.bind(this, view);

    try {
      ExtraMEC extraMEC = (ExtraMEC) getArguments().getSerializable(MEC);
      String actionName = getArguments().getString(ACTION_NAME);
      String venueName = getArguments().getString(VENUE_NAME);

      textViewTitle.setText("Мобильная электронная карта № " + extraMEC.getMecId());

      textViewEventSeat.setText(extraMEC.getCategoryName());
      textViewAction.setText(actionName);
      textViewAddress.setText(venueName);
      textViewSum.setText("Цена: " + Utils.formatedRub(extraMEC.getPrice()));

      imageViewQrCode.setImageBitmap(Utils.getBitmap(extraMEC.getQrCodeImg()));
      imageViewBarCode.setImageBitmap(Utils.getBitmap(extraMEC.getBarCodeImg()));
    } catch (Exception ex) {
      MyUncaughtExceptionHandler.sendError(ex);
    }
    return view;
  }

  @Override
  protected void setSwipeMainPanel() {
    mainPanel = rootPanel;
  }
}