package com.bil24.gcm;

import android.util.Log;
import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by SVV on 25.02.2016
 */
public class MyInstanceIDListenerService extends InstanceIDListenerService {

  /**
   * Called if InstanceID token is updated. This may occur if the security of
   * the previous token had been compromised. This call is initiated by the
   * InstanceID provider.
   */
  @Override
  public void onTokenRefresh() {
    Log.d("MyInstanceIDService", "onTokenRefresh");
//    new GCMDoRequest().execute(new GCMRequest(this, GCMCommand.GET_TOKEN));
  }
}