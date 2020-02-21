package com.bil24.adapter.mec;

import android.content.Context;
import android.view.*;
import android.widget.*;
import butterknife.*;
import com.bil24.R;
import com.bil24.storage.sql.db.MyExtraTicket;
import com.bil24.utils.Utils;
import server.net.obj.extra.*;

import java.util.List;

public class MECsQrAdapter extends ArrayAdapter<ExtraMEC> {
  private Context context;
  private List<ExtraMEC> list;

  public MECsQrAdapter(Context context, List<ExtraMEC> list) {
    super(context, R.layout.adapter_mecs_qr, list);
    this.context = context;
    this.list = list;
  }

  static class ViewHolder {
    @BindView(R.id.mecsQrAdapterQrCode)
    com.makeramen.roundedimageview.RoundedImageView qrCode;

    @BindView(R.id.mecsQrAdapterTextView)
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
      rowView = inflater.inflate(R.layout.adapter_mecs_qr, parent, false);
      holder = new ViewHolder(rowView);
      rowView.setTag(holder);
    } else {
      holder = (ViewHolder) rowView.getTag();
    }

    final ExtraMEC extraMEC = this.list.get(position);
    holder.textView.setText("Карта № " + extraMEC.getMecId());

    holder.qrCode.setVisibility(View.VISIBLE);
    holder.qrCode.setImageBitmap(Utils.getBitmap(extraMEC.getQrCodeImg()));

    return rowView;
  }

}
