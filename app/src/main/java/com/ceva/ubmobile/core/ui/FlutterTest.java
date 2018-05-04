package com.ceva.ubmobile.core.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ceva.ubmobile.R;
import com.flutterwave.raveandroid.RaveConstants;
import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.RavePayManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FlutterTest extends BaseActivity {
    @BindView(R.id.btnTest)
    Button btnTest;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flutter_test);
        setToolbarTitle("Flutter Test");
        ButterKnife.bind(this);

        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new RavePayManager(FlutterTest.this).setAmount(1000)
                        .setCountry("NG")
                        .setCurrency("NGN")
                        .setEmail("bktowett@gmail.com")
                        .setfName("Brian")
                        .setlName("Towett")
                        .setNarration("test")
                        .setPublicKey("FLWPUBK-bdf165d74458f757a4e88ae713b6f4bc-X")
                        .setSecretKey("FLWSECK-eb68d3e50870c3bcbaa0e292e9da6fc7-X")
                        .setTxRef("rtru")
                        .acceptAccountPayments(true)
                        .acceptCardPayments(true)
                        .onStagingEnv(true)
                        .initialize();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RaveConstants.RAVE_REQUEST_CODE && data != null) {
            String message = data.getStringExtra("response");
            if (resultCode == RavePayActivity.RESULT_SUCCESS) {
                Toast.makeText(this, "SUCCESS " + message, Toast.LENGTH_SHORT).show();
            } else if (resultCode == RavePayActivity.RESULT_ERROR) {
                Toast.makeText(this, "ERROR " + message, Toast.LENGTH_SHORT).show();
            } else if (resultCode == RavePayActivity.RESULT_CANCELLED) {
                Toast.makeText(this, "CANCELLED " + message, Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
