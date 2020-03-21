package com.example.stayathome;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.stayathome.helper.SharedPreferencesHelper;

public class InitialSetup extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_LOCATION = 1;
    SharedPreferencesHelper prefHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_setup);

        // Assign SharedPreferencesHelper when the Activity is created
        prefHelper = new SharedPreferencesHelper(this);

        // Request permission for location in order to receive the name of the SSID and BSSID
        while(!locationPermissionGranted()){
            requestLocationPermission();
        }

        // Save SSID when 'wifi betaetigen' is pressed
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

    private boolean locationPermissionGranted(){
        // Return true if access to the location permission has been granted
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_LOCATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                    break;
                } else {
                    // Permission denied
                    break;
                }
            }
            // Add more cases to the switch to check for other permission that might have been requested
        }
    }
} // End class InitialSetup
