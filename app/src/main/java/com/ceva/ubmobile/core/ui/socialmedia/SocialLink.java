package com.ceva.ubmobile.core.ui.socialmedia;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.core.ui.BaseActivity;

public class SocialLink extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_link);

        Bundle bundle = getIntent().getExtras();
        String title = bundle.getString(Constants.KEY_ACTIVITY_NAME);
        String link = "";
        if (title == null) {
            onBackPressed();
        } else {
            setToolbarTitle(title);
            link = bundle.getString(Constants.AZURE_LINK);
            WebView wv1 = findViewById(R.id.webview);
            wv1.setWebViewClient(new MyBrowser());
            wv1.getSettings().setLoadsImagesAutomatically(true);
            wv1.getSettings().setJavaScriptEnabled(true);
            wv1.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

            wv1.loadUrl(link);
        }

    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
