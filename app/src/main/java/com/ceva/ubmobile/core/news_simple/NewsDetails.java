package com.ceva.ubmobile.core.news_simple;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.core.ui.BaseActivity;

public class NewsDetails extends BaseActivity implements MyWebChromeClient.ProgressListener {
    String url;
    private WebView mWebView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);
        setToolbarTitle("Union News");
        url = getIntent().getExtras().getString("url");

        mWebView = findViewById(R.id.main_webview);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebView.setWebChromeClient(new MyWebChromeClient(this));
        mWebView.setWebViewClient(new WebViewClient() {

            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                view.loadUrl(url);

                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                showSmoothProgress();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                hideSmoothProgress();

            }

        });
        mWebView.loadUrl(url);

    }

    @Override
    public void onUpdateProgress(int progressValue) {
        if (progressValue == 90) {
            hideSmoothProgress();
        }
    }

}
