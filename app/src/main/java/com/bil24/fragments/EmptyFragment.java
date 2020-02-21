package com.bil24.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.*;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.*;
import com.bil24.R;

/**
 * User: SVV
 * Date: 24.05.2015.
 */
public class EmptyFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

  @BindView(R.id.listViewEmpty)
  ListView listView;
  @BindView(R.id.refreshEmpty)
  SwipeRefreshLayout refreshEmpty;
  @BindView(R.id.tvEmptyEmptyText)
  TextView tvEmptyEmptyText;
  private Unbinder unbinder;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_empty, container, false);
    unbinder = ButterKnife.bind(this, view);

//    tvEmptyTextView.setVisibility(View.GONE);
    refreshEmpty.setOnRefreshListener(this);

//    onRefresh();


    return view;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    if (unbinder != null) unbinder.unbind();
  }

  @Override
  public void onRefresh() {
    refreshEmpty.setRefreshing(true);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
  }
}