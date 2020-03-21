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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_setup);

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
                SharedPreferences wifiName = getSharedPreferences("wifiName", MODE_PRIVATE);
                SharedPreferences.Editor wifiNameEditor = wifiName.edit();
                wifiNameEditor.putString("wifi_name", ssid);
                wifiNameEditor.commit();
                //retrieve BSSID
                String bssid = connection.getBSSID();
                //store BSSID
                SharedPreferences wifiMAC = getSharedPreferences("wifiMAC", MODE_PRIVATE);
                SharedPreferences.Editor wifiMACEditor = wifiMAC.edit();
                wifiNameEditor.putString("wifi_id", bssid);
                wifiMACEditor.commit();

                String wifiConfirmation = getResources().getString(R.string.wifi_confirmation);
                //Toast.makeText(getApplicationContext(), wifiConfirmation, Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), ssid, Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), bssid, Toast.LENGTH_LONG).show();

                finish();
            }
        });
    }
}
