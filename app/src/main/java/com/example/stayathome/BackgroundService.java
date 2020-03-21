package com.example.stayathome;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.stayathome.helper.SharedPreferencesHelper;

import org.threeten.bp.Instant;

public class BackgroundService extends Service {
    private BroadcastReceiver rec;
    private Context mainContext;

    public BackgroundService(Context mainContext){
        // Constructor
        this.mainContext = mainContext;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // Do not allow binding
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        this.rec = new WifiBroadcasts(mainContext);
        IntentFilter filter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        this.registerReceiver(this.rec, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        this.unregisterReceiver(this.rec);
    }
} // End class BackgroundService


class WifiBroadcasts extends BroadcastReceiver {
    private Context mainContext;
    private SharedPreferencesHelper prefHelper;

    public WifiBroadcasts(Context mainContext){
        this.mainContext = mainContext;
        this.prefHelper = new SharedPreferencesHelper(this.mainContext);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)){
            WifiManager wifiMgr = (WifiManager) mainContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            if(wifiMgr.isWifiEnabled()){
                //Wi-Fi adapter is enabled (on)
                WifiInfo wifiInfo = wifiMgr.getConnectionInfo();

                if(wifiInfo.getNetworkId() != -1){
                    // Connected to an access point
                    String savedSSID = prefHelper.retrieveString("wifi_name");
                    String savedBSSID = prefHelper.retrieveString("wifi_id");

                    if(wifiInfo.getSSID().equals(savedSSID)){
                        // SSID is the same as the one that the user saved
                        updateWifiConnectedTime();
                    } else if(wifiInfo.getBSSID().equals(savedBSSID)) {
                        // SSID is not the same, but MAC-address did not change -> update SSID
                        prefHelper.storeString("wifi_name", wifiInfo.getSSID());
                        updateWifiConnectedTime();
                    } else{
                        // Completely different network
                    }

                } else {
                    // Not connected to an access point
                }
            } else {
                //Wi-Fi adapter is disabled (off)
            }
        }
    }

    private void updateWifiConnectedTime(){
        Instant instant = Instant.now();
    }
} // End class WiFiBroadcasts
