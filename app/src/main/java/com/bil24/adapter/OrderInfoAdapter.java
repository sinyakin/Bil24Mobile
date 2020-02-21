package com.bil24.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.*;
import android.widget.*;
import butterknife.*;
import com.bil24.R;
import com.bil24.utils.Utils;
import com.squareup.picasso.Picasso;
import server.net.obj.extra.ExtraOrderInfo;

import java.util.List;

public class OrderInfoAdapter extends ArrayAdapter<ExtraOrderInfo> {
  private Context context;
  private List<ExtraOrderInfo> actionList;

  public OrderInfoAdapter(Context context, List<ExtraOrderInfo> actionList) {
    super(context, R.layout.adapter_order_info, actionList);
    this.context = context;
    this.actionList = actionList;
  }

  static class ViewHolder {
    @BindView(R.id.orderInfoAdapterPoster1)
    ImageView poster1;

    @BindView(R.id.orderInfoAdapterActionName)
    TextView textViewActionName;

    @BindView(R.id.orderInfoAdapterTickets)
    TextView textViewTickets;

    @BindView(R.id.orderInfoAdapterSum)
    TextView textViewSum;

    public ViewHolder(View view) {
      ButterKnife.bind(this, view);
    }
  }

  @SuppressLint("SetTextI18n")
  @Override
  public View getView(final int position, View convertView, ViewGroup parent) {
    ViewHolder holder;

    View rowView = convertView;
    if (rowView == null) {
      LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      rowView = inflater.inflate(R.layout.adapter_order_info, parent, false);
      holder = new ViewHolder(rowView);
      rowView.setTag(holder);
    } else {
      holder = (ViewHolder) rowView.getTag();
    }

    ExtraOrderInfo orderInfo = this.actionList.get(position);
    holder.textViewActionName.setText(orderInfo.getActionName());
    holder.textViewTickets.setText("билетов: " + orderInfo.getQuantity());
    holder.textViewSum.setText("сумма: " + Utils.formatedRub(orderInfo.getSum()));

    Picasso.with(getContext())
        .load(orderInfo.getSmallPosterUrl())
        .placeholder(R.drawable.poster1)
        .error(R.drawable.poster1).fit()
        .into(holder.poster1);

    return rowView;
  }

}
