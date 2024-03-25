package com.example.wirwo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class MainActivity extends Activity {

    // Splash screen display duration in milliseconds
    private static final int SPLASH_DISPLAY_DURATION = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Delayed execution to navigate to LoginActivity after SPLASH_DISPLAY_DURATION milliseconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Create an Intent to start LoginActivity
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Close MainActivity to prevent going back to it when pressing back button from LoginActivity
            }
        }, SPLASH_DISPLAY_DURATION);
    }
}
