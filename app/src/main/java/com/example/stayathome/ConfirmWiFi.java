package com.example.stayathome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.stayathome.helper.SharedPreferencesHelper;
import com.example.stayathome.treedatabase.Tree;

public class ConfirmWiFi extends AppCompatActivity {

    SharedPreferencesHelper prefHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent confirmWifi = getIntent();
        final Tree newVTree = confirmWifi.getParcelableExtra("Tree");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_wi_fi);

        prefHelper = new SharedPreferencesHelper(getApplicationContext());

        TextView homeWiFi = findViewById(R.id.homeWiFi);
        final String ssid = prefHelper.retrieveString("wifi_name");
        homeWiFi.setText(ssid);

        Button confirmWiFiBtn = findViewById(R.id.confirmWiFiBtn);
        confirmWiFiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newVTree.setWifi(ssid);
                Intent chooseVTree = new Intent(ConfirmWiFi.this, ChooseVTree.class);
                chooseVTree.putExtra("Tree", newVTree);
                startActivity(chooseVTree);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }
}
