package com.bil24.activity;

import android.content.*;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.ImageView;
import butterknife.*;
import com.bil24.R;
import com.bil24.myelement.swipeback.SwipeBackActivity;
import com.squareup.picasso.*;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageActivity extends SwipeBackActivity {
  private static final String EXTRA_URL = "EXTRA_URL";

  private PhotoViewAttacher attacher;

  @BindView(R.id.imageActivityImageView)
  ImageView imageView;

  @BindView(R.id.toolbar)
  Toolbar toolbar;

  @BindView(R.id.imageActivityImageViewProgressBar)
  com.rey.material.widget.ProgressView progressBar;

  public static Intent getStartIntent(Context packageContext, @NonNull String url) {
    Intent intent = new Intent(packageContext, ImageActivity.class);
    intent.putExtra(EXTRA_URL, url);
    return intent;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_image);
    ButterKnife.bind(this);

    toolbar.setLogo(R.mipmap.logo_toolbar);
    setSupportActionBar(toolbar);
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

    progressBar.setVisibility(View.VISIBLE);
    attacher = new PhotoViewAttacher(imageView);
    String url = getIntent().getStringExtra(EXTRA_URL);
    Picasso.with(this)
        .load(url)
        .placeholder(R.drawable.poster2)
        .error(R.drawable.poster2)
        .into(imageView, new Callback() {
          @Override
          public void onSuccess() {
            progressBar.setVisibility(View.GONE);
            attacher.update();
          }

          @Override
          public void onError() {
          }
        });
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (attacher != null) attacher.cleanup();
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
