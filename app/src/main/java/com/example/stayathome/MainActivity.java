package com.example.stayathome;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.stayathome.helper.SharedPreferencesHelper;

/*ALL SHARED PREFERENCES KEYS
key: first_usage --> boolean to check if it is the first time that the app is launched

key: current_growth --> state of virtual tree currently growing

key: grown_trees_virtual --> number of already grown virtual trees

key: wifi_name --> SSID (network name)

key: wifi_id --> BSSID (MAC address)

key: challenge_duration --> planned duration of the current challenge in seconds (as long, in seconds)

key: challenge_start_time --> time when the current challenge has been started (as long)

key: actual_time_in_challenge --> time that has already passed in the challenge (as long, in seconds)
*/

public class MainActivity extends AppCompatActivity {

    SharedPreferencesHelper prefHelper;

    private BroadcastReceiver minuteUpdateReceiver;
    private int countMinutes;
    private int virtualTreeState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Assign SharedPreferences when the Activity is created
        prefHelper = new SharedPreferencesHelper(this);

        //check opened for first time
        boolean isFirstUsage = prefHelper.retrieveBoolean("first_usage");

        if (isFirstUsage) {
            //first usage
            prefHelper.storeBoolean("first_usage", false);

            //initial setup screen
            Intent firstTime = new Intent(MainActivity.this, InitialSetup.class);
            startActivity(firstTime);
        }
        //regular execution
        //show state of currently growing tree

        positionEntities();
    }

    // Moves the pot and labels etc. according to the background image position
    private void positionEntities() {
        // It is only necessary to adjust the height, since with is always adjusted
        // according to ImageView width
        ImageView background = findViewById(R.id.bgImageView);
        ImageView pot = findViewById(R.id.potImageView);

        float[] f = new float[9];
        background.getImageMatrix().getValues(f);
        final float scaleY = f[Matrix.MSCALE_Y];

        Drawable bgDrawable = background.getDrawable();
        final float bgHeight = bgDrawable.getIntrinsicHeight();
        final float actualHeight = scaleY * bgHeight;

        final float top = (bgHeight - actualHeight) / 2;
        final float newY = 950f*scaleY + top;
        pot.setY(newY);

        TextView virtualTreeGrowth = findViewById(R.id.virtualTreeGrowth);
        final float newYLabel = 1460f*scaleY + top;
        virtualTreeGrowth.setY(newYLabel);
    }

    //all virtual trees already grown
    public void showGrownTrees(View v) {
        Intent showTrees = new Intent(MainActivity.this, GrownTrees.class);
        startActivity(showTrees);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    //while app is running update tree growth --> call updateTree()
    public void startMinuteUpdater() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        minuteUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                countMinutes++;
                if (countMinutes >= 30) {
                    updateTree();
                }
            }
        };
        registerReceiver(minuteUpdateReceiver, intentFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startMinuteUpdater();
    }

    @Override
    protected void onPause(){
        super.onPause();
        unregisterReceiver(minuteUpdateReceiver);
    }

    //update Tree currently on screen
    public void updateTree() {
        virtualTreeState = prefHelper.retrieveInt("current_growth");
        TextView virtualTreeGrowth = findViewById(R.id.virtualTreeGrowth);
        virtualTreeGrowth.setText(virtualTreeState + "");
    }
}
