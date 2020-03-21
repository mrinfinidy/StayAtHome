package com.example.stayathome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/*ALL SHARED PREFERENCES KEYS
key: first_usage --> boolean to check if it is the first time that the app is launched

key: current_growth --> state of virtual tree currently growing

key: grown_trees_virtual --> number of already grown virtual trees

key: wifi_name --> SSID (network name)

key: wifi_id --> BSSID (MAC address)
*/

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Assign SharedPreferences when the Activity is created
        sharedPrefs = getSharedPreferences(getResources().getString(R.string.shared_prefs), MODE_PRIVATE);

        //check opened for first time
        boolean isFirstUsage = sharedPrefs.getBoolean("first_usage", true);

        if (isFirstUsage) {
            //first usage
            sharedPrefs.edit().putBoolean("first_usage", false).apply();

            //initial setup screen
            Intent firstTime = new Intent(MainActivity.this, InitialSetup.class);
            startActivity(firstTime);
        }
        //regular execution
        int virtualTreeState = sharedPrefs.getInt("current_growth", 0);
        TextView virtualTreeGrowth = findViewById(R.id.virtualTreeGrowth);
        virtualTreeGrowth.setText(virtualTreeState + "");
    }

    public void showGrownTrees(View v) {
        Intent showTrees = new Intent(MainActivity.this, GrownTrees.class);
        startActivity(showTrees);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
