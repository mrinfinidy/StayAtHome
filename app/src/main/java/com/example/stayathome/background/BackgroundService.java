package com.example.stayathome.background;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.stayathome.MainActivity;
import com.example.stayathome.R;
import com.example.stayathome.helper.ChallengeHelper;
import com.example.stayathome.helper.NotificationHelper;
import com.example.stayathome.helper.SharedPreferencesHelper;

import org.threeten.bp.Duration;
import org.threeten.bp.Instant;

/**
 * @author Daniel Scheible, created on 20.03.2020
 */

public class BackgroundService extends Service {
    private BroadcastReceiver recWifi;
    private BroadcastReceiver recDisplay;
    private SharedPreferencesHelper prefHelper;
    private HandlerThread handlerThread;
    private Handler mHandler;
    private Runnable runnable;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // Do not allow binding
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        this.prefHelper = new SharedPreferencesHelper(getApplicationContext());

        this.recWifi = new WifiBroadcasts(getApplicationContext(), this);
        IntentFilter filterNetwork = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        this.registerReceiver(this.recWifi, filterNetwork);

        this.recDisplay = new DisplayBroadcasts(getApplicationContext(), this);
        IntentFilter filterScreen = new IntentFilter();
        filterScreen.addAction(Intent.ACTION_SCREEN_ON);
        filterScreen.addAction(Intent.ACTION_SCREEN_OFF);
        this.registerReceiver(this.recDisplay, filterScreen);

        scheduleWakeupCall(prefHelper.retrieveLong("challenge_duration"));

        long currentTime = Instant.now().getEpochSecond();
        this.prefHelper.storeLong("challenge_start_time", currentTime);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        this.unregisterReceiver(this.recWifi);
        this.unregisterReceiver(this.recDisplay);
        this.handlerThread.quitSafely();
        stopSelf();
    }

    public void scheduleWakeupCall(long wakeupInSeconds){
        handlerThread = new HandlerThread("TreeFollower");
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper());
        runnable = new Runnable() {
            @Override
            public void run() {
                treeReady();
            }
        };
        mHandler.postDelayed(runnable, wakeupInSeconds * 1000);
    }

    public void cancelWakeupCall(){
        mHandler.removeCallbacks(runnable);
    }

    public void treeReady(){
        String tree_ready = getResources().getString(R.string.tree_ready_text_1) + " Walter " + getResources().getString(R.string.tree_ready_text_2);
        NotificationHelper.sendNotification(getApplicationContext(), NotificationHelper.CHANNEL_ID_GROWTH_PROGRESS, getResources().getString(R.string.tree_ready_headline), tree_ready);
    }

} // End class BackgroundService


class WifiBroadcasts extends BroadcastReceiver {
    private Context mainContext;
    private SharedPreferencesHelper prefHelper;
    private ChallengeHelper challengeHelper;
    private BackgroundService backService;

    public WifiBroadcasts(Context mainContext, BackgroundService backService){
        this.mainContext = mainContext;
        this.prefHelper = new SharedPreferencesHelper(this.mainContext);
        this.challengeHelper = new ChallengeHelper(this.prefHelper);
        this.backService = backService;
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

class DisplayBroadcasts extends BroadcastReceiver{

    private Context mainContext;
    private BackgroundService backService;
    private SharedPreferencesHelper prefHelper;


    public DisplayBroadcasts(Context mainContext, BackgroundService backService){
        this.mainContext = mainContext;
        this.backService = backService;
        this.prefHelper = new SharedPreferencesHelper(this.mainContext);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)){

        } else if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){

        }
    }
} // End class DisplayBroadcasts
