package com.example.stayathome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //check opened for first time
        SharedPreferences firstUsage = getSharedPreferences("firstUsage", MODE_PRIVATE);
        boolean isFirstUsage = firstUsage.getBoolean("first_usage", true);


        if (isFirstUsage) {
            //first usage
            SharedPreferences.Editor firstTimeEditor = firstUsage.edit();
            firstTimeEditor.putBoolean("first_usage", false);
            firstTimeEditor.apply();

            //initial setup screen
            Intent firstTime = new Intent(MainActivity.this, InitialSetup.class);
            startActivity(firstTime);

        } else {
            //regular execution



        }
    }

}
