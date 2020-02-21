package com.bil24.activity;

import android.content.*;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import android.widget.ListView;

import butterknife.*;
import com.bil24.R;
import com.bil24.adapter.mec.MECAdapter;
import com.bil24.dialog.*;
import com.bil24.net.AndroidSender;
import com.bil24.storage.sql.DataBase;
import server.net.*;
import server.net.obj.extra.*;

import java.util.*;

@Deprecated
@SuppressWarnings("SuspiciousNameCombination")
public class MECsActivity extends AppCompatActivity {
  private static final String EXTRA_ACTION_EVENT_ID = "EXTRA_ACTION_EVENT_ID";
  private static final String EXTRA_ACTION_NAME = "EXTRA_ACTION_NAME";
  private static final String EXTRA_VENUE_NAME = "EXTRA_VENUE_NAME";
  private List<ExtraMEC> list = new ArrayList<>();

  @BindView(R.id.mecActivityButtonShowAllQrCode)
  com.rey.material.widget.Button buttonAsQr;

  @BindView(R.id.mecActivityListView)
  ListView listView;

  public static Intent getStartIntent(Context packageContext, long actionEventId, String actionName, String venueName) {
    Intent intent = new Intent(packageContext, MECsActivity.class);
    intent.putExtra(EXTRA_ACTION_EVENT_ID, actionEventId);
    intent.putExtra(EXTRA_ACTION_NAME, actionName);
    intent.putExtra(EXTRA_VENUE_NAME, venueName);
    return intent;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_mec);
    ButterKnife.bind(this);
    NetManager.init(new AndroidSender());

    if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    long actionEventId = getIntent().getLongExtra(EXTRA_ACTION_EVENT_ID, -1);
    final String actionName = getIntent().getStringExtra(EXTRA_ACTION_NAME);
    final String venueName = getIntent().getStringExtra(EXTRA_VENUE_NAME);

    setTitle("Карта \"" + actionName + "\"");

    final MECAdapter adapter = new MECAdapter(this, actionName, venueName, list);
    listView.setAdapter(adapter);

    List<ExtraMEC> mecList = DataBase.getInstance(this).mec.getMECsByActionEventId(actionEventId);
    if (!mecList.isEmpty()) {
      this.list.addAll(mecList);
      adapter.notifyDataSetChanged();
    }

    buttonAsQr.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        //открыть все карты как qr коды
        MECsQrDialogFragment qrDialogFragment = MECsQrDialogFragment.create(new ArrayList<>(MECsActivity.this.list), actionName);
        qrDialogFragment.show((MECsActivity.this).getSupportFragmentManager(), null);
      }
    });
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        finish();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }
}