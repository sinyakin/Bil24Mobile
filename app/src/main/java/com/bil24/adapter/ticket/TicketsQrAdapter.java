package com.bil24.adapter.ticket;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.bil24.R;
import com.bil24.storage.sql.db.MyExtraTicket;
import com.bil24.utils.Utils;
import server.net.obj.extra.ExtraTicket;

import java.util.List;

public class TicketsQrAdapter extends ArrayAdapter<MyExtraTicket> {
  private Context context;
  private List<MyExtraTicket> ticketList;

  public TicketsQrAdapter(Context context, List<MyExtraTicket> ticketList) {
    super(context, R.layout.adapter_tickets_qr, ticketList);
    this.context = context;
    this.ticketList = ticketList;
  }

  static class ViewHolder {
    @BindView(R.id.ticketsQrAdapterQrCode)
    com.makeramen.roundedimageview.RoundedImageView qrCode;

    @BindView(R.id.ticketsQrAdapterTextView)
    TextView textView;

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
      rowView = inflater.inflate(R.layout.adapter_tickets_qr, parent, false);
      holder = new ViewHolder(rowView);
      rowView.setTag(holder);
    } else {
      holder = (ViewHolder) rowView.getTag();
    }

    final ExtraTicket ticket = this.ticketList.get(position);
    holder.textView.setText("Билет № " + ticket.getTicketId());

    holder.qrCode.setVisibility(View.VISIBLE);
    holder.qrCode.setImageBitmap(Utils.getBitmap(ticket.getQrCodeImg()));

    return rowView;
  }

}
