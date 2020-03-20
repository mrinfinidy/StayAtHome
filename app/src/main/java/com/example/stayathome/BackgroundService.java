package com.example.stayathome;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class BackgroundService extends Service {
    private BroadcastReceiver rec;
    SharedPreferences pref;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // Do not allow binding
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        this.rec = new WifiBroadcasts();
        IntentFilter filter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        this.registerReceiver(this.rec, filter);
        //TODO: finish access to SharedPreferences
        //pref = this.getSharedPreferences();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(this.rec);
    }
} // End class BackgroundService


class WifiBroadcasts extends BroadcastReceiver {
    private static final String TAG = "WiFiBroadcasts";

    @Override
    public void onReceive(Context context, Intent intent) {

    }
} // End class WiFiBroadcasts
