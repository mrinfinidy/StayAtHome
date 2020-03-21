package com.example.stayathome.background;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.stayathome.helper.ChallengeHelper;
import com.example.stayathome.helper.SharedPreferencesHelper;

import org.threeten.bp.Duration;
import org.threeten.bp.Instant;

public class BackgroundService extends Service {
    private BroadcastReceiver rec;
    private Context mainContext;
    private SharedPreferencesHelper prefHelper;

    public BackgroundService(Context mainContext){
        // Constructor
        this.mainContext = mainContext;
        this.prefHelper = new SharedPreferencesHelper(this.mainContext);
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
    public int onStartCommand(Intent intent, int flags, int startId) {
        long currentTime = Instant.now().getEpochSecond();
        this.prefHelper.storeLong("challenge_start_time", currentTime);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        this.unregisterReceiver(this.rec);
        stopSelf();
    }

    private void scheduleWakeupCall(){

    }

} // End class BackgroundService


class WifiBroadcasts extends BroadcastReceiver {
    private Context mainContext;
    private SharedPreferencesHelper prefHelper;
    private ChallengeHelper challengeHelper;

    public WifiBroadcasts(Context mainContext){
        this.mainContext = mainContext;
        this.prefHelper = new SharedPreferencesHelper(this.mainContext);
        this.challengeHelper = new ChallengeHelper(this.prefHelper);
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
                    String savedBSSID = prefHelper.retrieveString("wifi_id");

                    if(wifiInfo.getBSSID().equals(savedBSSID)){
                        // Same network the user has been connected to before
                        if(prefHelper.retrieveLong("last_disconnected") == 0){
                            prefHelper.storeLong("actual_time_in_challenge", 0);
                            scheduleWakeupCall();
                        } else {
                            long timeDisconnected = checkInterruptionTime();
                            if (timeDisconnected < 120){
                                // Disconnect time has been okay
                                prefHelper.removeValueFromStorage("last_disconnected");
                                scheduleWakeupCall();
                            } else {
                                // Disconnect has been too long
                                prefHelper.removeValueFromStorage("actual_time_in_challenge");
                                prefHelper.removeValueFromStorage("last_disconnected");
                            }
                        }
                        updateWifiConnectedTime();
                    } else {
                        // Completely different network

                    }

                } else {
                    // Not connected to an access point
                    prefHelper.storeLong("actual_time_in_challenge", challengeHelper.getTimeInChallenge());
                    updateWifiDisconnectedTime();
                }
            } else {
                // Wi-Fi adapter is disabled (off)
            }
        }
    }

    private void updateWifiConnectedTime(){
        Instant instant = Instant.now();
        prefHelper.storeLong("last_connected", instant.getEpochSecond());
    }

    private void updateWifiDisconnectedTime(){
        Instant instant = Instant.now();
        prefHelper.storeLong("last_disconnected", instant.getEpochSecond());
    }

    private long checkInterruptionTime(){
        long lastDisconnectedTime = prefHelper.retrieveLong("last_disconnected");
        Instant lastDisconnectedInstant = Instant.ofEpochSecond(lastDisconnectedTime);
        Instant currentInstant = Instant.now();
        return Duration.between(lastDisconnectedInstant, currentInstant).getSeconds();
    }

    private void scheduleWakeupCall(){
        long challenge_duration = prefHelper.retrieveLong("challenge_duration");

    }



    public void wakeupCall(){

    }


} // End class WiFiBroadcasts
