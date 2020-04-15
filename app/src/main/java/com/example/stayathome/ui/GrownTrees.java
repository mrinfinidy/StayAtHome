package com.example.stayathome.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stayathome.GrownTreesAdapter;
import com.example.stayathome.R;
import com.example.stayathome.TreeListItem;
import com.example.stayathome.treedatabase.Tree;
import com.example.stayathome.treedatabase.TreeViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class GrownTrees extends AppCompatActivity {

    private RecyclerView treesRecView;
    private RecyclerView.LayoutManager treeListLayout;
    private RecyclerView.Adapter grownTreesAdapter;

    private ArrayList<TreeListItem> treeListItems;
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

        treeListItems = new ArrayList<>();

        buildRecyclerView();

        plantableTrees = new ArrayList<>();
        numGrownTrees = 0;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.activity_grown_trees);

        resumeDisplay();
    }

    private void resumeDisplay () {
        //display # plantable trees
        TextView plantableTreesDisplay = findViewById(R.id.currentVirtualTrees);
        plantableTreesDisplay.setText(numGrownTrees + " fully grown trees");
    }

    //update number of plantable trees and displayed current trees
    void updateTreeInfos(List<Tree> trees) {


        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            for (Tree tree : trees) {
                if (tree.getWifi().equals(wifiManager.getConnectionInfo().getSSID())) {
                    treeListItems.add(formatTreeItem(tree));
                    if (tree.isPlantable() && !plantableTrees.contains(tree)) {
                        plantableTrees.add(tree);
                        numGrownTrees++;
                    }
                }
            }
        }

        //display # plantable trees
        TextView plantableTreesDisplay = findViewById(R.id.currentVirtualTrees);
        plantableTreesDisplay.setText(numGrownTrees + " fully grown trees");
    }

    //format tree to add in recycle view
    private TreeListItem formatTreeItem(Tree tree) {
        //get image of correct tree type
        int treeImg = 0;
        if (tree.getTreeType() == 1) {
            treeImg = R.drawable.ic_pappel5;
        } else if (tree.getTreeType() == 2) {
            //treeImg = mapleImg;
        } else if (tree.getTreeType() == 3) {
            //treeImg = cherryImg;
        }

        //get tree name and plantability
        String treeName = tree.getName();
        if (tree.isPlantable()) {
            treeName += " (" + R.string.plantable + ")";
        }

        return new TreeListItem(treeImg, treeName);
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


    public void buildRecyclerView() {
        treesRecView = findViewById(R.id.treesList);
        ViewGroup.LayoutParams rvParams = treesRecView.getLayoutParams();
        rvParams.height = (int) (getScreenHeight() * 0.4);
        treesRecView.setPadding(10,10,10,10);
        treesRecView.setHasFixedSize(true);
        treeListLayout = new LinearLayoutManager(this);
        grownTreesAdapter = new GrownTreesAdapter(treeListItems);

        treesRecView.setLayoutManager(treeListLayout);
        treesRecView.setAdapter(grownTreesAdapter);
    }

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
