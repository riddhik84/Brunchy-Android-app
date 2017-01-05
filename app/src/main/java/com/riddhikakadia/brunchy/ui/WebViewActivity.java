package com.riddhikakadia.brunchy.ui;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import com.riddhikakadia.brunchy.R;

public class WebViewActivity extends AppCompatActivity {

    WebView recipe_webview;
    final String WEBVIEW_LINK = "WEBVIEW_LINK";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        recipe_webview = (WebView) findViewById(R.id.recipe_webview);

        Intent intent = getIntent();
        if (intent != null) {
            String weblink = getIntent().getStringExtra(WEBVIEW_LINK).toString();
            recipe_webview.loadUrl(weblink);
        }
    }
}
