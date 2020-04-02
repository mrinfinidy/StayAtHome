package com.example.stayathome.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
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

import com.example.stayathome.R;
import com.example.stayathome.background.BackgroundService;
import com.example.stayathome.helper.NotificationHelper;
import androidx.appcompat.app.AppCompatActivity;

import com.example.stayathome.helper.SharedPreferencesHelper;
import com.example.stayathome.interfacelogic.TreeInfo;
import com.example.stayathome.interfacelogic.TreeManager;
import com.example.stayathome.treedatabase.Tree;
import com.example.stayathome.treedatabase.TreeDBActions;

import org.w3c.dom.Text;

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

    private TreeManager treeManager;
    private Tree currentTree;
    static TreeInfo treeInfo;

    private String projectName;
    private String wifiName;
    private String treeType;
    private String vTreeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Assign SharedPreferences object
        prefHelper = new SharedPreferencesHelper(getApplicationContext());

        // Create notification channels
        notHelper = new NotificationHelper();
        notHelper.createGrowthProgressNotificationChannel(getApplicationContext());

        // Prepare drawables
        prepareTreeDrawables();

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

        boolean isFirstUsage = prefHelper.retrieveBoolean("first_usage");
        startScreenUpdater();


        final TreeDBActions treeDBActions = new TreeDBActions(getApplicationContext());
        treeInfo = new TreeInfo(treeDBActions);
        treeManager = new TreeManager(treeDBActions);

        if (HoldSelection.isCreationPending()) {
            //create new virtual tree
            currentTree = new Tree(HoldSelection.getWifiName(), HoldSelection.getTreeType(), HoldSelection.getTreeName(), 0);
            createVirtualTree(treeManager, currentTree);
            HoldSelection.setCreationPending(false);
            //clear held selection
            HoldSelection.setTreeName(null);
            HoldSelection.setWifiName(null);
            HoldSelection.setTreeType(null);
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
            try {
                if (needNewVTree(treeInfo)) {
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
        }
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
        if (prefHelper.retrieveInt("current_growth") < 0) {
            return true;
        }

        return false;
    }

    //start new activities to get info what tree should be planted
    public void plantVTree(View v) {
        Intent chooseProject = new Intent(MainActivity.this, ConfirmWiFi.class);
        startActivity(chooseProject);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void createVirtualTree(TreeManager treeManager, Tree newVTree) {
        TextView vTreeNameDisplay = findViewById(R.id.vTreeNameDisplay);
        vTreeNameDisplay.setText(newVTree.getName());
        treeManager.insertTree(newVTree);
        prefHelper.storeInt("current_growth", 0);
        findViewById(R.id.potImageView).setClickable(true);
        //inform user that tree can be planted now
        TextView informUser = findViewById(R.id.informUser);
        informUser.setText("TAP HERE TO PUT TREE IN POT");
        informUser.setVisibility(View.VISIBLE);
    }

    //show tree that was growing when main activity was stopped
    private void showCurrentTree(TreeInfo treeInfo) throws ExecutionException, InterruptedException {
        if (currentTree == null) {
            String ssid = prefHelper.retrieveString("wifi_name");
            List<Tree> currentTrees = treeInfo.treesInWifi(ssid);
            currentTree = currentTrees.get(currentTrees.size() - 1);
        }
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

        if (!prefHelper.retrieveBoolean("tree_alive")) {
            ivPot.setClickable(true);
            informUser.setText("TAP POT TO KILL TREE");
            informUser.setVisibility(View.VISIBLE);
        }
        if (newTreeStatus != oldTreeStatus) {
            ivPot.setClickable(true);
            if (oldTreeStatus == 5) {
                informUser.setText("TAP POT TO HARVEST");
            } else {
                informUser.setText("TAP POT TO GROW");
            }
            informUser.setVisibility(View.VISIBLE);
        }
    }

    //show next growth state or harvest tree on tap
    public void growNow(View v) throws ExecutionException, InterruptedException {
        ImageView ivPlant = findViewById(R.id.plantImageView);

        int treeStatus = prefHelper.retrieveInt("current_growth");

        if (!prefHelper.retrieveBoolean("tree_alive")) {
            killTree(ivPlant);
        } else if (treeStatus == 0) {
            int grownTrees = prefHelper.retrieveInt("grown_trees_virtual") + 1;
            prefHelper.storeInt("grown_trees_virtual", grownTrees);
            prefHelper.storeInt("growth_on_screen", 0);
            prefHelper.storeLong("challenge_duration", 20);
            prefHelper.storeLong("allowed_time_disconnected", 20);
            prefHelper.storeString("tree_name", currentTree.getName());
            Toast.makeText(getApplicationContext(), "Seed planted", Toast.LENGTH_LONG).show();
        } else if (treeStatus <= 5) {
            ivPlant.setBackground(getResources().getDrawable(this.treeDrawables[treeStatus - 1]));
            ivPlant.setVisibility(View.VISIBLE);
            prefHelper.storeInt("growth_on_screen", treeStatus);
            treeManager.editGrowthState(currentTree, treeStatus);
        } else {
            ivPlant.setVisibility(View.INVISIBLE);
            prefHelper.storeInt("current_growth", -1);
            prefHelper.storeInt("growth_on_screen", -1);
            findViewById(R.id.plantVTreeBtn).setVisibility(View.VISIBLE);
        }

        findViewById(R.id.informUser).setVisibility(View.INVISIBLE);
        findViewById(R.id.potImageView).setClickable(false);

        startService(new Intent(this, BackgroundService.class));
        Log.i(TAG, "Tree status on screen has been updated");
    }

    public void killTree(ImageView ivPlant) throws ExecutionException, InterruptedException {
        prefHelper.storeBoolean("tree_alive", true);
        treeManager.deleteById(currentTree.getId());
        ivPlant.setVisibility(View.INVISIBLE);
        prefHelper.storeInt("current_growth", -1);
        prefHelper.storeInt("growth_on_screen", -1);
        findViewById(R.id.plantVTreeBtn).setVisibility(View.VISIBLE);
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
