package com.ceva.ubmobile.core.lifestyle;

import android.os.Bundle;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.core.ui.BaseActivity;

public class MyDonations extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mydonations);
        setToolbarTitle("My Donations");
    }
}
