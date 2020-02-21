package com.bil24.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.*;
import android.widget.*;
import butterknife.*;
import com.bil24.*;
import com.bil24.R;
import com.bil24.fragments.action.filter.Kind;
import com.bil24.utils.Utils;
import com.squareup.picasso.Picasso;
import server.net.obj.extra.ExtraActionV2;

import java.util.*;

public class ActionAdapter extends ArrayAdapter<ExtraActionV2> {
  private Context context;
  private List<ExtraActionV2> actionList = new ArrayList<>();

  public ActionAdapter(Context context, List<ExtraActionV2> actionList) {
    super(context, R.layout.adapter_action, actionList);
    this.context = context;
    this.actionList = actionList;
  }

  static class ViewHolder {
    @BindView(R.id.rlAdapterAction)
    LinearLayout rlAdapterAction;

    @BindView(R.id.ivAdapterActionPoster1)
    ImageView ivAdapterActionPoster1;

    @BindView(R.id.tvAdapterActionTitle)
    TextView tvAdapterActionTitle;

    @BindView(R.id.tvAdapterActionDates)
    TextView tvAdapterActionDates;

    @BindView(R.id.tvAdapterActionTime)
    TextView tvAdapterActionTime;

    @BindView(R.id.tvAdapterActionMinSum)
    TextView tvAdapterActionMinSum;

    @BindView(R.id.tvAdapterActionDescription)
    TextView tvAdapterDescription;

    @BindView(R.id.tvAdapterActionVenue)
    TextView tvAdapterVenue;

    @BindView(R.id.tvAdapterActionRating)
    TextView tvAdapterRating;

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
      rowView = inflater.inflate(R.layout.adapter_action, parent, false);
      holder = new ViewHolder(rowView);
      rowView.setTag(holder);
    } else {
      holder = (ViewHolder) rowView.getTag();
    }

    final ExtraActionV2 action = this.actionList.get(position);
    holder.tvAdapterActionTitle.setText(action.getActionName());
    holder.tvAdapterActionMinSum.setText("от " + Utils.formatedRubNotKop(action.getMinSum()));
    holder.tvAdapterDescription.setText(action.getFullActionName());

    if (action.getKindId() != Kind.Type.MEC) {
      String dateInfo = action.getFirstEventDate();
      if (!action.getFirstEventDate().equals(action.getLastEventDate())) dateInfo += " - " + action.getLastEventDate();
      holder.tvAdapterActionDates.setText(dateInfo);
      holder.tvAdapterActionDates.setVisibility(View.VISIBLE);
    } else {
      holder.tvAdapterActionDates.setText("");
      holder.tvAdapterActionDates.setVisibility(View.GONE);
    }

    holder.rlAdapterAction.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Controller.getInstance().openPage2(action);
      }
    });

    if (action.getRating() == 9 || action.getRating() == 10) {
      holder.tvAdapterRating.setVisibility(View.VISIBLE);
      holder.tvAdapterRating.setText("рейтинг " + action.getRating());
    } else {
      holder.tvAdapterRating.setVisibility(View.GONE);
    }

    if (action.getVenueMap().size() == 1) {
      holder.tvAdapterVenue.setVisibility(View.VISIBLE);
      holder.tvAdapterVenue.setText(action.getVenueMap().values().iterator().next());
    } else {
      holder.tvAdapterVenue.setVisibility(View.GONE);
    }

    if (action.getKindId() != Kind.Type.MEC && action.getActionEventTime() != null) {
      holder.tvAdapterActionTime.setVisibility(View.VISIBLE);
      holder.tvAdapterActionTime.setText(action.getActionEventTime());
    } else {
      holder.tvAdapterActionTime.setVisibility(View.GONE);
    }

    Picasso.with(getContext())
        .load(action.getSmallPosterUrl())
        .placeholder(R.drawable.poster1)
        .error(R.drawable.poster1).fit()
        .into(holder.ivAdapterActionPoster1);

    return rowView;
  }

}