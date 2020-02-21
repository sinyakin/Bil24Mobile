package com.bil24.adapter.promo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bil24.R;
import com.bil24.dialog.Dialogs;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PromoCodesAdapter extends ArrayAdapter<ExtraPromo> {
  private Context context;
  private List<ExtraPromo> list;

  public PromoCodesAdapter(Context context, List<ExtraPromo> list) {
    super(context, R.layout.adapter_promo_codes, list);
    this.context = context;
    this.list = list;
  }

  static class ViewHolder {
    @BindView(R.id.promoCodesAdapterName)
    TextView textViewName;

    @BindView(R.id.promoCodesAdapterInfo)
    TextView textViewInfo;

    @BindView(R.id.promoCodesAdapterDiscount)
    TextView textViewDiscount;

    @BindView(R.id.promoCodesAdapterMainPanel)
    RelativeLayout mainPanel;

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
      rowView = inflater.inflate(R.layout.adapter_promo_codes, parent, false);
      holder = new ViewHolder(rowView);
      rowView.setTag(holder);
    } else {
      holder = (ViewHolder) rowView.getTag();
    }

    final ExtraPromo promo = this.list.get(position);
    holder.textViewName.setText(promo.getExtraPromoCode().getCode());
    holder.textViewDiscount.setText(promo.getExtraPromoPack().getDiscountPercent() + "%");

    boolean active = promo.getExtraPromoPack().isActiveNow() && !promo.getExtraPromoCode().isSpent() && !promo.getExtraPromoCode().isUsed();

    String info = "Действует с " + promo.getExtraPromoPack().getStartTime() + " по " +
        promo.getExtraPromoPack().getEndTime() + ", скидка " + promo.getExtraPromoPack().getDiscountPercent() + "%";
    if (promo.getExtraPromoPack().getMaxDiscountSum() != null) {
      info += ", максимальная скидка по промокоду " + promo.getExtraPromoPack().getMaxDiscountSum() + " руб.";
    }
    if (promo.getExtraPromoPack().getMaxTickets() != null) {
      info += ", максимальное число билетов со скидкой " + promo.getExtraPromoPack().getMaxTickets() + " шт.";
    }
    holder.textViewInfo.setText(info);

    if (!active) {
      holder.textViewName.setTextColor(ContextCompat.getColor(context, R.color.no_activate_promo_code));
      holder.textViewDiscount.setTextColor(ContextCompat.getColor(context, R.color.no_activate_promo_code));
      holder.textViewInfo.setTextColor(ContextCompat.getColor(context, R.color.no_activate_promo_code));
    } else {
      holder.textViewName.setTextColor(ContextCompat.getColor(context, R.color.toolbar));
      holder.textViewDiscount.setTextColor(ContextCompat.getColor(context, R.color.price));
      holder.textViewInfo.setTextColor(Color.GRAY);
    }

    holder.mainPanel.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Dialogs.alert(context, promo.getExtraPromoPack().getName(), promo.getExtraPromoPack().getDescription(), null);
      }
    });

    return rowView;
  }
}
