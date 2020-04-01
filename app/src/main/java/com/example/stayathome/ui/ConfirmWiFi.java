package com.example.stayathome.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.stayathome.R;
import com.example.stayathome.helper.SharedPreferencesHelper;
import com.example.stayathome.treedatabase.Tree;

public class ConfirmWiFi extends AppCompatActivity {

    SharedPreferencesHelper prefHelper;

    public static Activity confirmWifi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_wi_fi);

        confirmWifi = this;

        prefHelper = new SharedPreferencesHelper(getApplicationContext());

        TextView homeWiFi = findViewById(R.id.homeWiFi);
        final String ssid = prefHelper.retrieveString("wifi_name");
        homeWiFi.setText(ssid);

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

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
