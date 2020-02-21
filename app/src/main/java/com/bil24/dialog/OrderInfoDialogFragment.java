package com.bil24.dialog;

import android.os.Bundle;
import android.view.*;
import android.widget.GridView;
import butterknife.*;
import com.bil24.R;
import com.bil24.adapter.OrderInfoAdapter;
import com.bil24.myelement.*;
import server.net.*;
import server.net.listener.GetOrderInfoListener;
import server.net.obj.GetOrderInfoClient;

/**
 * User: SVV
 * Date: 24.05.2015.
 */
public class OrderInfoDialogFragment extends MyDialogFragment implements GetOrderInfoListener {
  private static final String ORDER_ID = "EXTRA_ORDER_ID";

  @BindView(R.id.orderInfoDialogGridView)
  GridView gridView;

  @BindView(R.id.orderInfoDialogLoadPagePanel)
  MyLoadingPanel loadingPanel;

  public static OrderInfoDialogFragment create(long orderId) {
    OrderInfoDialogFragment ticketDialogFragment = new OrderInfoDialogFragment();
    Bundle bundle = new Bundle();
    bundle.putLong(ORDER_ID, orderId);
    ticketDialogFragment.setArguments(bundle);
    return ticketDialogFragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.dialog_order_info, container, false);
    ButterKnife.bind(this, view);

    gridView.setVisibility(View.GONE);
    loadingPanel.setText("Загрузка содержимого заказа...");
    loadingPanel.setVisibility(View.VISIBLE);

    long orderId = getArguments().getLong(ORDER_ID);
    getDialog().setTitle("Заказ № " + orderId);

    NetManager.getOrderInfo(this, orderId);
    return view;
  }

  @Override
  public void onGetOrderInfo(GetOrderInfoClient clientData) {
    if (!isAdded()) return;
    loadingPanel.setVisibility(View.GONE);
    gridView.setVisibility(View.VISIBLE);
    OrderInfoAdapter orderInfoAdapter = new OrderInfoAdapter(getActivity(), clientData.getOrderInfoList());
    gridView.setAdapter(orderInfoAdapter);
  }

  @Override
  public void onGetOrderInfoFailed(NetException e) {
    if (!isAdded()) return;
    if (e.isUserMessage()) Dialogs.showSnackBar(getActivity(), e.getMessage());
    dismiss();
  }
}