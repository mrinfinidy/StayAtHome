package com.example.stayathome.ui;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stayathome.R;
import com.example.stayathome.background.BackgroundService;
import com.example.stayathome.helper.NotificationHelper;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.stayathome.helper.SharedPreferencesHelper;
import com.example.stayathome.treedatabase.Tree;
import com.example.stayathome.treedatabase.TreeViewModel;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

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
    private SwipeRefreshLayout swipeRefreshLayout;

    private Tree currentTree;
    private TreeViewModel treeViewModel;

    private WifiManager wifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //swipe to refresh
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeToRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                onResume();
            }
        });

        // Assign SharedPreferences object
        prefHelper = new SharedPreferencesHelper(getApplicationContext());

        // Create notification channels
        notHelper = new NotificationHelper();
        notHelper.createGrowthProgressNotificationChannel(getApplicationContext());

        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

        //connect to database
        treeViewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(TreeViewModel.class);
        treeViewModel.getTrees().observe(this, new Observer<List<Tree>>() {
            @Override
            public void onChanged(List<Tree> trees) {
                Log.i(TAG, "DB interaction");
                if (trees.size() > 0 && currentTree == null) {
                    getCurrentTree(trees);
                }
                onResume();
            }
        });

        findViewById(R.id.potImageView).setClickable(false);
        setNamePosition();

        // Check if opened for first time
        boolean isFirstUsage = prefHelper.retrieveBoolean("first_usage");

        if (isFirstUsage) {
            //first usage
            prefHelper.storeBoolean("first_usage", false);
            //initial setup screen
            Intent firstTime = new Intent(MainActivity.this, InitialSetup.class);
            startActivity(firstTime);
            //initialize db tree id
            prefHelper.storeInt("tree_id", 0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        /*
        while (!dbLoadComplete) {
            android.os.SystemClock.sleep(100);
        }
         */

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        startScreenUpdater();

        if (HoldSelection.isCreationPending()) {
            //create new virtual tree
            int treeID = prefHelper.retrieveInt("tree_id");
            currentTree = new Tree(treeID, HoldSelection.getWifiName(), HoldSelection.getTreeType(), HoldSelection.getTreeName(), 0);
            createVirtualTree();
            HoldSelection.setCreationPending(false);
            prefHelper.storeBoolean("ongoing_challenge", true);
            prepareTreeDrawables();
            //clear held selection
            HoldSelection.setTreeName(null);
            HoldSelection.setWifiName(null);
            HoldSelection.setTreeType(0);
            //finish selection process activities
            ConfirmWiFi.confirmWifi.finish();
            ConfirmWiFi.confirmWifi = null;
            ChooseVTree.chooseVTree.finish();
            ChooseVTree.chooseVTree = null;
            ChooseName.chooseName.finish();
            ChooseName.chooseName = null;
            //increment tree id for next tree
            prefHelper.storeInt("tree_id", treeID + 1);
        } else {
            //regular execution
            //perform action based on if new tree needs to be planted
            if (needNewVTree(wifiManager)) {
                //show button to plant new virtual tree
                Button plantVTreeBtn = findViewById(R.id.plantVTreeBtn);
                plantVTreeBtn.setVisibility(View.VISIBLE);
                prefHelper.storeBoolean("ongoing_challenge", false);
            } else {
                if (currentTree != null) {
                    prepareTreeDrawables();
                    showCurrentTree();
                }
            }
        }
    }

    //initialize currentTree
    void getCurrentTree(List<Tree> trees) {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            for (int i = trees.size() - 1; i >= 0; i--) {
                if (trees.get(i).getWifi().equals(wifiManager.getConnectionInfo().getSSID())) {
                    currentTree = trees.get(i);
                    return;
                }
            }
        }
    }

    //initialize tree pics to display with tree type selected for current tree
    private void prepareTreeDrawables(){
        int tree1, tree2, tree3, tree4, tree5;

        if (currentTree.getTreeType() == 1) {
            treeDrawables = new int[] {
                    R.drawable.ic_pappel1,
                    R.drawable.ic_pappel2,
                    R.drawable.ic_pappel3,
                    R.drawable.ic_pappel4,
                    R.drawable.ic_pappel5
            };
        } else if (currentTree.getTreeType() == 2) {
            //maple
        } else if (currentTree.getTreeType() == 3) {
            //cherry
        } else {
            //no match
        }
    }

    // While activity is in running in foreground --> call updateTree()
    public void startScreenUpdater() {
        // Update tree status every 1 seconds
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
                mHandler.postDelayed(runnableScreenUpdate, 1 * 1000);
            }
        };
        mHandler.postDelayed(runnableScreenUpdate, 1 * 1000);
    }

    private void setNamePosition() {
        TextView vTreeNameDisplay = findViewById(R.id.vTreeNameDisplay);
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) vTreeNameDisplay.getLayoutParams();

        layoutParams.setMargins(
                getScreenWidth() / 2,
                (int) Math.round(getScreenHeight()),
                getScreenWidth() / 2,
                (int) Math.round(getScreenHeight() / 10.0)
        );

        vTreeNameDisplay.setLayoutParams(layoutParams);
    }

    private int getScreenHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    private int getScreenWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    //all virtual trees already grown
    public void showGrownTrees(View v) {
        Intent showTrees = new Intent(MainActivity.this, GrownTrees.class);
        startActivity(showTrees);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }


    //check if new virtual tree needs to be planted
    //NEED TO CORRECT
    private boolean needNewVTree(WifiManager wifiManager){
        if (!isConnected())
            return false;

        if (wifiManager.getConnectionInfo().getSSID() != null) {
            if (prefHelper.retrieveInt(wifiManager.getConnectionInfo().getSSID()) == 0) {
                return true;
            }
        }

        //if current tree is fully grown
        return prefHelper.retrieveInt("current_growth") < 0;
    }

    //start new activities to get info what tree should be planted
    public void plantVTree(View v) {
        if (!isConnected()) {
            Toast.makeText(getApplicationContext(), "You can only plant a new tree while connected", Toast.LENGTH_LONG).show();
            return;
        }
        Intent chooseProject = new Intent(MainActivity.this, ConfirmWiFi.class);
        startActivity(chooseProject);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void createVirtualTree() {
        prefHelper.storeBoolean("ongoing_challenge", true);
        TextView vTreeNameDisplay = findViewById(R.id.vTreeNameDisplay);
        vTreeNameDisplay.setText(currentTree.getName());
        treeViewModel.insert(currentTree);
        prefHelper.storeInt("current_growth", 0);
        findViewById(R.id.potImageView).setClickable(true);
        //update tree numbers in wifi (for fast access while db's loading
        int wifiTreeCount = prefHelper.retrieveInt(currentTree.getWifi()) + 1;
        prefHelper.storeInt(currentTree.getWifi(), wifiTreeCount);
        //inform user that tree can be planted now
        TextView informUser = findViewById(R.id.informUser);
        informUser.setText(R.string.tapPotSeed);
        informUser.setVisibility(View.VISIBLE);
    }

    //show tree that was growing when main activity was stopped
    private void showCurrentTree() {
        //display last tree in list
        int treeStatus = prefHelper.retrieveInt("growth_on_screen");
        ImageView ivPlant = findViewById(R.id.plantImageView);
        if (treeStatus > 0 && treeStatus <= 5) {
            ivPlant.setBackground(getResources().getDrawable(this.treeDrawables[treeStatus - 1]));
            ivPlant.setVisibility(View.VISIBLE);
        } else  if (treeStatus > 5){
            ivPlant.setBackground(getResources().getDrawable(this.treeDrawables[4]));
            ivPlant.setVisibility(View.VISIBLE);
        }

        //set name on name board
        TextView treeNameDisplay = findViewById(R.id.vTreeNameDisplay);
        treeNameDisplay.setText(currentTree.getName());
    }

    //check if next growth state is available
    public void updateTree() {
        ImageView ivPot = findViewById(R.id.potImageView);

        int newTreeStatus = prefHelper.retrieveInt("current_growth");
        int oldTreeStatus = prefHelper.retrieveInt("growth_on_screen");

        TextView informUser = findViewById(R.id.informUser);

        if ((!prefHelper.retrieveBoolean("tree_alive") && (currentTree != null) && (prefHelper.retrieveInt("growth_on_screen") >= 0))) {
            ivPot.setClickable(true);
            informUser.setText(R.string.tapPotKill);
            informUser.setVisibility(View.VISIBLE);
            findViewById(R.id.treeDead).setVisibility(View.VISIBLE);
        }
        if (newTreeStatus != oldTreeStatus) {
            ivPot.setClickable(true);
            if (oldTreeStatus == 5) {
                informUser.setText(R.string.tapPotHarvest);
            } else if (oldTreeStatus >= 0){
                informUser.setText(R.string.tapPotGrow);
            }
            informUser.setVisibility(View.VISIBLE);
        }
    }

    //show next growth state or harvest tree on tap
    public void growNow(View v) {
        Log.i(TAG, "id: " + currentTree.getId());

        if (!isConnected()) {
            Toast.makeText(getApplicationContext(), "You can only grow while connected", Toast.LENGTH_LONG).show();
            return;
        }

        ImageView ivPlant = findViewById(R.id.plantImageView);

        int treeState = prefHelper.retrieveInt("current_growth");

        if (!prefHelper.retrieveBoolean("tree_alive")) {
            //kill
            killTree(ivPlant);
            Log.i(TAG, "kill");
        } else if (treeState == 0) {
            //seed
            seedTree();
            Log.i(TAG, "seed");
        } else if (treeState <= 5) {
            //grow
            growTree(ivPlant, treeState);
            Log.i(TAG, "grow");
        } else {
            //harvest
            harvestTree(ivPlant);
            Log.i(TAG, "harvest");
        }

        findViewById(R.id.informUser).setVisibility(View.INVISIBLE);
        findViewById(R.id.potImageView).setClickable(false);

        startService(new Intent(this, BackgroundService.class));
        Log.i(TAG, "Tree status on screen has been updated");
    }

    public void seedTree() {
        //change challenge duration here
        long challengeDuration = ThreadLocalRandom.current().nextInt(15 * 60, 46 * 60); //in seconds
        //long challengeDuration = 1;
        prefHelper.storeInt("growth_on_screen", 0);
        prefHelper.storeLong("challenge_duration", challengeDuration);
        prefHelper.storeString("tree_name", currentTree.getName());
        Toast.makeText(getApplicationContext(), "Seed planted", Toast.LENGTH_LONG).show();
    }

    public void growTree(ImageView ivPlant, int treeState) {
        //change challenge duration here
        long challengeDuration = ThreadLocalRandom.current().nextInt(4 * 3600, 5 * 3600); //in seconds
        //long challengeDuration = 1;
        prefHelper.storeLong("challenge_duration", challengeDuration);
        ivPlant.setBackground(getResources().getDrawable(this.treeDrawables[treeState - 1]));
        ivPlant.setVisibility(View.VISIBLE);
        prefHelper.storeInt("growth_on_screen", treeState);
        currentTree.setGrowthState(treeState);
        treeViewModel.update(currentTree);
    }

    public void harvestTree(ImageView ivPlant) {
        int grownTrees = prefHelper.retrieveInt("grown_trees_virtual") + 1;
        prefHelper.storeInt("grown_trees_virtual", grownTrees);
        prefHelper.storeBoolean("ongoing_challenge", false);
        ivPlant.setVisibility(View.INVISIBLE);
        prefHelper.storeInt("current_growth", -1);
        prefHelper.storeInt("growth_on_screen", -1);
        TextView vTreeNameDisplay = findViewById(R.id.vTreeNameDisplay);
        vTreeNameDisplay.setText("");
        findViewById(R.id.plantVTreeBtn).setVisibility(View.VISIBLE);
        currentTree.setPlantable(true);
        treeViewModel.update(currentTree);
    }

    public void killTree(ImageView ivPlant) {
        findViewById(R.id.treeDead).setVisibility(View.INVISIBLE);
        prefHelper.storeBoolean("ongoing_challenge", false);
        prefHelper.storeBoolean("tree_alive", true);
        treeViewModel.delete(currentTree);
        int grownTrees = prefHelper.retrieveInt("grown_trees_virtual") - 1;
        prefHelper.storeInt("grown_trees_virtual", grownTrees);
        ivPlant.setVisibility(View.INVISIBLE);
        prefHelper.storeInt("current_growth", -1);
        prefHelper.storeInt("growth_on_screen", -1);
        TextView vTreeNameDisplay = findViewById(R.id.vTreeNameDisplay);
        vTreeNameDisplay.setText("");
        findViewById(R.id.plantVTreeBtn).setVisibility(View.VISIBLE);
        //update tree numbers in wifi (for fast access while db's loading
        int wifiTreeCount = prefHelper.retrieveInt(currentTree.getWifi());
        prefHelper.storeInt(currentTree.getWifi(), wifiTreeCount--);
    }

    //check if wifi is enabled and for Android 8.1+ check gps too
    private boolean isConnected() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if ((locationManager != null && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) || !wifiManager.isWifiEnabled()) {
                return false;
            }
        }

        if (!wifiManager.isWifiEnabled())
            return false;

        return true;
    }

    @Override
    protected void onPause(){
        super.onPause();
        mHandler.removeCallbacks(runnableScreenUpdate);
        Log.i(TAG, "ScreenUpdater stopped");
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

} // End class MainActivity
