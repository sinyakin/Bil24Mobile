package com.bil24.adapter.ticket;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.bil24.R;
import com.bil24.storage.sql.DataBase;
import com.bil24.storage.sql.db.MyExtraTicket;
import com.bil24.utils.Utils;

import java.util.List;

public class TicketAdapter extends ArrayAdapter<MyExtraTicket> {
  private Context context;
  private List<MyExtraTicket> ticketList;

  public TicketAdapter(Context context, List<MyExtraTicket> ticketList) {
    super(context, R.layout.adapter_ticket, ticketList);
    this.context = context;
    this.ticketList = ticketList;
  }

  public List<MyExtraTicket> getTicketList() {
    return ticketList;
  }

  static class ViewHolder {
    @BindView(R.id.ticketAdapterBarCode)
    com.makeramen.roundedimageview.RoundedImageView barCode;

    @BindView(R.id.ticketAdapterTextViewDate)
    TextView textViewDate;

    @BindView(R.id.ticketAdapterTextViewEventSeat)
    TextView textViewEventSeat;

    @BindView(R.id.ticketAdapterTextViewEventAddress)
    TextView textVieAddress;

    @BindView(R.id.ticketAdapterTextViewBarNumber)
    TextView textVieBarNumber;

    @BindView(R.id.ticketAdapterTicketPanel)
    RelativeLayout ticketPanel;

    @BindView(R.id.ticketAdapterButtonShow)
    com.rey.material.widget.Button buttonShow;

    public ViewHolder(View view) {
      ButterKnife.bind(this, view);
    }
  }

  @Override
  public View getView(final int position, View convertView, ViewGroup parent) {
    ViewHolder holder;

    View rowView = convertView;
    if (rowView == null) {
      LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      rowView = inflater.inflate(R.layout.adapter_ticket, parent, false);
      holder = new ViewHolder(rowView);
      rowView.setTag(holder);
    } else {
      holder = (ViewHolder) rowView.getTag();
    }

    final MyExtraTicket ticket = this.ticketList.get(position);
    if (ticket.isShow()) {
      holder.ticketPanel.setVisibility(View.VISIBLE);
      holder.buttonShow.setVisibility(View.GONE);

      holder.textViewDate.setText(ticket.getDate());
      holder.textViewEventSeat.setText(
          "сумма: " + Utils.formatedRub(ticket.getPrice()) + "\n" +
              ticket.getSeatInfo());
      holder.textVieAddress.setText("адрес: " + ticket.getVenueName());

      holder.barCode.setVisibility(View.VISIBLE);
      holder.barCode.setImageBitmap(Utils.getBitmap(ticket.getBarCodeImg()));
      holder.textVieBarNumber.setText(ticket.getBarCodeNumber());

    } else {
      holder.ticketPanel.setVisibility(View.GONE);
      holder.buttonShow.setVisibility(View.VISIBLE);
      holder.buttonShow.setText("Показать билет (" + ticket.getSeatInfo() + ")");
      holder.buttonShow.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          ticket.setShow(true);
          DataBase.getInstance(getContext()).ticket.updateTicketShow(ticket.getTicketId(), true);
          notifyDataSetChanged();
        }
      });
    }
    return rowView;
  }

}
