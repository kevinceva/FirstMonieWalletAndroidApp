package com.ceva.ubmobile.core.ui.cards;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.core.ui.BaseActivity;

public class CardTopUp extends BaseActivity {
    Button btnSubmit;
    LinearLayout account_fields;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardtopup);
        setToolbarTitle("Card Top-Up");
        hideTokenField();

        account_fields = (LinearLayout) findViewById(R.id.account_fields);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTokenField();
                account_fields.setVisibility(View.GONE);
            }
        });
    }

}
