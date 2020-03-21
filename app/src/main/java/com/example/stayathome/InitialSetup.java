package com.example.stayathome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigInteger;

public class InitialSetup extends AppCompatActivity {

    SharedPreferences sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_setup);

        // Assign SharedPreferences when the Activity is created
        sharedPrefs = getSharedPreferences(getResources().getString(R.string.shared_prefs), MODE_PRIVATE);

        //save SSID when 'wifi betaetigen' is pressed
        Button confirmWifi = (Button) findViewById(R.id.confirmWifi);
        confirmWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo connection = wifiManager.getConnectionInfo();
                //retrieve SSID
                String ssid = connection.getSSID();
                //store SSID
                sharedPrefs.edit().putString("wifi_name", ssid).apply();
                //retrieve BSSID
                String bssid = connection.getBSSID();
                //store BSSID
                sharedPrefs.edit().putString("wifi_id", bssid).apply();

                String wifiConfirmation = getResources().getString(R.string.wifi_confirmation);
                //Toast.makeText(getApplicationContext(), wifiConfirmation, Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), ssid, Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), bssid, Toast.LENGTH_LONG).show();

                finish();
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
