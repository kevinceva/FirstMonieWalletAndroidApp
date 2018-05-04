package com.ceva.ubmobile.core.omniview;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.core.ui.BaseActivity;

public class InsuranceRequest extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insurancerequest);
        setToolbarTitle("Bancassurance");
    }

    public void goNext(View v) {
        startActivity(new Intent(this, OpenAccountValidation.class));
    }

}
