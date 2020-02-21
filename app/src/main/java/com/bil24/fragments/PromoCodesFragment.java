package com.bil24.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.bil24.Controller;
import com.bil24.R;
import com.bil24.adapter.promo.ExtraPromo;
import com.bil24.adapter.promo.PromoCodesAdapter;
import com.bil24.dialog.Dialogs;
import com.bil24.storage.Settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import server.net.NetException;
import server.net.NetManager;
import server.net.listener.GetPromoCodesListener;
import server.net.obj.GetPromoCodesClient;
import server.net.obj.extra.ExtraPromoCode;
import server.net.obj.extra.ExtraPromoPack;

/**
 * User: SVV
 * Date: 24.05.2015.
 */
public class PromoCodesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, GetPromoCodesListener {

  @BindView(R.id.promoCodesFragmentListView)
  ListView listView;
  @BindView(R.id.promoCodesFragmentRefresh)
  SwipeRefreshLayout refresh;
  @BindView(R.id.promoCodesFragmentTextViewEmpty)
  TextView textViewEmpty;
  private Unbinder unbinder;

  private PromoCodesAdapter adapter;

  @SuppressWarnings("SuspiciousNameCombination")
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_promo_codes, container, false);
    unbinder = ButterKnife.bind(this, view);

    if (adapter == null) adapter = new PromoCodesAdapter(getActivity(), new ArrayList<ExtraPromo>());
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
    NetManager.getPromoCodes(this);
  }

  @Override
  public void onGetPromoCodes(GetPromoCodesClient clientData) {
    if (!isAdded()) return;
    refresh.setRefreshing(false);

    /*for (ExtraPromoPack extraPromoPack : clientData.getPromoPackList()) {
      Log.d("!!!!!", extraPromoPack.getName() + " " + extraPromoPack.getDescription() + " " + extraPromoPack.getStartTime() + " " + extraPromoPack.getEndTime() + " " + extraPromoPack.getDiscountPercent() + " " + extraPromoPack.isActiveNow());
      for (ExtraPromoCode extraPromoCode : extraPromoPack.getPromoCodeList()) {
        Log.d("!!!!!", "-->" + extraPromoCode.getCode() + " " + extraPromoCode.isUsed() + " " + extraPromoCode.isSpent());
      }
    }*/

    if (clientData.getPromoPackList().isEmpty()) {
      Settings.setPromoCodes(0);
      textViewEmpty.setVisibility(View.VISIBLE);
      listView.setVisibility(View.GONE);
    } else {
      textViewEmpty.setVisibility(View.GONE);
      listView.setVisibility(View.VISIBLE);
      adapter.clear();
      List<ExtraPromo> list = new ArrayList<>();
      int countActive = 0;
      for (ExtraPromoPack promoPack : clientData.getPromoPackList()) {
        for (ExtraPromoCode extraPromoCode : promoPack.getPromoCodeList()) {
          list.add(new ExtraPromo(promoPack, extraPromoCode));
          boolean active = promoPack.isActiveNow() && !extraPromoCode.isSpent() && !extraPromoCode.isUsed();
          if (active) countActive++;
        }
      }
      Settings.setPromoCodes(countActive);
      Collections.sort(list, new Comparator<ExtraPromo>() {
        @Override
        public int compare(ExtraPromo t1, ExtraPromo t2) {
          boolean active1 = t1.getExtraPromoPack().isActiveNow() && !t1.getExtraPromoCode().isSpent() && !t1.getExtraPromoCode().isUsed();
          boolean active2 = t2.getExtraPromoPack().isActiveNow() && !t2.getExtraPromoCode().isSpent() && !t2.getExtraPromoCode().isUsed();
          return PromoCodesFragment.compare(active2, active1);
        }
      });
      adapter.addAll(list);
    }
  }

  public static int compare(boolean x, boolean y) {
    return (x == y) ? 0 : (x ? 1 : -1);
  }

  @Override
  public void onGetPromoCodesFailed(NetException e) {
    if (!isAdded()) return;
    refresh.setRefreshing(false);
    if (e.isUserMessage()) Dialogs.showSnackBar(getActivity(), e.getMessage());
  }
}