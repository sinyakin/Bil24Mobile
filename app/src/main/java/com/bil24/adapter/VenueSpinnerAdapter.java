package com.bil24.adapter;

import android.content.Context;
import android.view.*;
import android.widget.*;
import butterknife.*;
import com.bil24.R;
import com.bil24.fragments.action.filter.Venue;

import java.util.List;

public class VenueSpinnerAdapter extends ArrayAdapter<Venue> {
  private Context context;
  private List<Venue> venueList;

  public VenueSpinnerAdapter(Context context, List<Venue> venueList) {
    super(context, R.layout.row_spn, venueList);
    this.context = context;
    this.venueList = venueList;
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

    Venue venue = this.venueList.get(position);
    holder.row_spn_tv.setText(venue.getVenueName());

    return rowView;
  }


  @Override
  public View getDropDownView(int position, View convertView, ViewGroup parent) {
    View rowView = convertView;
    if (rowView == null) {
      LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      rowView = inflater.inflate(R.layout.row_spn_dropdown, parent, false);
    }

    Venue venue = this.venueList.get(position);
    TextView row_spn_tv = (TextView) rowView.findViewById(R.id.row_spn_tv);
    row_spn_tv.setText(venue.getVenueName());

    return rowView;
  }

}
