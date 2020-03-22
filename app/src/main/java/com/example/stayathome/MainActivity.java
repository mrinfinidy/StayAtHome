package com.example.stayathome;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.stayathome.background.BackgroundService;
import com.example.stayathome.helper.NotificationHelper;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.example.stayathome.helper.SharedPreferencesHelper;
import com.example.stayathome.interfacelogic.TreeInfo;
import com.example.stayathome.interfacelogic.TreeManager;
import com.example.stayathome.treedatabase.Tree;
import com.example.stayathome.treedatabase.TreeDBActions;
import com.example.stayathome.treedatabase.TreeDatabase;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

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

    private SharedPreferencesHelper prefHelper;
    private NotificationHelper notHelper;
    private BackgroundService backgroundService;

    private BroadcastReceiver minuteUpdateReceiver;
    private int countMinutes;
    private int virtualTreeState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent createTree = getIntent();
        boolean creationPending = false;
        if (createTree.getParcelableExtra("Tree") != null) {
            Tree newVTree = createTree.getParcelableExtra("Tree");
            creationPending = true;
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Assign SharedPreferences object
        prefHelper = new SharedPreferencesHelper(getApplicationContext());

        // Create notification channels
        notHelper = new NotificationHelper();
        notHelper.createGrowthProgressNotificationChannel(getApplicationContext());

        // Check if opened for first time
        boolean isFirstUsage = prefHelper.retrieveBoolean("first_usage");

        if (isFirstUsage) {
            //first usage
            prefHelper.storeBoolean("first_usage", false);

            //initial setup screen
            Intent firstTime = new Intent(MainActivity.this, InitialSetup.class);
            startActivity(firstTime);
        }
        //regular execution
        TreeDBActions treeDBActions = new TreeDBActions(getApplicationContext());
        TreeInfo treeInfo = new TreeInfo(treeDBActions);
        TreeManager treeManager = new TreeManager(treeDBActions);

        //perform action based on if new tree needs to be planted
        try {
            if (creationPending) {
                creationPending = false;
            } else if (needNewVTree(treeInfo)) {
                //show button to plant new virtual tree
                Button plantVTreeBtn = findViewById(R.id.plantVTreeBtn);
                plantVTreeBtn.setVisibility(View.VISIBLE);
            } else {
                showCurrentTree(treeInfo);
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //virtualTreeState = prefHelper.retrieveInt("current_growth");
        //TextView virtualTreeGrowth = findViewById(R.id.virtualTreeGrowth);

    }

    //all virtual trees already grown
    public void showGrownTrees(View v) {
        Intent showTrees = new Intent(MainActivity.this, GrownTrees.class);
        startActivity(showTrees);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void startNewChallenge(View v){
        prefHelper.storeLong("challenge_duration", 30);
        prefHelper.storeLong("allowed_time_disconnected", 20);
        prefHelper.storeString("tree_name", "Walter");
        startService(new Intent(this, BackgroundService.class));
    }

    //while app is running update tree growth --> call updateTree()
    public void startMinuteUpdater() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        minuteUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                    updateTree();
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

    //check if new virtual tree needs to be planted
    private boolean needNewVTree(TreeInfo treeInfo) throws ExecutionException, InterruptedException {
        //if there are no trees in this wifi
        String ssid = prefHelper.retrieveString("wifi_name");
        List<Tree> allTreesInWifi = treeInfo.treesInWifi(ssid);
        if (allTreesInWifi == null || allTreesInWifi.size() == 0) {
            return true;
        }
        //if current tree is fully grown
        int growthState =  treeInfo.growthState(allTreesInWifi.get(allTreesInWifi.size() - 1));
        if (growthState == 5) {
            return true;
        }

        return false;
    }

    //start new activities to get info what tree should be planted
    public void plantVTree(View v) {
        Tree newVTree = new Tree("project", "wifi", "name", -1);

        Intent chooseProject = new Intent(MainActivity.this, ChooseProject.class);
        chooseProject.putExtra("Tree", newVTree);
        startActivity(chooseProject);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void showCurrentTree(TreeInfo treeInfo) throws ExecutionException, InterruptedException {
        String ssid = prefHelper.retrieveString("wifi_name");
        List<Tree> currentTrees = treeInfo.treesInWifi(ssid);
        //display last tree in list
    }

    //update Tree currently on screen
    public void updateTree() {

    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
} // End class MainActivity