package com.bil24.adapter.mec;

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
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import server.net.*;
import server.net.listener.DeleteListener;
import server.net.obj.DeleteClient;
import server.net.obj.extra.ExtraMECInfo;

import java.util.List;

import static com.bil24.Bil24Application.getContext;

public class MECInfoAdapter extends RecyclerView.Adapter<MECInfoAdapter.ViewHolder> {
  private Context context;
  private List<ExtraMECInfo> list;

  public MECInfoAdapter(Context context, List<ExtraMECInfo> list) {
    this.context = context;
    this.list = list;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View rowView = LayoutInflater.from(context).inflate(R.layout.adapter_mec_info, parent,false);
    return new MECInfoAdapter.ViewHolder(rowView);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    final ExtraMECInfo extraMECInfo = this.list.get(position);
    holder.textViewName.setText(extraMECInfo.getActionName());

    holder.buttonDel.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Dialogs.showConfirmDialog(((AppCompatActivity) context).getSupportFragmentManager(),
            "Удалить безвозвратно карты '" + extraMECInfo.getActionName() + "'", new ConfirmListener() {
              @Override
              public void positiveConfirm() {
                DataBase dataBase = DataBase.getInstance(getContext());
                dataBase.mecInfo.delMECInfo(extraMECInfo.getActionEventId());
                dataBase.mec.delMECsByActionEvent(extraMECInfo.getActionEventId());
                list.remove(extraMECInfo);
                notifyDataSetChanged();
                Settings.setMECInfoSize(Settings.getMECSInfoSize() - 1);
                Controller.getInstance().updateLeftMenuMECsAmount();
                NetManager.deleteTicketByActionEvent(new DeleteListener() {
                  @Override
                  public void onDelete(DeleteClient deleteClient) {
                  }

                  @Override
                  public void onDeleteFailed(NetException e) {
                  }
                }, extraMECInfo.getActionEventId());
              }

              @Override
              public void negativeConfirm() {
              }
            });
      }
    });

    holder.cardView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Controller.getInstance().showMec2Fragment(extraMECInfo.getActionEventId(), extraMECInfo.getCityId(), extraMECInfo.getActionName(), extraMECInfo.getVenueName(), extraMECInfo.isCityPass());
      }
    });

    Picasso.with(getContext())
        .load(extraMECInfo.getBigPosterUrl())
        .placeholder(R.drawable.poster2)
        .error(R.drawable.poster2).fit()
        .into(holder.poster);
  }

  @Override
  public int getItemCount() {
    return list.size();
  }

  static class ViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.mecInfoAdapterCardView)
    CardView cardView;

    @BindView(R.id.mecInfoAdapterPoster)
    RoundedImageView poster;

    @BindView(R.id.mecInfoAdapterName)
    com.rey.material.widget.TextView textViewName;

    @BindView(R.id.mecInfoAdapterButtonDel)
    com.rey.material.widget.Button buttonDel;

    public ViewHolder(View view) {
      super(view);
      ButterKnife.bind(this, view);
    }
  }
}