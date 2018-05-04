package com.ceva.ubmobile.core.lifestyle.flights;

import android.os.Bundle;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.core.ui.BaseActivity;

public class FlightConfirm extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_confirm);
        setToolbarTitle("Confirm");

    }
}
