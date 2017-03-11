package com.riddhikakadia.brunchy2.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import com.riddhikakadia.brunchy2.R;

import static com.riddhikakadia.brunchy2.util.Constants.WEBVIEW_LINK;

public class WebViewActivity extends AppCompatActivity {

    WebView recipe_webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        recipe_webview = (WebView) findViewById(R.id.recipe_webview);

        Intent intent = getIntent();
        if (intent != null) {
            String weblink = getIntent().getStringExtra(WEBVIEW_LINK);
            recipe_webview.loadUrl(weblink);
        }
    }
}
