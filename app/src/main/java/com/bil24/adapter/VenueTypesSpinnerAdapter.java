package com.bil24.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.*;
import android.widget.*;
import butterknife.*;
import com.bil24.R;
import server.net.obj.extra.ExtraVenueType;

import java.util.List;

public class VenueTypesSpinnerAdapter extends ArrayAdapter<ExtraVenueType> {
  private Context context;
  private List<ExtraVenueType> list;

  public VenueTypesSpinnerAdapter(Context context, List<ExtraVenueType> list) {
    super(context, R.layout.row_spn, list);
    this.context = context;
    this.list = list;
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

    ExtraVenueType venueType = this.list.get(position);
    holder.row_spn_tv.setText(venueType.getVenueTypeName());
    return rowView;
  }

  @Override
  public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
    View rowView = convertView;
    if (rowView == null) {
      LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      rowView = inflater.inflate(R.layout.row_spn_dropdown, parent, false);
    }

    ExtraVenueType venueType = this.list.get(position);
    TextView row_spn_tv = (TextView) rowView.findViewById(R.id.row_spn_tv);
    row_spn_tv.setText(venueType.getVenueTypeName());

    return rowView;
  }
}