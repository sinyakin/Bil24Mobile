package com.bil24.fragments;

import android.os.*;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.view.*;
import android.widget.*;
import butterknife.*;
import com.bil24.R;
import com.bil24.adapter.mec.MECInfoAdapter;
import com.bil24.dialog.Dialogs;
import com.bil24.storage.Settings;
import com.bil24.storage.sql.DataBase;
import server.net.*;
import server.net.listener.*;
import server.net.obj.*;
import server.net.obj.extra.*;

import java.util.*;

/**
 * User: SVV
 * Date: 24.05.2015.
 */
public class Mec1Fragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, GetMECsListener {

  @BindView(R.id.mec1FragmentRecyclerView)
  RecyclerView recyclerView;
  @BindView(R.id.mec1FragmentRefresh)
  SwipeRefreshLayout refresh;
  @BindView(R.id.mec1FragmentTextViewEmpty)
  TextView textViewEmpty;
  private Unbinder unbinder;

  Integer height;
  Integer width;

  @SuppressWarnings("SuspiciousNameCombination")
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_mec_1, container, false);
    unbinder = ButterKnife.bind(this, view);
    refresh.setOnRefreshListener(this);

    Display display = getActivity().getWindowManager().getDefaultDisplay();
    DisplayMetrics metrics = new DisplayMetrics();
    display.getMetrics(metrics);

    if (metrics.widthPixels < metrics.heightPixels) {
      width = metrics.widthPixels;
      height = metrics.heightPixels;
    } else {
      width = metrics.heightPixels;
      height = metrics.widthPixels;
    }
    width = width - 100;
    height = (height * 15) / 100;

    updateAdapter();
    textViewEmpty.setVisibility(View.GONE);
    if (!SecondFragment.isFromSecondFragment()) onRefresh();
    return view;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    if (unbinder != null) unbinder.unbind();
  }

  @Override
  public void onRefresh() {
    refresh.setRefreshing(true);
    NetManager.getMECs(this, Settings.getMECId(), width, width, height);
  }

  private void updateAdapter() {
    List<ExtraMECInfo> list = DataBase.getInstance(getActivity()).mecInfo.getMECsInfo();
    if (list.isEmpty()) {
      textViewEmpty.setVisibility(View.VISIBLE);
      recyclerView.setVisibility(View.GONE);
    } else {
      textViewEmpty.setVisibility(View.GONE);
      recyclerView.setVisibility(View.VISIBLE);
      StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(getContext().getResources().getInteger(R.integer.action_ticket_count), StaggeredGridLayoutManager.VERTICAL);
      recyclerView.setLayoutManager(layoutManager);
      MECInfoAdapter adapter = new MECInfoAdapter(getActivity(), list);
      recyclerView.setAdapter(adapter);
    }
    Settings.setMECInfoSize(list.size());
  }

  @Override
  public void onGetMEC(GetMECsClient clientData) {
    if (!isAdded()) return;
    refresh.setRefreshing(false);

    DataBase db = DataBase.getInstance(getActivity());

    long maxMECId = Settings.getMECId();
    for (ExtraMECInfo extraMECInfo : clientData.getList()) {
      for (ExtraMEC extraMEC : extraMECInfo.getMecList()) {
        maxMECId = Math.max(maxMECId, extraMEC.getMecId());
        ExtraMEC dbExtraMec = db.mec.getMECById(extraMEC.getMecId());
        if (dbExtraMec == null) {
          db.mec.addMEC(extraMEC.getMecId(), extraMECInfo.getActionEventId(), extraMEC.getPrice(),
              extraMEC.getCategoryName(), extraMEC.getQrCodeImg(), extraMEC.getBarCodeImg(), extraMEC.getBarCodeNumber());
        }
      }
      ExtraMECInfo dbExtraMECInfo = db.mecInfo.getMECInfoByActionEventId(extraMECInfo.getActionEventId());
      if (dbExtraMECInfo == null) {
        db.mecInfo.addMecInfo(extraMECInfo);
      } else {
        db.mecInfo.updateData(extraMECInfo);
      }
    }
    Settings.setMECId(maxMECId);
    updateAdapter();
  }

  @Override
  public void onGetMECFailed(NetException e) {
    if (!isAdded()) return;
    refresh.setRefreshing(false);
    if (e.isUserMessage()) Dialogs.showSnackBar(getActivity(), e.getMessage());
  }
}