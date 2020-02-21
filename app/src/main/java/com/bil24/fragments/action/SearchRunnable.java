package com.bil24.fragments.action;

import android.app.*;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import server.net.obj.extra.ExtraActionV2;

import java.util.*;

/**
 * Created by SVV on 26.09.2015
 */
public class SearchRunnable extends AsyncTask<String, Integer, List<ExtraActionV2>> {

  private ProgressDialog dialog;
  private List<ExtraActionV2> actionList;
  private ResultListener resultListener;
  private Activity activity;

  public SearchRunnable(Activity activity, List<ExtraActionV2> actionList, ResultListener resultListener) {
    this.activity = activity;
    this.actionList = actionList;
    this.resultListener = resultListener;
  }

  @Override
  protected void onPreExecute() {
    dialog = ProgressDialog.show(activity, "", "Поиск...", true, false);
    super.onPreExecute();
  }

  @NonNull
  protected List<ExtraActionV2> doInBackground(String... searchs) {
    List<ExtraActionV2> actionList = new ArrayList<>();
    try {
      for (ExtraActionV2 extraAction : this.actionList) {
        for (String search : searchs) {
          if (extraAction.getActionName().toLowerCase().contains(search) ||
              (extraAction.getFullActionName().toLowerCase().contains(search))) {
            if (actionList.indexOf(extraAction) == -1) actionList.add(extraAction);
            break;
          }
        }
      }
    } catch (Exception ignored) {
      ignored.printStackTrace();
    }
    return actionList;
  }

  protected void onPostExecute(List<ExtraActionV2> extraActionList) {
    dialog.dismiss();
    if (resultListener != null) resultListener.searchResult(extraActionList);
  }

  public interface ResultListener {
    void searchResult(List<ExtraActionV2> actionList);
  }
}