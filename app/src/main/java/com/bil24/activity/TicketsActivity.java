package com.bil24.activity;

import android.content.*;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.bil24.R;
import com.bil24.adapter.ticket.TicketAdapter;
import com.bil24.dialog.*;
import com.bil24.myelement.MyLoadingPanel;
import com.bil24.net.AndroidSender;
import com.bil24.storage.sql.DataBase;
import com.bil24.storage.sql.db.MyExtraTicket;
import com.fortysevendeg.swipelistview.*;
import server.net.*;
import server.net.listener.GetTicketsByActionEventListener;
import server.net.obj.*;
import server.net.obj.extra.ExtraTicket;

import java.util.*;

@Deprecated
@SuppressWarnings("SuspiciousNameCombination")
public class TicketsActivity extends AppCompatActivity implements GetTicketsByActionEventListener {
  private static final String EXTRA_ACTION_EVENT_ID = "EXTRA_ACTION_EVENT_ID";
  private static final String EXTRA_ACTION_NAME = "EXTRA_ACTION_NAME";

  private List<MyExtraTicket> ticketList = new ArrayList<>();
  private com.fortysevendeg.swipelistview.SwipeListView listView;
  private MyLoadingPanel loadingPanel;
  private com.rey.material.widget.Button buttonAsQr;
  private long actionEventId;
  private String actionName;
  private TicketAdapter adapter;

  public static Intent getStartIntent(Context packageContext, long actionEventId, String actionName) {
    Intent intent = new Intent(packageContext, TicketsActivity.class);
    intent.putExtra(EXTRA_ACTION_EVENT_ID, actionEventId);
    intent.putExtra(EXTRA_ACTION_NAME, actionName);
    return intent;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_ticket);
    NetManager.init(new AndroidSender());

    if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    actionEventId = getIntent().getLongExtra(EXTRA_ACTION_EVENT_ID, -1);
    actionName = getIntent().getStringExtra(EXTRA_ACTION_NAME);
    setTitle("Билеты на \"" + actionName + "\"");

    buttonAsQr = (com.rey.material.widget.Button) findViewById(R.id.ticketActivityButtonShowAllQrCode);
    buttonAsQr.setVisibility(View.GONE);

    listView = (SwipeListView) findViewById(R.id.ticketActivityGridView);
    loadingPanel = (MyLoadingPanel) findViewById(R.id.ticketActivityLoadingPanel);
    loadingPanel.setText("Загрузка билетов...");
    listView.setVisibility(View.GONE);

    listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
    listView.setSwipeMode(SwipeListView.SWIPE_MODE_BOTH);
    listView.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_REVEAL);
    listView.setSwipeActionRight(SwipeListView.SWIPE_ACTION_CHOICE);
    listView.setOffsetLeft(dp2px(0));
    listView.setOffsetRight(dp2px(0));
    listView.setAnimationTime(0);
    listView.setSwipeOpenOnLongPress(true);
    listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

      @Override
      public void onItemCheckedStateChanged(ActionMode mode, int position,
                                            long id, boolean checked) {
        if (listView.getCountSelected() == 0) mode.finish();
        mode.setTitle("Выделено: (" + listView.getCountSelected() + ")");
      }

      @Override
      public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
          case R.id.menu_delete:
            List<Long> ticketsIdList = new ArrayList<Long>();
            for (Integer position : listView.getPositionsSelected()) {
              MyExtraTicket extraTicket = (MyExtraTicket) listView.getItemAtPosition(position);
              ticketsIdList.add(extraTicket.getTicketId());
            }
            Dialogs.showTicketsToEmailDialog(TicketsActivity.this, getSupportFragmentManager(), ticketsIdList);
            listView.unselectedChoiceStates();
            mode.finish();
            return true;
          default:
            return false;
        }
      }

      @Override
      public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.menu_choice_items, menu);
        return true;
      }

      @Override
      public void onDestroyActionMode(ActionMode mode) {
        listView.unselectedChoiceStates();
      }

      @Override
      public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
      }
    });

    listView.setSwipeListViewListener(new BaseSwipeListViewListener() {
      @Override
      public void onOpened(int position, boolean toRight) {
        MyExtraTicket ticket = (MyExtraTicket) listView.getItemAtPosition(position);
        ticket.setShow(false);
        DataBase.getInstance(TicketsActivity.this).ticket.updateTicketShow(ticket.getTicketId(), false);
        adapter.notifyDataSetChanged();
        listView.closeAnimate(position);
      }

      @Override
      public void onClosed(int position, boolean fromRight) {
        Log.d("swipe", "onClosed");
      }

      @Override
      public void onListChanged() {
      }

      @Override
      public void onMove(int position, float x) {
        Log.d("swipe", "onMove");
      }

      @Override
      public void onStartOpen(int position, int action, boolean right) {
        Log.d("swipe", String.format("onStartOpen %d - action %d", position, action));
      }

      @Override
      public void onStartClose(int position, boolean right) {
        Log.d("swipe", String.format("onStartClose %d", position));
      }

      @Override
      public void onClickFrontView(int position) {
        MyExtraTicket extraTicket = (MyExtraTicket) listView.getItemAtPosition(position);
        TicketDialogFragment ticketDialogFragment = TicketDialogFragment.create(extraTicket, actionName);
        ticketDialogFragment.show(TicketsActivity.this.getSupportFragmentManager(), null);
        Log.d("swipe", String.format("onClickFrontView %d", position));
      }

      @Override
      public void onClickBackView(int position) {
        Log.d("swipe", String.format("onClickBackView %d", position));
      }

      @Override
      public void onDismiss(int[] reverseSortedPositions) {
        for (int position : reverseSortedPositions) {
          adapter.getTicketList().remove(position);
        }
        adapter.notifyDataSetChanged();
      }
    });

    adapter = new TicketAdapter(this, ticketList);
    listView.setAdapter(adapter);

    List<MyExtraTicket> ticketList = DataBase.getInstance(this).ticket.getTicketsByActionEventId(actionEventId);
    if (!ticketList.isEmpty()) {
      listView.setVisibility(View.VISIBLE);
      loadingPanel.setVisibility(View.GONE);
      buttonAsQr.setVisibility(View.VISIBLE);
      updateAdapter(ticketList);
    }

    Display display = getWindowManager().getDefaultDisplay();
    DisplayMetrics metrics = new DisplayMetrics();
    display.getMetrics(metrics);
    Integer height;
    Integer width;
    if (metrics.widthPixels < metrics.heightPixels) {
      width = metrics.widthPixels;
      height = metrics.heightPixels;
    } else {
      width = metrics.heightPixels;
      height = metrics.widthPixels;
    }
    width = width - 100;
    height = (height * 15) / 100;

    buttonAsQr.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        //открыть все билеты как qr коды
        TicketsQrDialogFragment qrDialogFragment = TicketsQrDialogFragment.create(new ArrayList<>(TicketsActivity.this.ticketList), actionName);
        qrDialogFragment.show((TicketsActivity.this).getSupportFragmentManager(), null);
      }
    });

    NetManager.getTicketsByActionEvent(this, actionEventId, width, width, height, GetTicketsByActionEventServer.Type.PNG);
  }

  private int dp2px(int dp) {
    return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
        getResources().getDisplayMetrics());
  }

  private void updateAdapter(final List<MyExtraTicket> ticketList) {
    this.ticketList.clear();
    this.ticketList.addAll(ticketList);
    adapter.notifyDataSetChanged();
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

  @Override
  public void onGetTicketsByActionEvent(GetTicketsByActionEventClient clientData) {
    if (isFinishing()) return;
    buttonAsQr.setVisibility(View.VISIBLE);
    listView.setVisibility(View.VISIBLE);
    loadingPanel.setVisibility(View.GONE);

    DataBase db = DataBase.getInstance(this);
    boolean addNewTicket = false;
    for (ExtraTicket ticket : clientData.getTicketList()) {
      ExtraTicket dbTicket = db.ticket.getTicketById(ticket.getTicketId());
      if (dbTicket == null) {
        addNewTicket = true;
        db.ticket.addTicket(ticket.getTicketId(), actionEventId, ticket.getActionName(), ticket.getVenueName(),
            ticket.getVenueAddress(), ticket.getDate(), ticket.getPrice().toString(),
            ticket.getSector(), ticket.getRow(), ticket.getNumber(), ticket.getCategoryName(),
            ticket.getQrCodeImg(), ticket.getBarCodeImg(), ticket.getBarCodeNumber());
      }
//      db.updateTicket(ic_menu_ticket.getTicketId(), TableTicket.BAR_CODE_IMAGE, ic_menu_ticket.getBarCodeImg());
    }

    if (addNewTicket)
      updateAdapter(db.ticket.getTicketsByActionEventId(actionEventId));
  }

  @Override
  public void onGetTicketsByActionEventFailed(NetException e) {
    if (isFinishing()) return;
    if (e.isUserMessage()) Dialogs.showSnackBar(this, e.getMessage());
  }
}