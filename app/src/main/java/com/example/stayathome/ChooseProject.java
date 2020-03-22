package com.example.stayathome;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.stayathome.treedatabase.Tree;

import java.util.ArrayList;

public class ChooseProject extends AppCompatActivity {

    RecyclerView locationsRecView;
    RecyclerView.LayoutManager locationsLayout;
    RecyclerView.Adapter locationsAdapter;
    ArrayList<String> locations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //collect intent
        Intent chooseProject = getIntent();
        final Tree newVTree = chooseProject.getParcelableExtra("Tree");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_project);

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
                //NEED TO RETRIEVE USER SELECTION
                String selectedLocation = "test location";
                newVTree.setProject(selectedLocation);
                Intent confirmWifi = new Intent(ChooseProject.this, ConfirmWiFi.class);
                confirmWifi.putExtra("Tree", newVTree);
                startActivity(confirmWifi);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    public void buildRecyclerView() {
        locationsRecView = (RecyclerView) findViewById(R.id.locationsRecView);
        locationsRecView.setHasFixedSize(true);
        locationsLayout = new LinearLayoutManager(this);
        locationsAdapter = new MainAdapter(locations);

        locationsRecView.setLayoutManager(locationsLayout);
        locationsRecView.setAdapter(locationsAdapter);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
