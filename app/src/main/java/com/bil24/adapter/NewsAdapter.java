package com.bil24.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.bil24.R;
import server.net.obj.extra.ExtraNews;

import java.util.List;

public class NewsAdapter extends ArrayAdapter<ExtraNews> {
  private Context context;
  private List<ExtraNews> list;

  public NewsAdapter(Context context, List<ExtraNews> list) {
    super(context, R.layout.adapter_news, list);
    this.context = context;
    this.list = list;
  }

  static class ViewHolder {
    @BindView(R.id.newsAdapterTvHeader)
    TextView tvHeader;
    @BindView(R.id.newsAdapterTvTime)
    TextView tvTime;
    @BindView(R.id.newsAdapterTvFullDescription)
    TextView tvFullDescription;

    public ViewHolder(View view) {
      ButterKnife.bind(this, view);
    }
  }

  public List<ExtraNews> getList() {
    return list;
  }

  public void addFirst(List<ExtraNews> list) {
    for (ExtraNews news : list) {
      insert(news, 0);
    }
  }

  @NonNull
  @Override
  public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
    ViewHolder holder;

    View rowView = convertView;
    if (rowView == null) {
      LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      rowView = inflater.inflate(R.layout.adapter_news, parent, false);
      holder = new ViewHolder(rowView);
      rowView.setTag(holder);
    } else {
      holder = (ViewHolder) rowView.getTag();
    }

    ExtraNews news = this.list.get(position);

    holder.tvHeader.setText(news.getHeader());
    holder.tvTime.setText(news.getDate());
    holder.tvFullDescription.setText(Html.fromHtml(news.getFullDescription()));
    holder.tvFullDescription.setMovementMethod(LinkMovementMethod.getInstance());
    holder.tvFullDescription.setLinkTextColor(getContext().getResources().getColor(R.color.toolbar));
    return rowView;
  }
}
