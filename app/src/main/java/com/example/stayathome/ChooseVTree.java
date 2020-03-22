package com.example.stayathome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.stayathome.treedatabase.Tree;

public class ChooseVTree extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent chooseVTree = getIntent();
        final Tree newVTree = chooseVTree.getParcelableExtra("Tree");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_v_tree);

        Button confirmVTreeBtn = findViewById(R.id.confirmVTreeBtn);
        confirmVTreeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //NEED TO RETRIEVE USER SELECTION
                Intent chooseName = new Intent(ChooseVTree.this, ChooseName.class);
                chooseName.putExtra("Tree", newVTree);
                startActivity(chooseName);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }
        });
    }
}
