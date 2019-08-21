package com.t.pausi.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.t.pausi.R;

public class FallowActivity extends AppCompatActivity {

    ImageView follow_back;
    WebView follow_webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fallow);

        follow_back = findViewById(R.id.follow_back);

        follow_webview = findViewById(R.id.follow_webview);
        follow_webview.setWebViewClient(new WebViewClient());
        follow_webview.loadUrl("https://www.instagram.com/pausi.inc/");

        follow_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
