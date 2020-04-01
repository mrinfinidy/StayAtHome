package com.example.stayathome.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stayathome.R;
import com.example.stayathome.helper.SharedPreferencesHelper;

public class GrownTrees extends AppCompatActivity {

    int numGrownTrees;
    SharedPreferencesHelper prefHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grown_trees);

        // Assign SharedPreferencesHelper when the Activity is created
        prefHelper = new SharedPreferencesHelper(this);

        numGrownTrees = prefHelper.retrieveInt("grown_trees_virtual");
        TextView currentVirtualTrees = findViewById(R.id.currentVirtualTrees);
        currentVirtualTrees.setText(numGrownTrees + "");
    }

    public void plantTree(View v) {
        //retrieve # of already grown virtual trees
        numGrownTrees = prefHelper.retrieveInt("grown_trees_virtual");

        //max. # of virtual trees
        int virtualTreesLimit = 14;

        //plant real tree if limit reached otherwise show # of virtual trees still needed
        if (numGrownTrees == virtualTreesLimit) {
            Intent chooseProject = new Intent(GrownTrees.this, ConfirmWiFi.class);
            startActivity(chooseProject);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            //Toast.makeText(getApplicationContext(), "Baum gepflanzt", Toast.LENGTH_LONG).show();
            //reset # of virtual trees
            prefHelper.storeInt("grown_trees_virtual", 0);
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
