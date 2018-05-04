package com.ceva.ubmobile.core.signon;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.core.ui.BaseActivity;
import com.ceva.ubmobile.core.ui.LandingPage;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TermsAndConditions extends BaseActivity {

    @BindView(R.id.btnDecline)
    Button btnDecline;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_termsconditions);
        ButterKnife.bind(this);
        setToolbarTitle("Terms & Conditions");
        btnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TermsAndConditions.this, LandingPage.class));
                finish();
            }
        });
    }

    public void onContinueClick(View v) {
        startActivity(new Intent(this, StepTwo.class));
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, LandingPage.class));
    }
}
