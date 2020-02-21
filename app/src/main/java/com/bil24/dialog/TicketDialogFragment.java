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
import com.bil24.storage.sql.db.MyExtraTicket;
import com.bil24.utils.Utils;
import com.makeramen.roundedimageview.RoundedImageView;

/**
 * User: SVV
 * Date: 24.05.2015.
 */
public class TicketDialogFragment extends MySwipeDialogFragment {
  private static final String ACTION_NAME = "ACTION_NAME";
  private static final String TICKET = "TICKET";

  @BindView(R.id.ticketDialogFragmentRootPanel)
  RelativeLayout rootPanel;

  @BindView(R.id.ticketDialogFragmentDataPanel)
  RelativeLayout dataPanel;


  @BindView(R.id.ticketDialogFragmentTextViewTitle)
  TextView textViewTitle;

  @BindView(R.id.ticketDialogFragmentTextViewAction)
  TextView textViewAction;

  @BindView(R.id.ticketDialogFragmentTextViewAddress)
  TextView textViewAddress;

  @BindView(R.id.ticketDialogFragmentTextViewDate)
  TextView textViewDate;

  @BindView(R.id.ticketDialogFragmentTextViewSum)
  TextView textViewSum;

  @BindView(R.id.ticketDialogFragmentTextViewEventSeat)
  TextView textViewEventSeat;

  @BindView(R.id.ticketDialogFragmentQrCode)
  RoundedImageView imageViewQrCode;

  @BindView(R.id.ticketDialogFragmentBarCode)
  RoundedImageView imageViewBarCode;

  public static TicketDialogFragment create(@NonNull MyExtraTicket ticket, @NonNull String actionName) {
    TicketDialogFragment ticketDialogFragment = new TicketDialogFragment();
    Bundle bundle = new Bundle();
    bundle.putSerializable(TICKET, ticket);
    bundle.putString(ACTION_NAME, actionName);
    ticketDialogFragment.setArguments(bundle);
    return ticketDialogFragment;
  }

  @SuppressLint("SetTextI18n")
  @SuppressWarnings("ConstantConditions")
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.dialog_fragment_ticket, container, false);
    ButterKnife.bind(this, view);

    try {
      MyExtraTicket ticket = (MyExtraTicket) getArguments().getSerializable(TICKET);
      String actionName = getArguments().getString(ACTION_NAME);

      textViewTitle.setText("Мобильный электронный билет № " + ticket.getTicketId());

      textViewAction.setText("Мероприятие: " + actionName);
      textViewAddress.setText("Адрес: " + ticket.getVenueAddress() + ", " + ticket.getVenueName());
      textViewDate.setText("Дата: " + ticket.getDate());
      textViewSum.setText("Цена: " + Utils.formatedRub(ticket.getPrice()));
      textViewEventSeat.setText(ticket.getSeatInfo());

      imageViewQrCode.setImageBitmap(Utils.getBitmap(ticket.getQrCodeImg()));
      imageViewBarCode.setImageBitmap(Utils.getBitmap(ticket.getBarCodeImg()));
    } catch (Exception ex) {
      MyUncaughtExceptionHandler.sendError(ex);
    }
    return view;
  }

  @Override
  public void setSwipeMainPanel() {
    this.mainPanel = rootPanel;
  }
}