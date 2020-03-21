package com.example.stayathome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.stayathome.helper.SharedPreferencesHelper;

public class InitialSetup extends AppCompatActivity {

    SharedPreferencesHelper prefHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_setup);

        // Assign SharedPreferencesHelper when the Activity is created
        prefHelper = new SharedPreferencesHelper(this);

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
                prefHelper.storeString("wifi_name", ssid);
                //retrieve BSSID
                String bssid = connection.getBSSID();
                //store BSSID
                prefHelper.storeString("wifi_id", bssid);

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
