package com.ceva.ubmobile.core.ui.billers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.core.ui.AirtimeTopUp;
import com.ceva.ubmobile.core.ui.BaseActivity;

public class BillPayments extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_payments);
        setToolbarTitle("Bill Payments");
        BottomMenu();
    }

    public void onMobileRechargeClick(View v) {
        startActivity(new Intent(this, AirtimeTopUp.class));
    }

    public void onBillerManagementClick(View v) {
        startActivity(new Intent(this, AirtimeTopUp.class));
    }

    public void onPayDueBillsClick(View v) {
        startActivity(new Intent(this, Billers.class));
    }

}
