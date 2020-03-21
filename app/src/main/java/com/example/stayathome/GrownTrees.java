package com.example.stayathome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class GrownTrees extends AppCompatActivity {

    int numGrownTrees;
    SharedPreferences grownTreesVirtual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grown_trees);

        grownTreesVirtual = getSharedPreferences("grownTreesVirtual", MODE_PRIVATE);
        numGrownTrees = grownTreesVirtual.getInt("grown_trees_virtual", 0);
        TextView currentVirtualTrees = (TextView) findViewById(R.id.currentVirtualTrees);
        currentVirtualTrees.setText(numGrownTrees + "");
    }

    public void plantTree(View v) {
        //retrieve # of already grown virtual trees
        grownTreesVirtual = getSharedPreferences("grownTreesVirtual", MODE_PRIVATE);
        numGrownTrees = grownTreesVirtual.getInt("grown_trees_virtual", 0);

        //max. # of virtual trees
        int virtualTreesLimit = 14;

        //plant real tree if limit reached otherwise show # of virtual trees still needed
        if (numGrownTrees == virtualTreesLimit) {
            Toast.makeText(getApplicationContext(), "Baum gepflanzt", Toast.LENGTH_LONG).show();
            //reset # of virtual trees
            SharedPreferences.Editor resetTrees = grownTreesVirtual.edit();
            resetTrees.putInt("grown_trees_virtual", 0);
            resetTrees.apply();
            finish();
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
