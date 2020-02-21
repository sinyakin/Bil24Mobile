package com.bil24.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.bil24.Controller;
import com.bil24.R;
import com.bil24.adapter.OrderAdapter;
import com.bil24.dialog.Dialogs;

import butterknife.Unbinder;
import server.en.OrderStatus;
import server.net.NetException;
import server.net.NetManager;
import server.net.listener.GetOrdersListener;
import server.net.obj.GetOrdersClient;
import server.net.obj.extra.ExtraOrder;

import java.util.ArrayList;

/**
 * User: SVV
 * Date: 24.05.2015.
 */
public class OrderFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, GetOrdersListener {

  @BindView(R.id.orderFragmentListView)
  ListView listView;
  @BindView(R.id.orderFragmentRefresh)
  SwipeRefreshLayout refresh;
  @BindView(R.id.orderFragmentTextViewEmpty)
  TextView textViewEmpty;
  private Unbinder unbinder;

  private OrderAdapter adapter;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_order, container, false);
    unbinder = ButterKnife.bind(this, view);

    if (adapter == null) adapter = new OrderAdapter(getActivity(), new ArrayList<ExtraOrder>());
    listView.setAdapter(adapter);

    textViewEmpty.setVisibility(View.GONE);
    refresh.setOnRefreshListener(this);

    return view;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    if (unbinder != null) unbinder.unbind();
  }

  @Override
  public void onResume() {
    onRefresh();
    super.onResume();
  }

  @Override
  public void onRefresh() {
    refresh.setRefreshing(true);
    NetManager.getOrders(this);
  }

  @Override
  public void onGetOrders(GetOrdersClient clientData) {
    if (!isAdded()) return;
    refresh.setRefreshing(false);

    if (clientData.getOrderList().isEmpty()) {
      textViewEmpty.setVisibility(View.VISIBLE);
      listView.setVisibility(View.GONE);
    } else {
      textViewEmpty.setVisibility(View.GONE);
      listView.setVisibility(View.VISIBLE);
      //----------------------------------------------------------
      //посчитаем кол-во неоплаченнх заказов
      int oderInWait = 0;
      for (ExtraOrder extraOrder : clientData.getOrderList()) {
        if (extraOrder.getStatusExtInt() == OrderStatus.NEW.getId()) oderInWait++;
      }
      Controller.getInstance().updateLeftMenuOrderAmount(oderInWait);
      //----------------------------------------------------------
      adapter.clear();
      adapter.addAll(clientData.getOrderList());
    }
  }

  @Override
  public void onGetOrdersFailed(NetException e) {
    if (!isAdded()) return;
    refresh.setRefreshing(false);
    if (e.isUserMessage()) Dialogs.showSnackBar(getActivity(), e.getMessage());
  }
}