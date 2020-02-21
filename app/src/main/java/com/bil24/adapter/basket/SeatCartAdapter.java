package com.bil24.adapter.basket;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import android.widget.*;
import butterknife.*;
import com.bil24.R;
import com.bil24.dialog.Dialogs;
import com.bil24.storage.Settings;
import com.bil24.utils.Utils;

import server.net.NetException;
import server.net.NetManager;
import server.net.listener.ReservationListener;
import server.net.obj.ReservationClient;
import server.net.obj.extra.ExtraActionEventCart;
import server.net.obj.extra.ExtraSeatCart;

import java.util.*;

class SeatCartAdapter extends ArrayAdapter<ExtraSeatCart> {
  private Context context;
  private List<ExtraSeatCart> extraSeatList;
  private BasketListener basketListener;
  private ExtraActionEventCart actionEvent;
  private ListViewHeightListener listViewHeightListener;

  SeatCartAdapter(Context context, ExtraActionEventCart actionEvent, BasketListener basketListener, ListViewHeightListener listViewHeightListener) {
    super(context, R.layout.adapter_basket_seat, actionEvent.getSeatList());
    this.context = context;
    this.extraSeatList = actionEvent.getSeatList();
    this.actionEvent = actionEvent;
    this.basketListener = basketListener;
    this.listViewHeightListener = listViewHeightListener;
  }

  static class ViewHolder {
    @BindView(R.id.basketSeatName)
    com.rey.material.widget.TextView textViewSeatName;

    @BindView(R.id.basketSeatTextViewPrice)
    com.rey.material.widget.TextView textViewPrice;

    @BindView(R.id.basketSeatTextViewWithDiscount)
    com.rey.material.widget.TextView textViewWithDiscount;
    @BindView(R.id.basketSeatTextViewDiscountReason)
    com.rey.material.widget.TextView textViewDiscountReason;

    @BindView(R.id.basketSeatButtonUnReservePlace)
    com.rey.material.widget.Button buttonUnReservePlace;

    public ViewHolder(View view) {
      ButterKnife.bind(this, view);
    }
  }

  @SuppressLint("SetTextI18n")
  @Override
  @NonNull
  public View getView(final int position, final View convertView, @NonNull ViewGroup parent) {
    final ViewHolder holder;

    View rowView = convertView;
    if (rowView == null) {
      LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      rowView = inflater.inflate(R.layout.adapter_basket_seat, parent, false);
      holder = new ViewHolder(rowView);
      rowView.setTag(holder);
    } else {
      holder = (ViewHolder) rowView.getTag();
    }

    final ExtraSeatCart seatCart = extraSeatList.get(position);
    if (seatCart.getRow() != null && seatCart.getSector() != null && seatCart.getNumber() != null) {
      holder.textViewSeatName.setText(seatCart.getSector() + ", " + seatCart.getRow() + ", " + seatCart.getNumber());
    } else {
      holder.textViewSeatName.setText("Категория: " + seatCart.getCategoryPriceName());
    }
    holder.textViewPrice.setText(Utils.sumFormat.format(seatCart.getNominal()) + " руб.");

    if (seatCart.isDiscount()) {
      holder.textViewWithDiscount.setVisibility(View.VISIBLE);
      holder.textViewDiscountReason.setVisibility(View.VISIBLE);
      holder.textViewWithDiscount.setText(Utils.sumFormat.format(seatCart.getPrice()) + " руб.");
      holder.textViewDiscountReason.setText(seatCart.getDiscountReason() == null ? "" : seatCart.getDiscountReason());
      holder.textViewPrice.setPaintFlags(holder.textViewPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    } else {
      holder.textViewWithDiscount.setVisibility(View.GONE);
      holder.textViewDiscountReason.setVisibility(View.GONE);
      holder.textViewPrice.setPaintFlags(holder.textViewPrice.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
    }

    holder.buttonUnReservePlace.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        holder.buttonUnReservePlace.setEnabled(false);
        basketListener.blockInterface();

        NetManager.unReserve(new ReservationListener() {
          @Override
          public void onReservation(ReservationClient clientData) {
            basketListener.unBlockInterface();
            switch (clientData.getType()) {
              case RESERVE:
              case RESERVE_BY_PLACE:
//                Settings.incSeatInReserve();
//                basketListener.changeSeatChecked(seatCart.getSeatId(), true);
                break;
              case UN_RESERVE:
              case UN_RESERVE_ALL:
                Settings.decSeatInReserve();
                ((AppCompatActivity) context).runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                    SeatCartAdapter.this.remove(seatCart);
                    listViewHeightListener.change(extraSeatList.isEmpty() ? actionEvent : null, Collections.singletonList(seatCart.getSeatId()));
                  }
                });
                break;
            }
          }

          @Override
          public void onReservationFailed(NetException e) {
            basketListener.unBlockInterface();
            holder.buttonUnReservePlace.setEnabled(true);
            if (e.isUserMessage()) Dialogs.showSnackBar((AppCompatActivity) context, e.getMessage());
          }
        }, Collections.singletonList(seatCart.getSeatId()));
      }
    });
    return rowView;
  }
}