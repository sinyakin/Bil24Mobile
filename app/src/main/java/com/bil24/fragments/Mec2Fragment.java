package com.bil24.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.ListView;

import butterknife.*;

import com.bil24.Controller;
import com.bil24.R;
import com.bil24.adapter.mec.MECAdapter;
import com.bil24.dialog.MECsQrDialogFragment;
import com.bil24.storage.sql.DataBase;
import server.net.obj.extra.ExtraMEC;

import java.util.*;

/**
 * Created by SVV on 02.11.2016
 */

public class Mec2Fragment extends SecondFragment {
  private static final String ARGUMENT_ACTION_EVENT_ID = "ARGUMENT_ACTION_EVENT_ID";
  private static final String ARGUMENT_CITY_ID = "ARGUMENT_CITY_ID";
  private static final String ARGUMENT_ACTION_NAME = "ARGUMENT_ACTION_NAME";
  private static final String ARGUMENT_VENUE_NAME = "ARGUMENT_VENUE_NAME";
  private static final String ARGUMENT_CITY_PASS = "ARGUMENT_CITY_PASS";

  private List<ExtraMEC> list = new ArrayList<>();

  @BindView(R.id.mec2FragmentButtonDiscount)
  com.rey.material.widget.Button buttonDiscount;
  @BindView(R.id.mec2FragmentButtonShowAllQrCode)
  com.rey.material.widget.Button buttonAsQr;
  @BindView(R.id.mec2FragmentListView)
  ListView listView;

  public static Fragment newInstance(long actionEventId, long cityId, String actionName, String venueName, boolean cityPass) {
    Fragment fragment = new Mec2Fragment();
    Bundle bundle = new Bundle();
    bundle.putLong(ARGUMENT_ACTION_EVENT_ID, actionEventId);
    bundle.putLong(ARGUMENT_CITY_ID, cityId);
    bundle.putString(ARGUMENT_ACTION_NAME, actionName);
    bundle.putString(ARGUMENT_VENUE_NAME, venueName);
    bundle.putBoolean(ARGUMENT_CITY_PASS, cityPass);
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_mec_2, container, false);
    unbinder = ButterKnife.bind(this, view);

    long actionEventId = getArguments().getLong(ARGUMENT_ACTION_EVENT_ID, -1);
    final long cityId = getArguments().getLong(ARGUMENT_CITY_ID, -1);
    final String actionName = getArguments().getString(ARGUMENT_ACTION_NAME);
    final String venueName = getArguments().getString(ARGUMENT_VENUE_NAME);
    final boolean cityPass = getArguments().getBoolean(ARGUMENT_CITY_PASS);
    getActivity().setTitle("Карта \"" + actionName + "\"");

    final MECAdapter adapter = new MECAdapter(getActivity(), actionName, venueName, list);
    listView.setAdapter(adapter);

    List<ExtraMEC> mecList = DataBase.getInstance(getActivity()).mec.getMECsByActionEventId(actionEventId);
    if (!mecList.isEmpty()) {
      this.list.addAll(mecList);
      adapter.notifyDataSetChanged();
    }

    if (!cityPass) buttonDiscount.setVisibility(View.GONE);

    buttonDiscount.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Controller.getInstance().showCityPassFragment(cityId);
      }
    });

    buttonAsQr.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        //открыть все карты как qr коды
        MECsQrDialogFragment qrDialogFragment = MECsQrDialogFragment.create(new ArrayList<>(Mec2Fragment.this.list), actionName);
        qrDialogFragment.show(getActivity().getSupportFragmentManager(), null);
      }
    });
    return view;
  }
}
