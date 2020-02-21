package com.bil24.adapter;

import android.content.Context;
import android.view.*;
import android.widget.*;
import butterknife.*;
import com.bil24.R;
import com.bil24.fragments.action.filter.Kind;

import java.util.List;

public class KindSpinnerAdapter extends ArrayAdapter<Kind> {
  private Context context;
  private List<Kind> kindList;

  public KindSpinnerAdapter(Context context, List<Kind> kindList) {
    super(context, R.layout.row_spn, kindList);
    this.context = context;
    this.kindList = kindList;
  }

  static class ViewHolder {
    @BindView(R.id.row_spn_tv)
    TextView row_spn_tv;

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
      rowView = inflater.inflate(R.layout.row_spn, parent, false);
      holder = new ViewHolder(rowView);
      rowView.setTag(holder);
    } else {
      holder = (ViewHolder) rowView.getTag();
    }

    Kind kind = this.kindList.get(position);
    holder.row_spn_tv.setText(kind.getKindName());

    return rowView;
  }

  @Override
  public View getDropDownView(int position, View convertView, ViewGroup parent) {
    View rowView = convertView;
    if (rowView == null) {
      LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      rowView = inflater.inflate(R.layout.row_spn_dropdown, parent, false);
    }

    Kind kind = this.kindList.get(position);
    TextView row_spn_tv = (TextView) rowView.findViewById(R.id.row_spn_tv);
    row_spn_tv.setText(kind.getKindName());

    return rowView;
  }

}
