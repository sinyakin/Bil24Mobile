package com.bil24.adapter.basket;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import android.widget.*;
import butterknife.*;
import com.bil24.*;
import com.bil24.R;
import com.bil24.dialog.*;
import com.bil24.fragments.action.filter.Kind;
import com.bil24.storage.Settings;
import com.squareup.picasso.Picasso;
import server.net.*;
import server.net.listener.ReservationListener;
import server.net.obj.ReservationClient;
import server.net.obj.extra.*;

import java.util.*;

public class BasketAdapter extends ArrayAdapter<ExtraActionEventCart> implements ListViewHeightListener {
  private Context context;
  private boolean discount;
  private List<ExtraActionEventCart> actionEventList;
  private BasketListener basketListener;

  public BasketAdapter(Context context, boolean discount, List<ExtraActionEventCart> actionEventList, BasketListener basketListener) {
    super(context, R.layout.adapter_basket, actionEventList);
    this.context = context;
    this.discount = discount;
    this.actionEventList = actionEventList;
    this.basketListener = basketListener;
  }

  static class ViewHolder {
    @BindView(R.id.basketAdapterPoster)
    ImageView poster;

    @BindView(R.id.basketAdapterButtonUnReserve)
    com.rey.material.widget.Button buttonUnReserve;

    @BindView(R.id.basketVenueTextViewFullName)
    com.rey.material.widget.TextView textViewFullName;

    @BindView(R.id.basketVenueTextViewVenueName)
    com.rey.material.widget.TextView textViewVenueName;

    @BindView(R.id.basketVenueTextViewDay)
    com.rey.material.widget.TextView textViewDay;
    @BindView(R.id.basketVenueTextViewTime)
    com.rey.material.widget.TextView textViewTime;

    @BindView(R.id.basketVenueListViewSeat)
    ListView listView;

    public ViewHolder(View view) {
      ButterKnife.bind(this, view);
    }
  }

  @SuppressLint("SetTextI18n")
  @Override
  @NonNull
  public View getView(int position, View convertView, @NonNull ViewGroup parent) {
    final ViewHolder holder;

    View rowView = convertView;
    if (rowView == null) {
      LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      rowView = inflater.inflate(R.layout.adapter_basket, parent, false);
      holder = new ViewHolder(rowView);
      rowView.setTag(holder);
    } else {
      holder = (ViewHolder) rowView.getTag();
    }

    final ExtraActionEventCart actionEvent = actionEventList.get(position);
    holder.textViewFullName.setText(actionEvent.getActionName());
    holder.textViewVenueName.setText(actionEvent.getVenueName() + "(" + actionEvent.getCityName() + ")");
    if (actionEvent.getKindId() != Kind.Type.MEC) {
      holder.textViewDay.setText(actionEvent.getDay());
      holder.textViewTime.setText(actionEvent.getTime());
      holder.textViewDay.setVisibility(View.VISIBLE);
      holder.textViewTime.setVisibility(View.VISIBLE);
    } else {
      holder.textViewDay.setText("");
      holder.textViewTime.setText("");
      holder.textViewDay.setVisibility(View.GONE);
      holder.textViewTime.setVisibility(View.GONE);
    }

    holder.listView.setAdapter(new SeatCartAdapter(getContext(), actionEvent, basketListener, this));
    holder.buttonUnReserve.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Dialogs.showConfirmDialog(((AppCompatActivity) context).getSupportFragmentManager(),
            "Удалить все зарезервированные места на \"" + actionEvent.getActionName() + "\"", new ConfirmListener() {
          @Override
          public void positiveConfirm() {
            final List<Long> seatIdList = new ArrayList<>();
            for (ExtraSeatCart extraSeatCart : actionEvent.getSeatList()) {
              seatIdList.add(extraSeatCart.getSeatId());
            }
            basketListener.blockInterface();
            NetManager.unReserveAll(new ReservationListener() {
              @Override
              public void onReservation(ReservationClient clientData) {
                basketListener.unBlockInterface();
                change(actionEvent, seatIdList);
              }

              @Override
              public void onReservationFailed(NetException e) {
                basketListener.unBlockInterface();
                if (e.isUserMessage()) Dialogs.showSnackBar((AppCompatActivity) context, e.getMessage());
              }
            }, actionEvent.getActionEventId());
          }

          @Override
          public void negativeConfirm() {}
        });
      }
    });

    setListViewHeightBasedOnChildren(holder.listView);

    Picasso.with(getContext())
            .load(actionEvent.getSmallPosterUrl())
            .placeholder(R.drawable.poster2)
            .error(R.drawable.poster2).fit()
            .into(holder.poster);
    return rowView;
  }

  //ListView in ListView
  //Если ничего не делать, то отображатеся только первый элемент вложенного ListView
  //чтобы все поправить, нужно пересчитать размеры каждого элемента внутри адаптера
  public void setListViewHeightBasedOnChildren(ListView listView) {
    SeatCartAdapter listAdapter = (SeatCartAdapter) listView.getAdapter();
    if (listAdapter == null) return;

    int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
    int totalHeight = 0;
    View view = null;
    for (int i = 0; i < listAdapter.getCount(); i++) {
      view = listAdapter.getView(i, view, listView);
      if (i == 0) view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, RelativeLayout.LayoutParams.WRAP_CONTENT));

      RelativeLayout relativeLayout = (RelativeLayout) view;
      for (int j = 0; j < relativeLayout.getChildCount(); j++) {
        View inView = relativeLayout.getChildAt(j);
        inView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) inView.getLayoutParams();
        params.height = inView.getMeasuredHeight();
        inView.setLayoutParams(params);
        inView.requestLayout();
      }
      view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
      totalHeight += view.getMeasuredHeight();
    }

    ViewGroup.LayoutParams params = listView.getLayoutParams();
    params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
    listView.setLayoutParams(params);
    listView.requestLayout();
  }

  @Override
  public void change(@Nullable ExtraActionEventCart actionEvent, @NonNull List<Long> seatIdList) {
    notifyDataSetChanged();
    if (actionEvent != null) {
      remove(actionEvent);
      if (actionEventList.isEmpty()) {
        Settings.setSeatInReserve(0);
        Controller.getInstance().showActionPageFragment();
        return;
      }
    }
    if (discount) Dialogs.startProcess(context, "Перерасчет скидки...");
    basketListener.changeSeatChecked(seatIdList, false, discount);
  }
}
