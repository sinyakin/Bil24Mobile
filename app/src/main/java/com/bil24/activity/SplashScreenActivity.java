package com.bil24.activity;

import android.app.*;
import android.content.Intent;
import android.os.*;
import android.util.Log;
import android.widget.TextView;

import com.bil24.Controller;
import com.bil24.InstallReferrerReceiver;
import com.bil24.R;
import com.bil24.fragments.action.filter.City;
import com.bil24.net.AndroidSender;
import com.bil24.storage.FrontendData;
import com.bil24.storage.Settings;
import com.bil24.storage.SettingsCommon;
import com.bil24.storage.sql.DataBase;

import butterknife.BindView;
import butterknife.ButterKnife;
import server.net.NetManager;

public class SplashScreenActivity extends Activity implements InstallReferrerReceiver.ReferrerAuthListener {

  @BindView(R.id.activitySplashScreenTextView)
  TextView activitySplashScreenTextView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash_screen);
    ButterKnife.bind(this);
    Controller.getInstance().setKassatka(android.os.Build.MODEL);
    //залипуха //todo удалить когда андройд будет 40v
    if (SettingsCommon.isRealZone()) DataBase.getInstance(this).mainData.setNewDefaultFrontendData();

    //Логин: sinyakin89@list.ru
    //Пароль: cenijeqifike
    //Код сотрудника: 9753
    //telpo 86337898
    Log.d("!!!!!", DataBase.getInstance(this).mainData.getFrontendData().toString());

    //инициализация сетевого интерфейса
    NetManager.init(new AndroidSender());

    //шайтан условия для referrer
    if (Settings.isWaitReferrer()) {
      final DataBase db = DataBase.getInstance(this);
      new Handler().postDelayed(new Runnable() {

        @Override
        public void run() {
          if (db.mainData.isReferrerAuth()) {
            checkReferrer(50);
            return;
          }
          activitySplashScreenTextView.setText("Настройка приложения...");
          new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
              if (db.mainData.isReferrerAuth()) {
                checkReferrer(50);
                return;
              }
              activitySplashScreenTextView.setText("Авторизация интерфейса...");
              checkReferrer(2800);
            }
          }, 2800);
        }
      }, 2800);
    } else checkReferrer(950);

  }

  private void checkReferrer(int delay) {
    new Handler().postDelayed(new Runnable() {

      @Override
      public void run() {
        DataBase db = DataBase.getInstance(SplashScreenActivity.this);
        if (db.mainData.isReferrerAuth()) {
          FrontendData frontendData = db.mainData.getFrontendData();
          activitySplashScreenTextView.setText("Авторизация интерфейса " + frontendData.getFid() + "...");
          InstallReferrerReceiver.authFrontend(SplashScreenActivity.this, frontendData.getFid(), frontendData.getToken(), SplashScreenActivity.this);
        } else startActivity();
      }
    }, delay);
  }

  @Override
  public void onBackPressed() {
  }

  private void startActivity() {
    new Handler().postDelayed(new Runnable() {

      @Override
      public void run() {
        //проверим город
        new Thread(new Runnable() {
          @Override
          public void run() {
            DataBase db = DataBase.getInstance(SplashScreenActivity.this);
            final City citySelected = db.city.getSelectedCity();
            SplashScreenActivity.this.runOnUiThread(new Runnable() {
              @Override
              public void run() {
                if (SplashScreenActivity.this.isFinishing()) return;
                Class aClass = MainActivity.class;
                if (citySelected == null) {
                  aClass = CityActivity.class;
                }
                Intent intent = new Intent(SplashScreenActivity.this, aClass);
                startActivity(intent);
                finish();
              }
            });
          }
        }).start();
      }
    }, 50);
  }

  @Override
  public void referrerFinish() {
    startActivity();
  }
}