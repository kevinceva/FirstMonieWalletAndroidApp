package com.ceva.ubmobile.core.lifestyle.doctors;

import android.os.Bundle;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.core.ui.BaseActivity;

public class DoctorConfirm extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_confirm);
        setToolbarTitle("Confirm");
    }
}
