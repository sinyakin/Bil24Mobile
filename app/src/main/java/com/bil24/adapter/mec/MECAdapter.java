package com.bil24.adapter.mec;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import android.widget.*;
import butterknife.*;
import com.bil24.R;
import com.bil24.dialog.MECDialogFragment;
import com.bil24.utils.Utils;
import server.net.obj.extra.ExtraMEC;

import java.util.*;

public class MECAdapter extends ArrayAdapter<ExtraMEC> {
  private Context context;
  private List<ExtraMEC> list = new ArrayList<>();
  private String actionName = "";
  private String venueName = "";

  public MECAdapter(Context context, String actionName, String venueName, List<ExtraMEC> list) {
    super(context, R.layout.adapter_mec, list);
    this.context = context;
    this.actionName = actionName;
    this.venueName = venueName;
    this.list = list;
  }

  public List<ExtraMEC> getList() {
    return list;
  }

  static class ViewHolder {
    @BindView(R.id.mecAdapterBarCode)
    com.makeramen.roundedimageview.RoundedImageView barCode;

    @BindView(R.id.mecAdapterTextViewEventSeat)
    TextView textViewEventSeat;

    @BindView(R.id.mecAdapterTextViewEventAddress)
    TextView textVieAddress;

    @BindView(R.id.mecAdapterTextViewBarNumber)
    TextView textVieBarNumber;

    @BindView(R.id.mecAdapterTicketPanel)
    RelativeLayout ticketPanel;

    public ViewHolder(View view) {
      ButterKnife.bind(this, view);
    }
  }

  @NonNull
  @Override
  public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
    ViewHolder holder;

    View rowView = convertView;
    if (rowView == null) {
      LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      rowView = inflater.inflate(R.layout.adapter_mec, parent, false);
      holder = new ViewHolder(rowView);
      rowView.setTag(holder);
    } else {
      holder = (ViewHolder) rowView.getTag();
    }

    final ExtraMEC extraMEC = this.list.get(position);
    holder.textViewEventSeat.setText(
        "сумма: " + Utils.formatedRub(extraMEC.getPrice()) + "\n\n" +
            extraMEC.getCategoryName());
    holder.textVieAddress.setText(venueName);
    holder.barCode.setImageBitmap(Utils.getBitmap(extraMEC.getBarCodeImg()));
    holder.textVieBarNumber.setText(extraMEC.getBarCodeNumber());
    holder.ticketPanel.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        MECDialogFragment dialogFragment = MECDialogFragment.create(extraMEC, actionName, venueName);
        dialogFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), null);
      }
    });
    return rowView;
  }
}
