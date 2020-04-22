package com.example.stayathome.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.service.chooser.ChooserTargetService;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.stayathome.MainAdapter;
import com.example.stayathome.R;
import com.example.stayathome.helper.SharedPreferencesHelper;
import com.example.stayathome.server.ServerBackground;
import com.example.stayathome.treedatabase.Tree;
import com.example.stayathome.treedatabase.TreeViewModel;

import java.util.ArrayList;
import java.util.List;


public class ChooseProject extends AppCompatActivity {

    RecyclerView locationsRecView;
    RecyclerView.LayoutManager locationsLayout;
    RecyclerView.Adapter locationsAdapter;
    ArrayList<String> locations;
    public static String selectedLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_project);

        //get ssid
        final WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        //connect to database
        final ArrayList<Tree> plantableTrees = new ArrayList<>();
        final TreeViewModel treeViewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(TreeViewModel.class);
        treeViewModel.getTrees().observe(this, new Observer<List<Tree>>() {
            @Override
            public void onChanged(List<Tree> trees) {
                Log.i("Choose Project: ", "DB intreaction");
                if (wifiManager != null) {
                    for (Tree tree : trees) {
                        if (tree.getWifi().equals(wifiManager.getConnectionInfo().getSSID()) && tree.isPlantable()) {
                            plantableTrees.add(tree);
                        }
                    }
                }
            }
        });

        //locations for user to choose
        locations = new ArrayList<>();
        locations.add("Karlsruhe");
        locations.add("Jena");
        locations.add("Weimar");
        locations.add("Frankfurt");
        locations.add("Berlin");

        buildRecyclerView();

        Button confirmProjectBtn = (Button) findViewById(R.id.confirmProjectBtn);
        confirmProjectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedLocation == null) {
                    Toast.makeText(getApplicationContext(), "You need to select a location", Toast.LENGTH_LONG).show();
                } else {
                    //send plant real tree assignment to server
                    sendTree();
                    //update # grown virtual trees
                    int virtualTreesLimit = 2;
                    for (int i = 0; i < virtualTreesLimit; i++) {
                        plantableTrees.get(i).setPlantable(false);
                        treeViewModel.update(plantableTrees.get(i));
                    }
                    //return to grwon trees activity
                    Toast.makeText(getApplicationContext(), "Tree planted in " + selectedLocation, Toast.LENGTH_LONG).show();
                    Intent confirmProject = new Intent(ChooseProject.this, GrownTrees.class);
                    startActivity(confirmProject);
                    finish();
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }
        });
    }

    private void sendTree() {
        SharedPreferencesHelper prefHelper = new SharedPreferencesHelper(getApplicationContext());
        String userName = prefHelper.retrieveString("user_name");

        ServerBackground serverBackground = new ServerBackground(this);
        serverBackground.execute("assignment" ,userName, selectedLocation);
    }

    //locations are displayed here
    public void buildRecyclerView() {
        locationsRecView = (RecyclerView) findViewById(R.id.locationsRecView);
        ViewGroup.LayoutParams rvParams = locationsRecView.getLayoutParams();
        rvParams.height = (int) (getScreenHeight() * 0.4);
        locationsRecView.setPadding(10,10,10,10);
        locationsRecView.setHasFixedSize(true);
        locationsLayout = new LinearLayoutManager(this);
        locationsAdapter = new MainAdapter(getApplicationContext(), locations); //use strings stored in locations

        locationsRecView.setLayoutManager(locationsLayout);
        locationsRecView.setAdapter(locationsAdapter);
    }

    //screen height in pixels
    private int getScreenHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
