package com.bil24.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import android.widget.*;
import butterknife.*;
import com.bil24.R;
import com.bil24.fragments.action.filter.City;

import java.util.*;

/**
 * Created by SVV on 28.10.2016
 */

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.ViewHolder> {
  private Context context;
  private List<City> list = new ArrayList<>();
  private int mSelectedItem = -1;
  private AdapterView.OnItemClickListener onItemClickListener;

  public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private CityAdapter cityAdapter;
    @BindView(R.id.cityAdapterTextView)
    com.rey.material.widget.TextView cityNameTextView;
    @BindView(R.id.cityAdapterCardView)
    android.support.v7.widget.CardView cardView;

    public ViewHolder(View view, CityAdapter cityAdapter) {
      super(view);
      this.cityAdapter = cityAdapter;
      ButterKnife.bind(this, view);
      view.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
      mSelectedItem = getAdapterPosition();
      notifyItemRangeChanged(0, list.size());
      cityAdapter.onItemHolderClick(this);
    }
  }

  public CityAdapter(Context context, List<City> list) {
    this.context = context;
    this.list = list;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_city, parent, false);
    return new ViewHolder(view, this);
  }

  public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
    this.onItemClickListener = onItemClickListener;
  }

  private void onItemHolderClick(CityAdapter.ViewHolder holder) {
    if (onItemClickListener != null) onItemClickListener.onItemClick(null, holder.itemView, holder.getAdapterPosition(), holder.getItemId());
  }

  @Override
  public void onBindViewHolder(final ViewHolder holder, int position) {
    City city = list.get(position);
    holder.cityNameTextView.setText(city.getCityName());
    if (position == mSelectedItem) {
      holder.cityNameTextView.setTextColor(Color.WHITE);
      holder.cityNameTextView.setBackgroundColor(context.getResources().getColor(R.color.toolbar_lite));
    } else {
      holder.cityNameTextView.setTextColor(Color.DKGRAY);
      holder.cityNameTextView.setBackgroundColor(Color.WHITE);
    }
  }

  public List<City> getList() {
    return list;
  }

  @Override
  public int getItemCount() {
    return list.size();
  }
}
