package com.example.stayathome.helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.stayathome.R;

public class SharedPreferencesHelper {

    private SharedPreferences prefs;

    public SharedPreferencesHelper(Context context){
        // Constructor
        this.prefs = context.getSharedPreferences(
                context.getResources().getString(R.string.shared_prefs), Context.MODE_PRIVATE);
    }

    public void storeString(String key, String value){
        // Stores a String value with the key passed in
        this.prefs.edit().putString(key, value).apply();
    }

    public String retrieveString(String key){
        // Returns a String value saved with the key passed in. Default value when non-existent: ""
        return prefs.getString(key, "");
    }
} // End class SharedPreferencesHelper
