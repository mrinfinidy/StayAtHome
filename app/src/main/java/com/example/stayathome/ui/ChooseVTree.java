package com.example.stayathome.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.stayathome.R;
import com.example.stayathome.treedatabase.Tree;

public class ChooseVTree extends AppCompatActivity {

    public static Activity chooseVTree;

    TextView selectedTreeType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_v_tree);

        chooseVTree = this;

        selectedTreeType = findViewById(R.id.selectedTreeType);
        HoldSelection.setTreeType(1);

        //confirm selected tree type
        Button confirmVTreeBtn = findViewById(R.id.confirmVTreeBtn);
        confirmVTreeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chooseName = new Intent(ChooseVTree.this, ChooseName.class);
                startActivity(chooseName);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    //when user clicks on pappel img
    public void clickPappel(View v) {
        selectedTreeType.setText(R.string.pappel);
        HoldSelection.setTreeType(1);
    }

    //when user clicks on maple img
    public void clickMaple(View v) {
        selectedTreeType.setText(R.string.maple);
        HoldSelection.setTreeType(2);
    }
    //when user clicks on cherry img
    public void clickCherry(View w) {
        selectedTreeType.setText(R.string.cherry);
        HoldSelection.setTreeType(3);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
