package com.bil24.fragments;

import android.os.*;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.*;
import android.widget.*;
import butterknife.*;
import com.bil24.R;
import com.bil24.adapter.ticket.ActionTicketAdapter;
import com.bil24.dialog.Dialogs;
import com.bil24.storage.Settings;
import com.bil24.storage.sql.DataBase;
import com.bil24.storage.sql.db.MyActionEvent;
import server.net.*;
import server.net.listener.*;
import server.net.obj.*;
import server.net.obj.extra.ExtraActionEventsGroupedByTickets;

import java.util.*;

/**
 * User: SVV
 * Date: 24.05.2015.
 */
public class Ticket1Fragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, GetActionEventsGroupedByTicketsListener {

  @BindView(R.id.ticket1FragmentRecyclerView)
  RecyclerView recyclerView;
  @BindView(R.id.ticket1FragmentRefresh)
  SwipeRefreshLayout refresh;
  @BindView(R.id.ticket1FragmentTextViewEmpty)
  TextView textViewEmpty;
  private int quantityRequests = 1;
  private Unbinder unbinder;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_ticket_1, container, false);
    unbinder = ButterKnife.bind(this, view);
    refresh.setOnRefreshListener(this);
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

  public void setQuantityRequests(int quantityRequests) {
    this.quantityRequests = quantityRequests;
  }

  @Override
  public void onRefresh() {
    refresh.setRefreshing(true);
    NetManager.getActionEventsGroupedByTickets(this);
  }

  private void updateAdapter() {
    List<MyActionEvent> actionList = DataBase.getInstance(getActivity()).action.getActions();
    StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(getContext().getResources().getInteger(R.integer.action_ticket_count), StaggeredGridLayoutManager.VERTICAL);
    recyclerView.setLayoutManager(layoutManager);
    ActionTicketAdapter adapter = new ActionTicketAdapter(getActivity(), actionList);
    recyclerView.setAdapter(adapter);
  }

  @Override
  public void onGetActionEventsGroupedByTickets(GetActionEventsGroupedByTicketsClient clientData) {
    if (!isAdded()) return;
    refresh.setRefreshing(false);

    Settings.setActionEventTickets(clientData.getList().size());
    DataBase db = DataBase.getInstance(getActivity());
    boolean isNewAction = false;
    final List<Long> deleteActionEventList = new ArrayList<>();

    for (ExtraActionEventsGroupedByTickets actionEvent : clientData.getList()) {
      MyActionEvent dbAction = db.action.getActionEventById(actionEvent.getActionEventId());
      if (dbAction == null) {
        isNewAction = true;
        db.action.addActionEvent(actionEvent, clientData.getDayPattern(), clientData.getTimePattern());
      } else {
        if (dbAction.isDelete()) deleteActionEventList.add(dbAction.getActionEventId());
        else db.action.updateData(actionEvent);
      }
    }

    if (clientData.getList().isEmpty()) {
      textViewEmpty.setVisibility(View.VISIBLE);
      recyclerView.setVisibility(View.GONE);
    } else {
      textViewEmpty.setVisibility(View.GONE);
      recyclerView.setVisibility(View.VISIBLE);
    }

    if (isNewAction) {
      quantityRequests = 1;
      updateAdapter();
    } else {
      if (quantityRequests-- > 1) {
        new Handler().postDelayed(new Runnable() {
          @Override
          public void run() {
            onRefresh();
          }
        }, 1500);
      }
    }

    //удалим с сервака ненужные билеты, если они не были удалены, а только помечены
    new Thread(new Runnable() {
      @Override
      public void run() {
        for (Long actionEventId : deleteActionEventList) {
          NetManager.deleteTicketByActionEvent(new DeleteListener() {
            @Override
            public void onDelete(DeleteClient deleteClient) {
            }

            @Override
            public void onDeleteFailed(NetException e) {
            }
          }, actionEventId);
        }
      }
    }).start();
  }

  @Override
  public void onGetActionEventsGroupedByTicketsFailed(NetException e) {
    if (!isAdded()) return;
    refresh.setRefreshing(false);
    if (e.isUserMessage()) Dialogs.showSnackBar(getActivity(), e.getMessage());
  }
}