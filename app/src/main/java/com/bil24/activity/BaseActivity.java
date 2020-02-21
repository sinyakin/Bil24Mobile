package com.bil24.activity;

import android.content.*;
import android.os.Handler;
import android.support.v4.app.*;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import com.bil24.*;
import com.bil24.gcm.*;
import com.bil24.service.*;
import com.bil24.storage.*;
import com.bil24.utils.Utils;
import server.net.*;
import server.net.listener.SetPushTokenListener;
import server.net.obj.SetPushTokenClient;

/**
 * User: SVV
 * Date: 29.05.2015.
 */
public abstract class BaseActivity extends AppCompatActivity implements SetPushTokenListener {
  public CharSequence oldActionBarTitle;
  private ServiceConnector serviceConnector = new ServiceConnector(this);

  private BroadcastReceiver broadcastReceiver;

  public boolean showActivity = false;
  public Fragment currentFragment;

  @Override
  protected void onPause() {
    super.onPause();
    showActivity = false;
    serviceConnector.disConnect();
    LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
  }

  @Override
  protected void onStop() {
    Settings.setNeedAuth(true);
    super.onStop();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    new Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        if (isFinishing()) {
          android.os.Process.killProcess(android.os.Process.myPid());
        }
      }
    }, 400);
  }

  @Override
  public void onResume() {
    super.onResume();
    showActivity = true;
    startService();

    if (broadcastReceiver == null) {
      broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
          onPush();
        }
      };
    }
    LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
        new IntentFilter(MyGcmListenerService.BROADCAST_FILTER));
  }

  public abstract void onPush();

  public void showFragment(Fragment fragment) {
    currentFragment = fragment;
    String backStateName = fragment.getClass().getName();
    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
    transaction.replace(R.id.container, fragment, backStateName);
    transaction.addToBackStack(backStateName);
    transaction.commitAllowingStateLoss();
  }

  public void removeFragment() {
    if (currentFragment == null) return;
   removeFragment(currentFragment);
  }

  private void removeFragment(Fragment fragment) {
    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    transaction.remove(fragment);
    transaction.commit();
  }

  public void startService() {
    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          if (!Utils.isMyServiceRunning(BaseActivity.this, MyService.class)) {
            Intent service = new Intent(BaseActivity.this, MyService.class);
            startService(service);
          }
          serviceConnector.connect();
        } catch (Exception ignored) {
        }
      }
    }).start();
  }

  //-------------------------------------------------------------------
  //получение токена, отправка его на сервер
  public void processWithGCM() {
    new Thread(new Runnable() {
      @Override
      public void run() {
        if (Utils.checkPlayServices(BaseActivity.this, BaseActivity.this)) {
          //запросим токен
          FrontendData frontendData = Controller.getInstance().getFrontendData(BaseActivity.this);
          new GCMDoRequest().execute(new GCMRequest(BaseActivity.this, frontendData.getFid(), GCMCommand.GET_TOKEN, new GCMResponseListener() {
            @Override
            public void onGCMSuccess(GCMCommand command, String token) {
              //отправим токен на сервер
              sendingToken();
              Controller.getInstance().setCheckGcm(false);
            }

            @Override
            public void onGCMFail(GCMCommand command) {
              new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                  processWithGCM();
                }
              }, 3000);
            }
          }));
        }
      }
    }).start();
  }

  private void sendingToken() {
    String pushToken = Settings.getPushToken();
    if (!Settings.isSentTokenToServer() && pushToken != null) {
      NetManager.setPushToken(BaseActivity.this, pushToken);
    }
  }

  @Override
  public void onSetPushToken(SetPushTokenClient setPushTokenClient) {
    Settings.sentTokenToServer(true);
  }

  @Override
  public void onSetPushTokenFailed(NetException e) {
    new Handler().postDelayed(new Runnable() {

      @Override
      public void run() {
        sendingToken();
      }
    }, 5000);
  }
}
