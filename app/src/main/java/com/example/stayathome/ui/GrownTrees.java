package com.example.stayathome.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stayathome.R;
import com.example.stayathome.treedatabase.Tree;
import com.example.stayathome.treedatabase.TreeViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class GrownTrees extends AppCompatActivity {

    private TreeViewModel treeViewModel;
    private List<Tree> plantableTrees;
    private int numGrownTrees;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grown_trees);

        //connect to database
        treeViewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(TreeViewModel.class);
        treeViewModel.getTrees().observe(this, new Observer<List<Tree>>() {
            @Override
            public void onChanged(List<Tree> trees) {
                Log.i("GrownTrees: ", "DB intreaction");
                updateTreeInfos(trees);
            }
        });

        plantableTrees = new ArrayList<>();
        numGrownTrees = 0;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.activity_grown_trees);
    }

    //update number of plantable trees and displayed current trees
    void updateTreeInfos(List<Tree> trees) {
        String vTrees = "";

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            for (Tree tree : trees) {
                if (tree.getWifi().equals(wifiManager.getConnectionInfo().getSSID())) {
                    vTrees += tree.getName() + " " + tree.getGrowthState() + " " + tree.isPlantable() + "\n";
                    if (tree.isPlantable() && !plantableTrees.contains(tree)) {
                        plantableTrees.add(tree);
                        numGrownTrees++;
                    }
                }
            }
        }

        //display trees in wifi
        TextView treesInWifiDisplay = findViewById(R.id.vTreesDisplay);
        treesInWifiDisplay.setText(vTrees);
        //display # plantable trees
        TextView plantableTreesDisplay = findViewById(R.id.currentVirtualTrees);
        plantableTreesDisplay.setText(numGrownTrees + "");
    }

    public void plantTree(View v) throws ExecutionException, InterruptedException {
        //max. # of virtual trees
        int virtualTreesLimit = 2;

        //plant real tree if limit reached otherwise show # of virtual trees still needed
        if (numGrownTrees >= virtualTreesLimit) {
            //choose project
            Intent chooseProject = new Intent(GrownTrees.this, ChooseProject.class);
            startActivity(chooseProject);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else {
            int virtualTreesNeeded = virtualTreesLimit - numGrownTrees;
            Toast.makeText(getApplicationContext(), "Dir fehlen noch " + virtualTreesNeeded + " Bäumchen", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
