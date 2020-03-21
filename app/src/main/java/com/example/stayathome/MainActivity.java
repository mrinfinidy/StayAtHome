package com.example.stayathome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

/*ALL SHARED PREFERENCES AND KEYS
firstUsage --> boolean to check if it is the first time that the app is launched
key: first_usage

currentGrowth --> state of virtual tree currently growing
key: current_growth

grownTreesVirtual --> number of already grown virtual trees
key: grown_trees_virtual

wifiName --> SSID (network name)
key: wifi_name

wifiId --> BSSID (MAC address)
keyL wifi_id
 */
public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //check opened for first time
        SharedPreferences firstUsage = getSharedPreferences("firstUsage", MODE_PRIVATE);
        boolean isFirstUsage = firstUsage.getBoolean("first_usage", true);

        if (isFirstUsage) {
            //first usage
            SharedPreferences.Editor firstTimeEditor = firstUsage.edit();
            firstTimeEditor.putBoolean("first_usage", false);
            firstTimeEditor.apply();

            //initial setup screen
            Intent firstTime = new Intent(MainActivity.this, InitialSetup.class);
            startActivity(firstTime);
        }
        //regular execution
        SharedPreferences currentGrowth = getSharedPreferences("currentGrowth", MODE_PRIVATE);
        int virtualTreeState = currentGrowth.getInt("current_growth", 0);
        TextView virtualTreeGrowth = (TextView) findViewById(R.id.virtualTreeGrowth);
        virtualTreeGrowth.setText(virtualTreeState + "");
    }

    public void showGrownTrees(View v) {
        Intent showTrees = new Intent(MainActivity.this, GrownTrees.class);
        startActivity(showTrees);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

}
