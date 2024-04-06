package com.allstar.wirwo;

import android.app.Activity;
import android.os.Bundle;


public class faqs extends Activity {

    private PopupWindowHelper popupMenuHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.faqs);

        // Initialize PopupMenuHelper with context of your activity
        popupMenuHelper = new PopupWindowHelper(this);

        findViewById(R.id.toolbar_navigation_icon).setOnClickListener(v -> {
            // Call showPopup() method to show the popup
            popupMenuHelper.showPopup(v);
        });
    }

}
