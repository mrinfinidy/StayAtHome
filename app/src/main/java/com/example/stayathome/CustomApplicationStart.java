package com.example.stayathome;

import android.app.Application;
import com.jakewharton.threetenabp.AndroidThreeTen;

public class CustomApplicationStart extends Application {
    @Override
    public void onCreate(){
        super.onCreate();

        // Initialize the ThreeTen Android Backport
        AndroidThreeTen.init(this);
    }
} // End class CustomApplicationStart
