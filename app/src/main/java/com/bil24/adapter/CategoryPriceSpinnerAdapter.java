package com.bil24.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.*;
import android.widget.*;
import butterknife.*;
import com.bil24.R;
import com.bil24.utils.Utils;
import server.net.obj.extra.ExtraCategoryPriceExt;

import java.util.List;

public class CategoryPriceSpinnerAdapter extends ArrayAdapter<ExtraCategoryPriceExt> {
  private Context context;
  private List<ExtraCategoryPriceExt> categoryPriceList;

  public CategoryPriceSpinnerAdapter(Context context, List<ExtraCategoryPriceExt> categoryPriceList) {
    super(context, R.layout.row_spn, categoryPriceList);
    this.context = context;
    this.categoryPriceList = categoryPriceList;
  }

  static class ViewHolder {
    @BindView(R.id.row_spn_tv)
    TextView row_spn_tv;

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
      rowView = inflater.inflate(R.layout.row_spn, parent, false);
      holder = new ViewHolder(rowView);
      rowView.setTag(holder);
    } else {
      holder = (ViewHolder) rowView.getTag();
    }

    ExtraCategoryPriceExt categoryPrice = this.categoryPriceList.get(position);
    holder.row_spn_tv.setText(categoryPrice.getCategoryPriceName() + " " + Utils.sumFormat.format(categoryPrice.getPrice()) + " руб.");

    return rowView;
  }

  @SuppressLint("SetTextI18n")
  @Override
  public View getDropDownView(int position, View convertView, ViewGroup parent) {
    View rowView = convertView;
    if (rowView == null) {
      LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      rowView = inflater.inflate(R.layout.row_spn_dropdown, parent, false);
    }

    ExtraCategoryPriceExt categoryPrice = getItem(position);
    TextView row_spn_tv = (TextView) rowView.findViewById(R.id.row_spn_tv);
    row_spn_tv.setText(categoryPrice.getCategoryPriceName() + " " + Utils.sumFormat.format(categoryPrice.getPrice()) + " руб.");

    return rowView;
  }

}
