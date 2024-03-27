package com.example.wirwo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Dashboard extends Activity {

    private FirebaseAuth auth;
    private PopupWindowHelper popupMenuHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_main);

        // Initialize FirebaseAuth instance
        auth = FirebaseAuth.getInstance();

        // Get reference to TextView
        TextView welcomeText = findViewById(R.id.welcome_text);

        // Get current user
        FirebaseUser currentUser = auth.getCurrentUser();

        // Check if user is not null
        if (currentUser != null) {
            // Retrieve the display name of the current user
            String currentUserName = currentUser.getDisplayName();

            // Apply the retrieved display name to the TextView
            String text = "Ciao, " + currentUserName + ", Check your Wireless Worms Today!";
            welcomeText.setText(text);
        }

        // Initialize PopupMenuHelper with context of your activity
        popupMenuHelper = new PopupWindowHelper(this);

        SwitchMaterial waterPumpSwitch = findViewById(R.id.waterPumpSwitch);
        SwitchMaterial ventiSwitch = findViewById(R.id.ventiSwitch);

        waterPumpSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Water pump switch is turned on
                Toast.makeText(Dashboard.this, "Water Pump is turned on", Toast.LENGTH_SHORT).show();
            } else {
                // Water pump switch is turned off
                Toast.makeText(Dashboard.this, "Water Pump is turned off", Toast.LENGTH_SHORT).show();
            }
        });

        // Set OnCheckedChangeListener for the ventilation switch
        ventiSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Ventilation switch is turned on
                Toast.makeText(Dashboard.this, "Ventilation is turned on", Toast.LENGTH_SHORT).show();
            } else {
                // Ventilation switch is turned off
                Toast.makeText(Dashboard.this, "Ventilation is turned off", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.toolbar_navigation_icon).setOnClickListener(v -> {
            // Call showPopup() method to show the popup
            popupMenuHelper.showPopup(v);
        });
    }
}
