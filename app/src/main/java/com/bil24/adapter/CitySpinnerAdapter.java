package com.bil24.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.*;
import android.widget.*;
import butterknife.*;
import com.bil24.R;
import com.bil24.fragments.action.filter.City;

import java.util.List;

public class CitySpinnerAdapter extends ArrayAdapter<City> {
  private Context context;
  private List<City> cityList;

  public CitySpinnerAdapter(Context context, List<City> cityList) {
    super(context, R.layout.row_spn, cityList);
    this.context = context;
    this.cityList = cityList;
  }

  static class ViewHolder {
    @BindView(R.id.row_spn_tv)
    TextView row_spn_tv;

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
      rowView = inflater.inflate(R.layout.row_spn, parent, false);
      holder = new ViewHolder(rowView);
      rowView.setTag(holder);
    } else {
      holder = (ViewHolder) rowView.getTag();
    }
    City city = this.cityList.get(position);
    holder.row_spn_tv.setText(city.getCityName());
    return rowView;
  }

  @Override
  public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
    View rowView = convertView;
    if (rowView == null) {
      LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      rowView = inflater.inflate(R.layout.row_spn_dropdown, parent, false);
    }
    City city = this.cityList.get(position);
    TextView row_spn_tv = (TextView) rowView.findViewById(R.id.row_spn_tv);
    row_spn_tv.setText(city.getCityName());
    return rowView;
  }
}
