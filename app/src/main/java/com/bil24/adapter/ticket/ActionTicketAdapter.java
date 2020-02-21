package com.bil24.adapter.ticket;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import butterknife.*;
import com.bil24.*;
import com.bil24.R;
import com.bil24.dialog.*;
import com.bil24.storage.Settings;
import com.bil24.storage.sql.DataBase;
import com.bil24.storage.sql.db.MyActionEvent;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import server.net.*;
import server.net.listener.DeleteListener;
import server.net.obj.DeleteClient;

import java.util.List;

public class ActionTicketAdapter extends RecyclerView.Adapter<ActionTicketAdapter.ViewHolder> {
  private Context context;
  private List<MyActionEvent> actionList;

  public ActionTicketAdapter(Context context, List<MyActionEvent> actionList) {
    this.context = context;
    this.actionList = actionList;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View rowView = LayoutInflater.from(context).inflate(R.layout.adapter_action_ticket, parent,false);
    return new ViewHolder(rowView);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    final MyActionEvent actionEvent = this.actionList.get(position);
    holder.textViewName.setText(actionEvent.getActionName());
    holder.textViewDate.setText(String.valueOf(actionEvent.getDay() + " " + actionEvent.getTime()));

    if (actionEvent.getDate() > System.currentTimeMillis()) {
      holder.buttonDel.setVisibility(View.GONE);
    } else {
      holder.buttonDel.setVisibility(View.VISIBLE);
      holder.buttonDel.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          Dialogs.showConfirmDialog(((AppCompatActivity) context).getSupportFragmentManager(),
              "Удалить безвозвратно билеты на " + actionEvent.getActionName(), new ConfirmListener() {
                @Override
                public void positiveConfirm() {
                  DataBase dataBase = DataBase.getInstance(context);
                  dataBase.action.delActionEvent(actionEvent.getActionEventId());
                  dataBase.ticket.delTicketByActionEvent(actionEvent.getActionEventId());
                  actionList.remove(actionEvent);
                  notifyDataSetChanged();
                  Settings.setActionEventTickets(Settings.getActionEventTickets() - 1);
                  NetManager.deleteTicketByActionEvent(new DeleteListener() {
                    @Override
                    public void onDelete(DeleteClient deleteClient) {}

                    @Override
                    public void onDeleteFailed(NetException e) {}
                  }, actionEvent.getActionEventId());
                }

                @Override
                public void negativeConfirm() {
                }
              });
        }
      });
    }

    holder.cardView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Controller.getInstance().showTicket2Fragment(actionEvent.getActionEventId(), actionEvent.getActionName());
      }
    });

    Picasso.with(context)
        .load(actionEvent.getBigPosterUrl())
        .placeholder(R.drawable.poster2)
        .error(R.drawable.poster2).fit()
        .into(holder.poster);
  }

  @Override
  public int getItemCount() {
    return actionList.size();
  }

  static class ViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.actionTicketAdapterCardView)
    CardView cardView;

    @BindView(R.id.actionTicketAdapterPoster)
    RoundedImageView poster;

    @BindView(R.id.actionTicketAdapterName)
    com.rey.material.widget.TextView textViewName;

    @BindView(R.id.actionTicketAdapterDate)
    com.rey.material.widget.TextView textViewDate;

    @BindView(R.id.actionTicketAdapterButtonDel)
    com.rey.material.widget.Button buttonDel;

    public ViewHolder(View view) {
      super(view);
      ButterKnife.bind(this, view);
    }
  }
}