package com.example.stayathome.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

        //set height of scrollview according to screen size
        ViewGroup.LayoutParams svParams = findViewById(R.id.vTreesDisplaySV).getLayoutParams();
        svParams.height = (int) (getScreenHeight() * 0.5);


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
        selectedTreeType.setTextColor(getResources().getColor(R.color.pappelGreen));
        findViewById(R.id.vTreeSelectionScreen).setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pappel_back));
        HoldSelection.setTreeType(1);
    }

    //when user clicks on maple img
    public void clickMaple(View v) {
        selectedTreeType.setText(R.string.maple);
        selectedTreeType.setTextColor(getResources().getColor(R.color.mapleRed));
        findViewById(R.id.vTreeSelectionScreen).setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.maple_back));
        HoldSelection.setTreeType(2);
    }
    //when user clicks on cherry img
    public void clickCherry(View w) {
        selectedTreeType.setText(R.string.cherry);
        selectedTreeType.setTextColor(getResources().getColor(R.color.cherryPink));
        findViewById(R.id.vTreeSelectionScreen).setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.cherry_back));
        HoldSelection.setTreeType(3);
    }

    //screen height in pixels
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
