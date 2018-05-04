package com.ceva.ubmobile.core.ui.socialmedia;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.core.ui.BaseActivity;

public class LinkSocialMedia extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_social_media);
        setToolbarTitle("Link Social Media");
    }

    public void onTwitterClick(View view) {
        Intent intent = new Intent(this, SocialLink.class);
        intent.putExtra(Constants.KEY_ACTIVITY_NAME, "Twitter");
        intent.putExtra(Constants.AZURE_LINK, "https://twitter.com");
        startActivity(intent);
    }

    public void onFacebookClick(View view) {
        Intent intent = new Intent(this, SocialLink.class);
        intent.putExtra(Constants.KEY_ACTIVITY_NAME, "Facebook");
        intent.putExtra(Constants.AZURE_LINK, "https://m.facebook.com");
        startActivity(intent);
    }

    public void onGoogleClick(View view) {
        Intent intent = new Intent(this, SocialLink.class);
        intent.putExtra(Constants.KEY_ACTIVITY_NAME, "Google");
        intent.putExtra(Constants.AZURE_LINK, "https://plus.google.com");
        startActivity(intent);
    }

    public void onLinkedInClick(View view) {
        Intent intent = new Intent(this, SocialLink.class);
        intent.putExtra(Constants.KEY_ACTIVITY_NAME, "LinkedIn");
        intent.putExtra(Constants.AZURE_LINK, "https://linkedin.com");
        startActivity(intent);
    }

    public void onWindowsClick(View view) {
        Intent intent = new Intent(this, SocialLink.class);
        intent.putExtra(Constants.KEY_ACTIVITY_NAME, "Microsoft");
        intent.putExtra(Constants.AZURE_LINK, "https://login.live.com/");
        startActivity(intent);
    }
}
