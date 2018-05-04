package com.ceva.ubmobile.core.ui.bankservices;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.core.ui.BaseActivity;
import com.ceva.ubmobile.core.ui.locator.RequestDSA;

public class CashMenu extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_menu);
        setToolbarTitle(getString(R.string.cash));

    }

    public void onCardlessWithdrawalsClick(View v) {
        startActivity(new Intent(this, Withdrawals.class));
    }

    public void onRequestDSA(View v) {
        startActivity(new Intent(this, RequestDSA.class));
    }

    public void onRequestLocation(View v) {
        openLocator();
    }

    public void onRequestCancel(View v) {
        Intent intent = new Intent(this, CancelToken.class);
        intent.putExtra("extra", "0");
        startActivity(intent);
    }

    public void onRequestStatus(View v) {
        Intent intent = new Intent(this, CancelToken.class);
        intent.putExtra("extra", "1");
        startActivity(intent);
    }

}
