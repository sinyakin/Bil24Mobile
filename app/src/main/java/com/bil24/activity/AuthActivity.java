package com.bil24.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.*;
import android.support.v7.app.*;
import android.view.*;
import android.widget.TextView;
import butterknife.*;
import com.bil24.*;
import com.bil24.R;
import com.bil24.storage.*;

public class AuthActivity extends AppCompatActivity implements View.OnClickListener {
  @BindView(R.id.activityAuthPass)
  TextView tvPass;

  @BindView(R.id.activityAuthTvNumeral0)
  TextView tv0;
  @BindView(R.id.activityAuthTvNumeral1)
  TextView tv1;
  @BindView(R.id.activityAuthTvNumeral2)
  TextView tv2;
  @BindView(R.id.activityAuthTvNumeral3)
  TextView tv3;
  @BindView(R.id.activityAuthTvNumeral4)
  TextView tv4;
  @BindView(R.id.activityAuthTvNumeral5)
  TextView tv5;
  @BindView(R.id.activityAuthTvNumeral6)
  TextView tv6;
  @BindView(R.id.activityAuthTvNumeral7)
  TextView tv7;
  @BindView(R.id.activityAuthTvNumeral8)
  TextView tv8;
  @BindView(R.id.activityAuthTvDelete)
  TextView tvDelete;

  @BindView(R.id.activityAuthTvNumeral9)
  TextView tv9;

  private String password = "";
  private String correctPassword = "";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_auth);
    ButterKnife.bind(this);

    FrontendData frontendData = Controller.getInstance().getFrontendData(this);
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setLogo(R.mipmap.logo_toolbar);
      actionBar.setTitle(" " + frontendData.getFrontendType().getDesc());
    }

    tv0.setOnClickListener(this);
    tv1.setOnClickListener(this);
    tv2.setOnClickListener(this);
    tv3.setOnClickListener(this);
    tv4.setOnClickListener(this);
    tv5.setOnClickListener(this);
    tv6.setOnClickListener(this);
    tv7.setOnClickListener(this);
    tv8.setOnClickListener(this);
    tv9.setOnClickListener(this);

    tvDelete.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        vibrate();
        if (password.isEmpty()) return;
        password = password.substring(0, password.length() - 1);
        tvPass.setText(tvPass.getText().toString().substring(0, tvPass.getText().toString().length() - 1));
      }
    });

    correctPassword = String.valueOf(frontendData.getFid());
  }

  @SuppressLint("SetTextI18n")
  @Override
  public void onClick(View v) {
    if (v instanceof TextView) {
      TextView tv = (TextView) v;
      vibrate();
      password = password + tv.getText();
      tvPass.setText(tvPass.getText() + "*");
      if (correctPassword.equals(password)) {
        Settings.setNeedAuth(false);
        setResult(RESULT_OK);
        finish();
      }
    }
  }

  private void vibrate() {
    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    vibrator.vibrate(100);
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    Settings.setNeedAuth(true);
    setResult(RESULT_CANCELED);
    finish();
  }
}
