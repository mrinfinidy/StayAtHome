package com.example.stayathome.ui;

import androidx.appcompat.app.AppCompatActivity;

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

    int numGrownTrees;
    SharedPreferencesHelper prefHelper;

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.activity_grown_trees);

        // Assign SharedPreferencesHelper when the Activity is created
        prefHelper = new SharedPreferencesHelper(this);

        numGrownTrees = prefHelper.retrieveInt("grown_trees_virtual");
        TextView currentVirtualTrees = findViewById(R.id.currentVirtualTrees);
        currentVirtualTrees.setText(numGrownTrees + "");

        TextView vTreesDisplay = findViewById(R.id.vTreesDisplay);
        showTrees(prefHelper, vTreesDisplay);
    }

    public void plantTree(View v) {
        //retrieve # of already grown virtual trees
        numGrownTrees = prefHelper.retrieveInt("grown_trees_virtual");

        //max. # of virtual trees
        int virtualTreesLimit = 2;

        //plant real tree if limit reached otherwise show # of virtual trees still needed
        if (numGrownTrees >= virtualTreesLimit) {
            Intent chooseProject = new Intent(GrownTrees.this, ChooseProject.class);
            startActivity(chooseProject);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            //reset # of virtual trees
            prefHelper.storeInt("grown_trees_virtual", numGrownTrees - virtualTreesLimit);
        } else {
            int virtualTreesNeeded = virtualTreesLimit - numGrownTrees;
            Toast.makeText(getApplicationContext(), "Dir fehlen noch " + virtualTreesNeeded + " Bäumchen", Toast.LENGTH_LONG).show();
        }
    }

    //show names grown virtual trees
    private void showTrees(SharedPreferencesHelper prefHelper, TextView vTreesDisplay) {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE); //currently connected wifi
        List<Tree> treesInWifi = new ArrayList<Tree>();

        //get list of trees in this wifi
        try {
            treesInWifi = MainActivity.treeInfo.treesInWifi(wifiManager.getConnectionInfo().getSSID());
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //write tree names in string
        String vTrees = "";
        for (Tree tree : treesInWifi) {
            vTrees += tree.getName() + " " + tree.getTreeType() + " " + tree.getGrowthState() + "\n";
        }

        vTreesDisplay.setText(vTrees);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
