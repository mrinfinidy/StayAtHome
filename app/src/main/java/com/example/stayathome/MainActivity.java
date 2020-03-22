package com.example.stayathome;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import org.w3c.dom.Text;

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

    private static final String TAG = "MainActivity";
    private SharedPreferencesHelper prefHelper;
    private NotificationHelper notHelper;
    private int[] treeDrawables;
    private HandlerThread handlerThread;
    private Handler mHandler;
    private Runnable runnableScreenUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent createTree = getIntent();
        boolean creationPending = false;
        Tree newVTree= null;
        if (createTree.getParcelableExtra("Tree") != null) {
            newVTree = createTree.getParcelableExtra("Tree");
            creationPending = true;
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Assign SharedPreferences object
        prefHelper = new SharedPreferencesHelper(getApplicationContext());

        // Create notification channels
        notHelper = new NotificationHelper();
        notHelper.createGrowthProgressNotificationChannel(getApplicationContext());

        // Prepare drawables
        prepareTreeDrawables();

        findViewById(R.id.potImageView).setEnabled(false);

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
        final TreeInfo treeInfo = new TreeInfo(treeDBActions);
        final TreeManager treeManager = new TreeManager(treeDBActions);

        //perform action based on if new tree needs to be planted
        try {
            if (creationPending) {
                if (newVTree != null) {
                    createVirtualTree(treeManager, newVTree);
                }
                creationPending = false;
            } else if (needNewVTree(treeInfo)) {
                //show button to plant new virtual tree
                Button plantVTreeBtn = findViewById(R.id.plantVTreeBtn);
                plantVTreeBtn.setVisibility(View.VISIBLE);
            } else {
                //showCurrentTree(treeInfo);
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //virtualTreeState = prefHelper.retrieveInt("current_growth");
        //TextView virtualTreeGrowth = findViewById(R.id.virtualTreeGrowth);

    }

    @Override
    protected void onResume() {
        super.onResume();
        startScreenUpdater();
    }

    @Override
    protected void onPause(){
        super.onPause();
        mHandler.removeCallbacks(runnableScreenUpdate);
        Log.i(TAG, "ScreenUpdater stopped");
    }

    private void prepareTreeDrawables(){
        int pappel1 = R.drawable.ic_pappel1;
        int pappel2 = R.drawable.ic_pappel2;
        int pappel3 = R.drawable.ic_pappel3;
        int pappel4 = R.drawable.ic_pappel4;
        int pappel5 = R.drawable.ic_pappel5;
        treeDrawables = new int[]{pappel1, pappel2, pappel3, pappel4, pappel5};
    }

    // While activity is in running in foreground --> call updateTree()
    public void startScreenUpdater() {
        // Update tree status every 5 seconds
        Log.i(TAG, "ScreenUpdater started");
        updateTree();

        handlerThread = new HandlerThread("ScreenUpdater");
        handlerThread.start();
        mHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message inputMessage){
                super.handleMessage(inputMessage);
                updateTree();
            }
        };

        runnableScreenUpdate = new Runnable() {
            @Override
            public void run() {
                mHandler.sendMessage(new Message());
                mHandler.postDelayed(runnableScreenUpdate, 5 * 1000);
            }
        };
        mHandler.postDelayed(runnableScreenUpdate, 5 * 1000);
    }

    //all virtual trees already grown
    public void showGrownTrees(View v) {
        Intent showTrees = new Intent(MainActivity.this, GrownTrees.class);
        startActivity(showTrees);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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
        if (prefHelper.retrieveInt("current_growth") == 5) {
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

    // Plant a new tree or renew an existing tree
    public void plantVTree2(View v) {
        if(prefHelper.retrieveInt("current_growth") == 0){
            String treeName = "Walter";

            int grown_trees = prefHelper.retrieveInt("grown_trees_virtual") + 1;
            prefHelper.storeInt("grown_trees_virtual", grown_trees);
            prefHelper.storeInt("growth_on_screen", 0);
            prefHelper.storeLong("challenge_duration", 20);
            prefHelper.storeLong("allowed_time_disconnected", 20);
            prefHelper.storeString("tree_name", treeName);
        }
        findViewById(R.id.potImageView).setEnabled(false);
        startService(new Intent(this, BackgroundService.class));
    }

    private void createVirtualTree(TreeManager treeManager, Tree newVTree) {
        TextView virtualTreeGrowth = findViewById(R.id.virtualTreeGrowth);
        virtualTreeGrowth.setText(newVTree.getName());
        treeManager.insertTree(newVTree);
        findViewById(R.id.potImageView).setEnabled(true);
    }

    private void showCurrentTree(TreeInfo treeInfo) throws ExecutionException, InterruptedException {
        String ssid = prefHelper.retrieveString("wifi_name");
        List<Tree> currentTrees = treeInfo.treesInWifi(ssid);
        //display last tree in list
    }

    // Update tree currently on screen
    public void updateTree() {
        ImageView ivTop = findViewById(R.id.potImageView);
        ImageView ivPlant = findViewById(R.id.plantImageView);

        int new_tree_status = prefHelper.retrieveInt("current_growth");
        int old_tree_status = prefHelper.retrieveInt("growth_on_screen");
        if (new_tree_status != old_tree_status) {
            ivPlant.setBackground(getResources().getDrawable(this.treeDrawables[new_tree_status - 1]));
            ivPlant.setVisibility(View.VISIBLE);
            prefHelper.storeInt("growth_on_screen", new_tree_status);
            ivTop.setEnabled(true);
        } else if (new_tree_status == 5) {
            ivPlant.setBackground(getResources().getDrawable(this.treeDrawables[4]));
            ivTop.setEnabled(true);
            prefHelper.storeInt("current_growth", 0);
            prefHelper.storeInt("growth_on_screen", 0);
        }

        Log.i(TAG, "Tree status on screen has been updated");
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
} // End class MainActivity
