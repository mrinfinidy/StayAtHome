package com.example.stayathome.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.stayathome.R;
import com.example.stayathome.treedatabase.Tree;

public class ChooseName extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_name);

        Button confirmNameBtn = findViewById(R.id.confirmNameBtn);
        confirmNameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText vTreeName = findViewById(R.id.vTreeName);
                String name = vTreeName.getText().toString();
                HoldSelection.setTreeName(name);
                Intent createTree = new Intent(ChooseName.this, MainActivity.class);
                startActivity(createTree);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
