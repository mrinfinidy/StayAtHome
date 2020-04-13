package com.example.stayathome.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.Image;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stayathome.R;
import com.example.stayathome.helper.SharedPreferencesHelper;
import com.example.stayathome.treedatabase.Tree;

import java.util.HashSet;

public class ConfirmWiFi extends AppCompatActivity {

    SharedPreferencesHelper prefHelper;

    public static Activity confirmWifi;
    private AnimationDrawable wifiAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_wi_fi);

        ImageView wifiBars = (ImageView) findViewById(R.id.wifiBars);
        wifiBars.setBackgroundResource(R.drawable.wifi_animation);
        wifiAnimation = (AnimationDrawable) wifiBars.getBackground();

        confirmWifi = this;
        HoldSelection.setCreationPending(true);

        //get name of currently connected wifi
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo= wifiManager.getConnectionInfo();
        final String ssid = wifiInfo.getSSID();
        TextView homeWiFi = findViewById(R.id.homeWiFi);
        homeWiFi.setText(ssid);
        addWifi(ssid);

        Button confirmWiFiBtn = findViewById(R.id.confirmWiFiBtn);
        confirmWiFiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HoldSelection.setWifiName(ssid);
                Intent chooseVTree = new Intent(ConfirmWiFi.this, ChooseVTree.class);
                startActivity(chooseVTree);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    private void addWifi(String ssid) {
        SharedPreferencesHelper prefHelper = new SharedPreferencesHelper(getApplicationContext());
        HashSet<String> wifis = prefHelper.retrieveSet("wifis");
        wifis.add(ssid);
        prefHelper.storeSet("wifis", wifis);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        wifiAnimation.start();
    }

    @Override
    public void onBackPressed() {
        HoldSelection.setCreationPending(false);
        super.onBackPressed();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
