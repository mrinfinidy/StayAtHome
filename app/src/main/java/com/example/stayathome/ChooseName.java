package com.example.stayathome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.stayathome.interfacelogic.TreeManager;
import com.example.stayathome.treedatabase.Tree;

public class ChooseName extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent chooseName = getIntent();
        final Tree newVTree = chooseName.getParcelableExtra("Tree");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_name);

        Button confirmNameBtn = findViewById(R.id.confirmNameBtn);
        confirmNameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText vTreeName = findViewById(R.id.vTreeName);
                newVTree.setName(vTreeName.getText().toString());
                Intent createTree = new Intent(ChooseName.this, MainActivity.class);
                createTree.putExtra("Tree", newVTree);
                startActivity(createTree);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }
}
