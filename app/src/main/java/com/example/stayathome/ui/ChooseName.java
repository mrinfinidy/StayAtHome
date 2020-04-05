package com.example.stayathome.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.stayathome.R;
import com.example.stayathome.helper.SharedPreferencesHelper;
import com.example.stayathome.treedatabase.Tree;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class ChooseName extends AppCompatActivity {

    public static Activity chooseName;

    private SharedPreferencesHelper prefHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_name);

        chooseName = this;

    }

    public void confirmName(View v) throws ExecutionException, InterruptedException {
        EditText vTreeName = findViewById(R.id.vTreeName);
        String name = vTreeName.getText().toString();

        if (isDuplicate(name)) {
            Toast.makeText(getApplicationContext(), "There is already another tree with this name", Toast.LENGTH_LONG).show();
        } else {
            HoldSelection.setTreeName(name);
            Intent createTree = new Intent(ChooseName.this, MainActivity.class);
            startActivity(createTree);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    private boolean isDuplicate(String name) throws ExecutionException, InterruptedException {
        List<Tree> currentTrees =  MainActivity.treeInfo.treesInWifi(HoldSelection.getWifiName());
        for (int i = 0; i < currentTrees.size(); i++) {
            if (currentTrees.get(i).getName().equals(name)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
