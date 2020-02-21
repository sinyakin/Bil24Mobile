package com.bil24.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.bil24.MyUncaughtExceptionHandler;
import com.bil24.R;
import com.bil24.myelement.MyDialogFragment;
import com.bil24.storage.sql.db.MyExtraTicket;
import com.bil24.adapter.ticket.TicketsQrAdapter;

import java.util.ArrayList;

/**
 * User: SVV
 * Date: 24.05.2015.
 */
@SuppressWarnings("unchecked")
public class TicketsQrDialogFragment extends MyDialogFragment {
  private static final String ACTION_NAME = "ACTION_NAME";
  private static final String TICKET = "TICKET";

  @BindView(R.id.ticketsQrFragmentGridView)
  GridView gridView;

  public static TicketsQrDialogFragment create(ArrayList<MyExtraTicket> ticketList, String actionName) {
    TicketsQrDialogFragment ticketDialogFragment = new TicketsQrDialogFragment();
    Bundle bundle = new Bundle();
    bundle.putSerializable(TICKET, ticketList);
    bundle.putString(ACTION_NAME, actionName);
    ticketDialogFragment.setArguments(bundle);
    return ticketDialogFragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.dialog_fragment_tickets_qr, container, false);
    ButterKnife.bind(this, view);

    try {
      ArrayList<MyExtraTicket> ticketList = (ArrayList<MyExtraTicket>) getArguments().getSerializable(TICKET);
      String actionName = getArguments().getString(ACTION_NAME);
      getDialog().setTitle(actionName);
      gridView.setAdapter(new TicketsQrAdapter(getActivity(), ticketList));
    } catch (Exception ex) {
      MyUncaughtExceptionHandler.sendError(ex);
    }

    return view;
  }

}