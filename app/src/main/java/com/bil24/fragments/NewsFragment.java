package com.bil24.fragments;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.bil24.R;
import com.bil24.adapter.NewsAdapter;
import com.bil24.dialog.Dialogs;
import com.bil24.storage.Settings;
import com.rey.material.widget.Button;

import butterknife.Unbinder;
import server.net.NetException;
import server.net.NetManager;
import server.net.listener.GetNewsListener;
import server.net.obj.GetNewsClient;
import server.net.obj.GetNewsServer;
import server.net.obj.extra.ExtraNews;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * User: SVV
 * Date: 24.05.2015.
 */
public class NewsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, GetNewsListener, AbsListView.OnScrollListener {
  private static final String TAG = "NewsFragment";
  private static final int count = 10;

  private View footer;
  private NewsAdapter adapter;
  private boolean firstRequest = true;
  private boolean requestFinish = false;
  private long timeNew;
  private long timeOld;
  private boolean lastOld = false;
  private ConnectionManagerNews connectionManagerNews;
  private CopyOnWriteArrayList<ExtraNews> hideNewsList = new CopyOnWriteArrayList<>();

  @BindView(R.id.newsFragmentListView)
  ListView listView;
  @BindView(R.id.newsFragmentRefresh)
  SwipeRefreshLayout refresh;
  @BindView(R.id.newsFragmentTVEmpty)
  TextView tvEmpty;
  @BindView(R.id.newsFragmentButtonForwardNews)
  Button forwardButton;
  private Unbinder unbinder;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_news, container, false);
    unbinder = ButterKnife.bind(this, view);

    footer = inflater.inflate(R.layout.loading_footer, null);
    tvEmpty.setVisibility(View.GONE);
    refresh.setOnRefreshListener(this);

    if (adapter == null) adapter = new NewsAdapter(getActivity(), new ArrayList<ExtraNews>());
    listView.addFooterView(footer);
    listView.setAdapter(adapter);
    listView.setOnScrollListener(this);

    if (hideNewsList.isEmpty()) {
      forwardButton.setVisibility(View.GONE);
    } else {
      forwardButton.setVisibility(View.VISIBLE);
      forwardButton.setText(Html.fromHtml("\u2191 свежие новости: " + hideNewsList.size()));
    }
    forwardButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        GetNewsClient clientData = new GetNewsClient(hideNewsList, GetNewsServer.CursorMode.forward, true, "", 0);
        onGetNews(clientData);
        hideNewsList.clear();
        listView.setSelectionAfterHeaderView();
        forwardButton.setVisibility(View.GONE);
        hideAllNotification();
      }
    });

    if (connectionManagerNews == null) {
      connectionManagerNews = new ConnectionManagerNews(new GetNewsListener() {
        @Override
        public void onGetNews(GetNewsClient clientData) {
          if (!isAdded()) return;
          if (!clientData.getNewsList().isEmpty()) {
            setTimeNew(clientData.getNewsList().get(clientData.getNewsList().size() - 1).getUnixTime());
          }
          hideNewsList.addAll(clientData.getNewsList());
          if (!hideNewsList.isEmpty()) {
            forwardButton.setText(Html.fromHtml("\u2191 свежие новости: " + hideNewsList.size()));
            forwardButton.setVisibility(View.VISIBLE);
          }
        }

        @Override
        public void onGetNewsFailed(NetException e) {
        }
      }, count, timeNew);
    }
    if (firstRequest) {
      onRefresh();
    } else {
      emptyData();
    }
    return view;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    if (unbinder != null) unbinder.unbind();
  }

  @Override
  public void onResume() {
    super.onResume();
    if (!connectionManagerNews.isAlive()) connectionManagerNews.start();
    else connectionManagerNews.setPause(false);
  }

  public void onPush() {
    connectionManagerNews.wakeup();
  }

  @Override
  public void onPause() {
    super.onPause();
    connectionManagerNews.setPause(true);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    connectionManagerNews.terminate();
  }

  public void setTimeNew(long timeNew) {
    this.timeNew = timeNew;
    connectionManagerNews.setTimeNew(timeNew);
  }

  @Override
  public void onRefresh() {
    setTimeNew(0);
    getNews(this, timeNew, GetNewsServer.CursorMode.backward);
    firstRequest = true;
    hideNewsList.clear();
    forwardButton.setVisibility(View.GONE);
    showFooter();
    if (!adapter.isEmpty()) refresh.setRefreshing(true);
    hideAllNotification();
  }

  private void hideAllNotification() {
    try {
      //удалим все пуши
      NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
      notificationManager.cancelAll();
    } catch (Exception ignored) {
    }
  }

  private void getNews(GetNewsListener listener, long time, GetNewsServer.CursorMode cursorMode) {
    requestFinish = false;
    NetManager.getNews(listener, time, count, cursorMode);
  }

  @Override
  public synchronized void onGetNews(GetNewsClient clientData) {
    if (!isAdded()) return;
    requestFinish = true;
    refresh.setRefreshing(false);

    if (firstRequest) {
      adapter.clear();
      setTimeNew(clientData.getServerUnixTime());
    }

    //установим пометку, что данная новость прочитана
    for (ExtraNews news : clientData.getNewsList()) {
      Settings.setReadNewsId(news.getId());
    }

    if (clientData.getCursorMode() == GetNewsServer.CursorMode.backward) {
      lastOld = clientData.isLast();
      if (lastOld) hideFooter();
    }

    if (!clientData.getNewsList().isEmpty()) {
      if (clientData.getCursorMode() == GetNewsServer.CursorMode.forward) {
        adapter.addFirst(clientData.getNewsList());
        setTimeNew(clientData.getNewsList().get(clientData.getNewsList().size() - 1).getUnixTime());
      } else {
        adapter.addAll(clientData.getNewsList());
        if (firstRequest) setTimeNew(clientData.getNewsList().get(0).getUnixTime());
        timeOld = clientData.getNewsList().get(clientData.getNewsList().size() - 1).getUnixTime();
      }
    }
    if (firstRequest) firstRequest = false;
    emptyData();
  }

  private void hideFooter() {
    footer.setVisibility(View.GONE);
    footer.setPadding(0, -1 * footer.getHeight(), 0, 0);
  }

  private void showFooter() {
    footer.setVisibility(View.VISIBLE);
    footer.setPadding(0, 0, 0, 0);
  }

  private void emptyData() {
    if (adapter.isEmpty()) {
      tvEmpty.setVisibility(View.VISIBLE);
      listView.setVisibility(View.GONE);
    } else {
      tvEmpty.setVisibility(View.GONE);
      listView.setVisibility(View.VISIBLE);
    }
  }

  @Override
  public synchronized void onGetNewsFailed(NetException e) {
    if (!isAdded()) return;
    requestFinish = true;
    refresh.setRefreshing(false);
    if (e.isUserMessage()) Dialogs.showSnackBar(getActivity(), e.getMessage());
  }

  @Override
  public void onScrollStateChanged(AbsListView view, int scrollState) {
  }

  @Override
  public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;
    if (loadMore && requestFinish && !lastOld) {
      getNews(this, timeOld, GetNewsServer.CursorMode.backward);
    }
  }
}