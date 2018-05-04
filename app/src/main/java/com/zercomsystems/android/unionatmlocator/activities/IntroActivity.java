package com.zercomsystems.android.unionatmlocator.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.ceva.ubmobile.R;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

import static com.zercomsystems.android.unionatmlocator.helpers.Constants.SEEN_TUTORIAL;

/**
 * Created by android on 20/06/2017.
 */

public class IntroActivity extends AppIntro {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // Note here that we DO NOT use setContentView();
        // Add your slide fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.
//        addSlide(firstFragment);
//        addSlide(secondFragment);
//        addSlide(thirdFragment);
//        addSlide(fourthFragment);

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.
        addSlide(AppIntroFragment.newInstance("ATM Locator", "fonts/GothamRounded-Medium.otf", "At a single tap you can view all ATMs nearby with directions to the chosen ATM", "fonts/GothamRounded-Medium.otf", R.drawable.union_girl_full, Color.parseColor("#00afef"), Color.parseColor("#ffffff"), Color.parseColor("#f5f7fa")));
        addSlide(AppIntroFragment.newInstance("ATM Status", "fonts/GothamRounded-Medium.otf", "Saves time and elevates stress, check if the nearest Unionbank ATM is online, offline or Not dispensing ", "fonts/GothamRounded-Medium.otf", R.drawable.union_girl_full, Color.parseColor("#00afef"), Color.parseColor("#ffffff"), Color.parseColor("#f5f7fa")));
        addSlide(AppIntroFragment.newInstance("Branch Locator", "fonts/GothamRounded-Medium.otf", "Using your current location to get address & route to the nearest Unionbank Branch/Smart Branch", "fonts/GothamRounded-Medium.otf", R.drawable.union_girl_full, Color.parseColor("#00afef"), Color.parseColor("#ffffff"), Color.parseColor("#f5f7fa")));

        // OPTIONAL METHODS
        // Override bar/separator color.
        setBarColor(Color.parseColor("#00afef"));
        setSeparatorColor(Color.parseColor("#2196F3"));

        // Hide Skip/Done button.
        showSkipButton(false);
        setProgressButtonEnabled(true);
        setSkipText("SKIP");
        setDoneText("DONE");

        //Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permission in Manifest.
        setVibrate(true);
        setVibrateIntensity(30);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // save the records
        getSharedPreferences(SplashActivityATM.class.getSimpleName(), MODE_PRIVATE).edit().putBoolean(SEEN_TUTORIAL, true).apply();
        // Do something when users tap on Done button.
        Intent intent = new Intent(this, MainActivityATM.class);
        this.startActivity(intent);
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
}