package com.example.stayathome;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.stayathome.helper.SharedPreferencesHelper;
import com.example.stayathome.treedatabase.Tree;
import com.example.stayathome.treedatabase.TreeRepository;
import com.example.stayathome.treedatabase.TreeViewModel;

/*ALL SHARED PREFERENCES KEYS
key: first_usage --> boolean to check if it is the first time that the app is launched

key: current_growth --> state of virtual tree currently growing

key: grown_trees_virtual --> number of already grown virtual trees

key: wifi_name --> SSID (network name)

key: wifi_id --> BSSID (MAC address)

key: challenge_duration --> planned duration of the current challenge in seconds (as long, in seconds)

key: challenge_start_time --> time when the current challenge has been started (as long)

key: actual_time_in_challenge --> time that has already passed in the challenge (as long, in seconds)
*/

public class MainActivity extends AppCompatActivity {

    SharedPreferencesHelper prefHelper;

    private BroadcastReceiver minuteUpdateReceiver;
    private int countMinutes;
    private int virtualTreeState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Assign SharedPreferences when the Activity is created
        prefHelper = new SharedPreferencesHelper(this);

        //check opened for first time
        boolean isFirstUsage = prefHelper.retrieveBoolean("first_usage");

        if (isFirstUsage) {
            //first usage
            prefHelper.storeBoolean("first_usage", false);

            //initial setup screen
            Intent firstTime = new Intent(MainActivity.this, InitialSetup.class);
            startActivity(firstTime);
        }
        //regular execution
        //show state of currently growing tree
        TreeRepository treeRepository = new TreeRepository((Application) getApplicationContext());
        Tree tree = new Tree("1", "2", "3", 4);
        treeRepository.insert(tree);

        virtualTreeState = prefHelper.retrieveInt("current_growth");
        TextView virtualTreeGrowth = findViewById(R.id.virtualTreeGrowth);

        virtualTreeGrowth.setText(treeRepository.getTree(1) + "");
    }

    //all virtual trees already grown
    public void showGrownTrees(View v) {
        Intent showTrees = new Intent(MainActivity.this, GrownTrees.class);
        startActivity(showTrees);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    //while app is running update tree growth --> call updateTree()
    public void startMinuteUpdater() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        minuteUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                    //updateTree();
            }
        };
        registerReceiver(minuteUpdateReceiver, intentFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startMinuteUpdater();
    }

    @Override
    protected void onPause(){
        super.onPause();
        unregisterReceiver(minuteUpdateReceiver);
    }

    //update Tree currently on screen
    public void updateTree() {
        virtualTreeState = prefHelper.retrieveInt("current_growth");
        TextView virtualTreeGrowth = findViewById(R.id.virtualTreeGrowth);
        virtualTreeGrowth.setText(virtualTreeState + "");
    }
}
