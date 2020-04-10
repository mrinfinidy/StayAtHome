package com.example.stayathome.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stayathome.R;
import com.example.stayathome.treedatabase.Tree;
import com.example.stayathome.treedatabase.TreeInfo;
import com.example.stayathome.treedatabase.TreeViewModel;
import java.util.concurrent.ExecutionException;

public class GrownTrees extends AppCompatActivity {

    private TextView vTreesDisplay;
    private int numGrownTrees;

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.activity_grown_trees);

        //calculate # plantable trees in Wifi
        numGrownTrees = TreeInfo.getPlantableTrees().size();

        //display # plantable trees in Wifi
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
            TreeViewModel treeViewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(TreeViewModel.class);
            for (Tree tree : TreeInfo.getPlantableTrees()) {
                tree.setPlantable(false);
                treeViewModel.update(tree);
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
        for (Tree tree : TreeInfo.getTreesInWifi()) {
            vTrees += tree.getName() + " " + tree.getTreeType() + " " + tree.getGrowthState() + " " + tree.isPlantable() + "\n";
        }

        vTreesDisplay.setText(vTrees);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
