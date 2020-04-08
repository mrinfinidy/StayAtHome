package com.example.stayathome.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.stayathome.MainAdapter;
import com.example.stayathome.R;
import com.example.stayathome.helper.SharedPreferencesHelper;
import com.example.stayathome.treedatabase.Tree;

import java.util.ArrayList;

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
                    //TO DO: send selection to server
                    //update # grown virtual trees
                    Toast.makeText(getApplicationContext(), "Tree planted in " + selectedLocation, Toast.LENGTH_LONG).show();
                    Intent confirmProject = new Intent(ChooseProject.this, GrownTrees.class);
                    startActivity(confirmProject);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }
        });
    }

    //locations are displayed here
    public void buildRecyclerView() {
        locationsRecView = (RecyclerView) findViewById(R.id.locationsRecView);
        locationsRecView.setHasFixedSize(true);
        locationsLayout = new LinearLayoutManager(this);
        locationsAdapter = new MainAdapter(locations); //use strings stored in locations

        locationsRecView.setLayoutManager(locationsLayout);
        locationsRecView.setAdapter(locationsAdapter);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
