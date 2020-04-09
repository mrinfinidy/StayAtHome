package com.example.stayathome.background;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.stayathome.R;
import com.example.stayathome.helper.NotificationHelper;
import com.example.stayathome.helper.SharedPreferencesHelper;

import org.threeten.bp.Duration;
import org.threeten.bp.Instant;

import java.util.HashSet;

public class BackgroundService extends Service {
    private static final String TAG = "BackgroundService";
    private BroadcastReceiver recWifi;
    private BroadcastReceiver recDisplay;
    private SharedPreferencesHelper prefHelper;
    private HandlerThread handlerThread;
    private Handler mHandler;
    private Runnable runnableTreeUpdate;
    private Runnable runnableTreeDown;
    private boolean treeUpdatesActive;

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

    public void scheduleTreeUpdate(long nextUpdateInSeconds){
        if (!prefHelper.retrieveBoolean("ongoing_challenge")) {
            return;
        }

        Log.i(TAG, "Next tree status update has been posted (due in " + nextUpdateInSeconds + " seconds)");
        handlerThread = new HandlerThread("TreeFollower");
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper());

        runnableTreeUpdate = new Runnable() {
            @Override
            public void run() {
                int growth = prefHelper.retrieveInt("current_growth") ;
                int onScreen = prefHelper.retrieveInt("growth_on_screen");
                if ((growth + 1) - onScreen <= 1 ) {
                    growth++;
                }
                Log.i(TAG, "Current tree size: " + growth);
                if(growth <= 5){
                    //treeDry(); this causes crash
                } else {
                    //treeReady(); this causes crash
                }
                prefHelper.storeInt("current_growth", growth);
            }
        };
        mHandler.postDelayed(runnableTreeUpdate, nextUpdateInSeconds * 1000);
        treeUpdatesActive = true;
    }

    public void cancelTreeUpdates(){
        Log.i(TAG, "Tree updates have been stopped");
        mHandler.removeCallbacks(runnableTreeUpdate);
        treeUpdatesActive = false;
    }

    public void scheduleTreeDown(){
        final int allowedTimeDisconnected = 3;

        Log.i(TAG, "Tree down timer has been started");
        runnableTreeDown = new Runnable() {
            @Override
            public void run() {
                //treeDown(); this causes crash
            }
        };
        mHandler.postDelayed(runnableTreeDown, allowedTimeDisconnected * 1000);
    }

    public void cancelTreeDown(){
        Log.i(TAG, "Tree down timer has been stopped");
        mHandler.removeCallbacks(runnableTreeDown);
    }

    public boolean isWakeupCallActive(){
        return treeUpdatesActive;
    }

    public void treeReady(){
        String treeReady = getResources().getString(R.string.your_tree) + " " + prefHelper.retrieveString("tree_name") + " " + getResources().getString(R.string.tree_ready_text);
        NotificationHelper.sendNotification(getApplicationContext(), NotificationHelper.CHANNEL_ID_GROWTH_PROGRESS, getResources().getString(R.string.tree_ready_headline), treeReady);
    }

    public void treeDry(){
        String treeDry = getResources().getString(R.string.your_tree) + " " + prefHelper.retrieveString("tree_name") + " " + getResources().getString(R.string.tree_dry_text);
        NotificationHelper.sendNotification(getApplicationContext(), NotificationHelper.CHANNEL_ID_GROWTH_PROGRESS, getResources().getString(R.string.tree_dry_headline), treeDry);
    }

    public void treeDown(){
        String treeDown = getResources().getString(R.string.your_tree) + " " + prefHelper.retrieveString("tree_name") + " " + getResources().getString(R.string.tree_down_text);
        NotificationHelper.sendNotification(getApplicationContext(), NotificationHelper.CHANNEL_ID_GROWTH_PROGRESS, getResources().getString(R.string.tree_down_headline), treeDown);
    }

} // End class BackgroundService


class WifiBroadcasts extends BroadcastReceiver {
    private static final String TAG = "WifiBroadcasts";
    private Context mainContext;
    private SharedPreferencesHelper prefHelper;
    private BackgroundService backService;

    public WifiBroadcasts(Context mainContext, BackgroundService backService){
        this.mainContext = mainContext;
        this.prefHelper = new SharedPreferencesHelper(this.mainContext);
        this.backService = backService;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final int allowedTimeDisconnected = 3;

        String action = intent.getAction();

        if(WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)){
            WifiManager wifiMgr = (WifiManager) mainContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            //int wifiStateExtra = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);

            if(isConnected(wifiMgr)){
                //Wi-Fi adapter is enabled (on)
                WifiInfo wifiInfo = wifiMgr.getConnectionInfo();

                if(wifiInfo.getNetworkId() != -1){
                    // Connected to an access point
                    Log.i(TAG, "Connected to wifi " + wifiInfo.getSSID());
                    if(isSavedSSID(wifiInfo.getSSID())){
                        // Phone has connected to the user selected wifi-network
                        if(prefHelper.retrieveLong("last_disconnected") == 0){
                            // Phone has not disconnected before start of a challenge
                            backService.scheduleTreeUpdate(prefHelper.retrieveLong("challenge_duration"));
                        } else {
                            // Phone has disconnected in an active challenge, check for wifi-downtime
                            long timeDisconnected = checkInterruptionTime();
                            Log.i(TAG, "WiFi has been disconnected for " + timeDisconnected + " seconds");
                            if (timeDisconnected < allowedTimeDisconnected || prefHelper.retrieveInt("growth_on_screen") >= 1){
                                backService.cancelTreeDown();
                                long new_time_in_challenge = prefHelper.retrieveLong("last_disconnected") - prefHelper.retrieveLong("challenge_start_time");
                                prefHelper.storeLong("actual_time_in_challenge", new_time_in_challenge);
                                prefHelper.removeValueFromStorage("last_disconnected");
                                long new_wakeup_in_seconds = prefHelper.retrieveLong("challenge_duration") - new_time_in_challenge;
                                backService.scheduleTreeUpdate(prefHelper.retrieveLong("challenge_duration"));

                            } else {
                                // Disconnect has been too long
                                prefHelper.removeValueFromStorage("actual_time_in_challenge");
                                prefHelper.removeValueFromStorage("last_disconnected");
                                prefHelper.storeBoolean("tree_alive", false);
                                prefHelper.storeBoolean("ongoing_challenge", false);
                            }
                        }
                        updateWifiConnectedTime();
                    } else {
                        // different network
                        if (!prefHelper.contains("last_disconnected")) {
                            Log.i(TAG, "connected to new WiFi");
                            backService.cancelTreeDown();
                            updateWifiDisconnectedTime();
                            backService.scheduleTreeDown();
                        }
                    }
                }
            } else {
                // Wi-Fi adapter is disabled (off)
                Log.i(TAG, "WiFi adapter has been disabled");
                backService.cancelTreeUpdates();
                updateWifiDisconnectedTime();
                backService.scheduleTreeDown();
            }
        }
    }

    //check if wifi is enabled and for Android 8.1+ check gps too
    private boolean isConnected(WifiManager wifiManager) {
        if (wifiManager == null)
            return false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            LocationManager locationManager = (LocationManager)  mainContext.getSystemService(Context.LOCATION_SERVICE);
            if ((locationManager != null && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) || !wifiManager.isWifiEnabled()) {
                return false;
            }
        }

        if (!wifiManager.isWifiEnabled())
            return false;

        return true;
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
        return Duration.between(lastDisconnectedInstant, Instant.now()).getSeconds();
    }

    private boolean isSavedSSID(String ssid) {
        HashSet<String> wifis = prefHelper.retrieveSet("wifis");
        for (String wifiName : wifis) {
            if (wifiName.equals(ssid)) {
                return true;
            }
        }
        return false;
    }
} // End class WiFiBroadcasts

class DisplayBroadcasts extends BroadcastReceiver{
    private static final String TAG = "DisplayBroadcasts";
    private Context mainContext;
    private BackgroundService backService;
    private SharedPreferencesHelper prefHelper;

    public DisplayBroadcasts(Context mainContext, BackgroundService backService){
        // Constructor
        this.mainContext = mainContext;
        this.backService = backService;
        this.prefHelper = new SharedPreferencesHelper(this.mainContext);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
            Log.i(TAG, "Screen has been turned on");
        } else if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
            Log.i(TAG, "Screen has been turned off");
        }
    }


} // End class DisplayBroadcasts
