package com.zercomsystems.android.unionatmlocator.activities;

import android.content.Intent;
import android.os.Bundle;


import com.ceva.ubmobile.R;

import static com.zercomsystems.android.unionatmlocator.helpers.Constants.SEEN_TUTORIAL;

/**
 * Created by oreofe on 7/8/2016.
 */
public class SplashActivityATM extends ATMBaseActivity {

    @Override
    public int getLayoutId() {
        return R.layout.activity_splash_locator;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setStatusBarTranslucent(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        boolean hasSeen = getSharedPreferences(SplashActivityATM.class.getSimpleName(), MODE_PRIVATE)
                .getBoolean(SEEN_TUTORIAL, false);

        Intent intent = null;
        if (!hasSeen) {
            intent = new Intent(SplashActivityATM.this, IntroActivity.class);

        } else {
            intent = new Intent(SplashActivityATM.this, MainActivityATM.class);

        }

        SplashActivityATM.this.startActivity(intent);
        finish();

    }
}
