package com.bil24.adapter;

import android.annotation.SuppressLint;
import android.content.*;
import android.support.annotation.NonNull;
import android.view.*;
import android.widget.*;
import butterknife.*;
import com.bil24.*;
import com.bil24.R;
import com.squareup.picasso.Picasso;
import server.net.obj.extra.*;

import java.util.*;

public class VenueCityPassAdapter extends ArrayAdapter<ExtraVenue> {
  private Context context;
  private List<ExtraVenue> list = new ArrayList<>();

  public VenueCityPassAdapter(Context context, List<ExtraVenue> list) {
    super(context, R.layout.adapter_venue_city_pass, list);
    this.context = context;
    this.list = list;
  }

  static class ViewHolder {
    @BindView(R.id.venueCityPassAdapterMainPanel)
    LinearLayout mainPanel;
    @BindView(R.id.venueCityPassAdapterVenuePoster)
    ImageView poster;
    @BindView(R.id.venueCityPassAdapterTvName)
    TextView tvName;
    @BindView(R.id.venueCityPassAdapterTvAddress)
    TextView tvAddress;

    public ViewHolder(View view) {
      ButterKnife.bind(this, view);
    }
  }

  @NonNull
  @SuppressLint("SetTextI18n")
  @Override
  public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
    ViewHolder holder;

    View rowView = convertView;
    if (rowView == null) {
      LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      rowView = inflater.inflate(R.layout.adapter_venue_city_pass, parent, false);
      holder = new ViewHolder(rowView);
      rowView.setTag(holder);
    } else {
      holder = (ViewHolder) rowView.getTag();
    }

    final ExtraVenue venue = this.list.get(position);
    holder.tvName.setText(venue.getVenueName());
    holder.tvAddress.setText(venue.getAddress());

    holder.mainPanel.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Controller.getInstance().showCityPassVenueFragment(venue);
      }
    });

    Picasso.with(getContext())
        .load(venue.getImageUrl())
        .placeholder(R.drawable.poster1)
        .error(R.drawable.poster1).fit()
        .into(holder.poster);

    return rowView;
  }
}