package com.example.stayathome.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stayathome.R;
import com.example.stayathome.helper.SharedPreferencesHelper;
import com.example.stayathome.interfacelogic.TreeInfo;
import com.example.stayathome.treedatabase.Tree;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class GrownTrees extends AppCompatActivity {

    private TextView vTreesDisplay;
    private List<Tree> treesInWifi;
    private List<Tree> availableTrees;
    private int numGrownTrees;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.activity_grown_trees);

        //calculate # available trees in Wifi
        numGrownTrees = 0;
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        availableTrees = new ArrayList<Tree>();

        try {
            treesInWifi = MainActivity.treeInfo.treesInWifi(wifiManager.getConnectionInfo().getSSID());
            for (Tree tree : treesInWifi) {
                if (tree.isToPlant()) {
                    availableTrees.add(tree);
                    numGrownTrees++;
                }
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        //display # available trees in Wifi
        TextView currentVirtualTrees = findViewById(R.id.currentVirtualTrees);
        currentVirtualTrees.setText(numGrownTrees + "");

        //display all trees in Wifi
        vTreesDisplay = findViewById(R.id.vTreesDisplay);
        showTrees();
    }

    public void plantTree(View v) throws ExecutionException, InterruptedException {
        //max. # of virtual trees
        int virtualTreesLimit = 2;

        //plant real tree if limit reached otherwise show # of virtual trees still needed
        if (numGrownTrees >= virtualTreesLimit) {
            //reset # of virtual trees
            for (Tree tree : availableTrees) {
                MainActivity.treeManager.editPlantability(tree,false);
                numGrownTrees--;
            }

            //choose project
            Intent chooseProject = new Intent(GrownTrees.this, ChooseProject.class);
            startActivity(chooseProject);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else {
            int virtualTreesNeeded = virtualTreesLimit - numGrownTrees;
            Toast.makeText(getApplicationContext(), "Dir fehlen noch " + virtualTreesNeeded + " Bäumchen", Toast.LENGTH_LONG).show();
        }
    }

    //show names grown virtual trees
    private void showTrees() {
        //write tree names in string
        String vTrees = "";
        for (Tree tree : treesInWifi) {
            vTrees += tree.getName() + " " + tree.getTreeType() + " " + tree.getGrowthState() + " " + tree.isToPlant() + "\n";
        }

        vTreesDisplay.setText(vTrees);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
