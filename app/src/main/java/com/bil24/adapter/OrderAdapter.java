package com.bil24.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import android.widget.*;
import butterknife.*;
import com.bil24.*;
import com.bil24.R;
import com.bil24.dialog.*;
import com.bil24.storage.Settings;
import com.bil24.utils.Utils;
import server.en.OrderStatus;
import server.net.*;
import server.net.listener.DeleteListener;
import server.net.obj.DeleteClient;
import server.net.obj.extra.ExtraOrder;

import java.math.BigDecimal;
import java.util.List;

public class OrderAdapter extends ArrayAdapter<ExtraOrder> {
  private static final BigDecimal ZERO = BigDecimal.ZERO;
  private Context context;
  private List<ExtraOrder> actionList;

  public OrderAdapter(Context context, List<ExtraOrder> actionList) {
    super(context, R.layout.adapter_order, actionList);
    this.context = context;
    this.actionList = actionList;
  }

  static class ViewHolder {
    @BindView(R.id.orderAdapterNumber)
    TextView textViewNumber;

    @BindView(R.id.orderAdapterDate)
    TextView textViewDate;

    @BindView(R.id.orderAdapterTickets)
    TextView textViewTickets;

    @BindView(R.id.orderAdapterDiscount)
    TextView textViewDiscount;

    @BindView(R.id.orderAdapterCharge)
    TextView textViewCharge;

    @BindView(R.id.orderAdapterSum)
    TextView textViewSum;

    @BindView(R.id.orderAdapterButtonBuy)
    com.rey.material.widget.Button button;

    @BindView(R.id.orderAdapterButtonDelete)
    com.rey.material.widget.Button buttonDelete;

    @BindView(R.id.orderAdapterButtonPrint)
    ImageButton printButton;

    @BindView(R.id.orderAdapterMainPanel)
    RelativeLayout mainPanel;

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
      rowView = inflater.inflate(R.layout.adapter_order, parent, false);
      holder = new ViewHolder(rowView);
      rowView.setTag(holder);
    } else {
      holder = (ViewHolder) rowView.getTag();
    }

    final ExtraOrder order = this.actionList.get(position);
    holder.textViewNumber.setText("Заказ № " + order.getOrderId());
    holder.textViewDate.setText("Создан: " + order.getDate());
    holder.textViewTickets.setText("Количество: " + order.getQuantity());
    if (order.getDiscount().compareTo(ZERO) > 0) {
      holder.textViewDiscount.setVisibility(View.VISIBLE);
      holder.textViewDiscount.setText("Скидка: " + Utils.formatedRub(order.getDiscount()));
    } else {
      holder.textViewDiscount.setVisibility(View.GONE);
    }
    if (order.getServiceCharge().compareTo(ZERO) > 0) {
      holder.textViewCharge.setVisibility(View.VISIBLE);
      holder.textViewCharge.setText("Сервисный сбор: " + Utils.formatedRub(order.getServiceCharge()));
    } else {
      holder.textViewCharge.setVisibility(View.GONE);
    }
    holder.textViewSum.setText("Цена: " + Utils.formatedRub(order.getSum()));

    holder.mainPanel.setOnClickListener(v -> {
      OrderInfoDialogFragment dialogFragment = OrderInfoDialogFragment.create(order.getOrderId());
      dialogFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), null);
    });

    if (Controller.getInstance().canWorkAsPrintTerminal(getContext()) &&
        (order.getStatusExtInt() == OrderStatus.NEW.getId() || order.getStatusExtInt() == OrderStatus.PAID.getId() || order.getStatusExtInt() == OrderStatus.PROCESSING.getId())) {
      holder.printButton.setVisibility(View.VISIBLE);
      holder.printButton.setOnClickListener(view -> Dialogs.showPrintCheckDialog(((AppCompatActivity) context).getSupportFragmentManager(), order.getOrderId(), order.getStatusExtInt()));
    } else {
      holder.printButton.setVisibility(View.GONE);
    }

    if (order.getStatusExtInt() == OrderStatus.NEW.getId()) {
      if (Settings.needPayment(order.getOrderId())) {
        holder.button.setEnabled(true);
        holder.button.setText("Оплатить заказ");
        holder.button.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            if (order.getFormUrl() == null) return;
            Controller.getInstance().showAlfaActivity(order.getOrderId(), order.getFormUrl(), false);
          }
        });
      } else {
        holder.button.setEnabled(false);
        holder.button.setText("в процессе...");
      }
    } else {
      holder.button.setEnabled(false);
      holder.button.setText(order.getUserMessage());
    }

    if (order.getStatusExtInt() == OrderStatus.NEW.getId()) {
      holder.buttonDelete.setVisibility(View.GONE);
    } else {
      holder.buttonDelete.setVisibility(View.VISIBLE);
      holder.buttonDelete.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Dialogs.showConfirmDialog(((AppCompatActivity) context).getSupportFragmentManager(),
              "Удалить заказ № \"" + order.getOrderId() + "\"", new ConfirmListener() {
                @Override
                public void positiveConfirm() {
                  OrderAdapter.this.remove(order);
                  NetManager.deleteOrder(new DeleteListener() {
                    @Override
                    public void onDelete(DeleteClient deleteClient) {
                    }

                    @Override
                    public void onDeleteFailed(NetException e) {
                      if (e.isUserMessage()) {
                        OrderAdapter.this.add(order);
                        Dialogs.showSnackBar((Activity) OrderAdapter.this.getContext(), e.getMessage());
                      }
                    }
                  }, order.getOrderId());
                }

                @Override
                public void negativeConfirm() {
                }
              });
        }
      });
    }
    return rowView;
  }
}
