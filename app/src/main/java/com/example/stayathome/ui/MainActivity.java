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
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stayathome.R;
import com.example.stayathome.background.BackgroundService;
import com.example.stayathome.helper.NotificationHelper;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.stayathome.helper.SharedPreferencesHelper;
import com.example.stayathome.treedatabase.Tree;
import com.example.stayathome.treedatabase.TreeInfo;
import com.example.stayathome.treedatabase.TreeViewModel;

import java.util.List;

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
                if (trees.size() > 0) {
                    getCurrentTree(trees);
                }
            }
        });


        findViewById(R.id.potImageView).setClickable(false);

        // Check if opened for first time
        boolean isFirstUsage = prefHelper.retrieveBoolean("first_usage");

        if (isFirstUsage) {
            //first usage
            prefHelper.storeBoolean("first_usage", false);
            //initial setup screen
            Intent firstTime = new Intent(MainActivity.this, InitialSetup.class);
            startActivity(firstTime);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        startScreenUpdater();

        if (HoldSelection.isCreationPending()) {
            //create new virtual tree
            currentTree = new Tree(HoldSelection.getWifiName(), HoldSelection.getTreeType(), HoldSelection.getTreeName(), 0);
            createVirtualTree(currentTree);
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
                mHandler.postDelayed(runnableScreenUpdate, 1 * 1000);
            }
        };
        mHandler.postDelayed(runnableScreenUpdate, 1 * 1000);
    }

    //all virtual trees already grown
    public void showGrownTrees(View v) {
        Intent showTrees = new Intent(MainActivity.this, GrownTrees.class);
        startActivity(showTrees);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }


    //check if new virtual tree needs to be planted
    private boolean needNewVTree(WifiManager wifiManager){
        if (!isConnected())
            return false;

        //if there are no trees in this wifi
        if (currentTree == null)
            return true;

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

    private void createVirtualTree(Tree newVTree) {
        prefHelper.storeBoolean("ongoing_challenge", true);
        TextView vTreeNameDisplay = findViewById(R.id.vTreeNameDisplay);
        vTreeNameDisplay.setText(newVTree.getName());
        treeViewModel.insert(newVTree);
        prefHelper.storeInt("current_growth", 0);
        findViewById(R.id.potImageView).setClickable(true);
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
        prefHelper.storeInt("growth_on_screen", 0);
        prefHelper.storeLong("challenge_duration", 5);
        prefHelper.storeString("tree_name", currentTree.getName());
        Toast.makeText(getApplicationContext(), "Seed planted", Toast.LENGTH_LONG).show();
    }

    public void growTree(ImageView ivPlant, int treeState) {
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
        findViewById(R.id.plantVTreeBtn).setVisibility(View.VISIBLE);
        currentTree.setPlantable(true);
        treeViewModel.update(currentTree);
    }

    public void killTree(ImageView ivPlant) {
        prefHelper.storeBoolean("ongoing_challenge", false);
        prefHelper.storeBoolean("tree_alive", true);
        treeViewModel.delete(currentTree);
        int grownTrees = prefHelper.retrieveInt("grown_trees_virtual") - 1;
        prefHelper.storeInt("grown_trees_virtual", grownTrees);
        ivPlant.setVisibility(View.INVISIBLE);
        prefHelper.storeInt("current_growth", -1);
        prefHelper.storeInt("growth_on_screen", -1);
        findViewById(R.id.plantVTreeBtn).setVisibility(View.VISIBLE);
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
