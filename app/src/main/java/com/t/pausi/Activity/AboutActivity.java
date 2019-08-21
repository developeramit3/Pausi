package com.t.pausi.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;

import com.t.pausi.R;

public class AboutActivity extends AppCompatActivity {
    ImageView about_back;
    WebView about_webview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        //------------------------------ find view ----------------------

        about_back = findViewById(R.id.about_back);
        about_webview = findViewById(R.id.about_webview);
        about_webview.loadUrl("http://35.180.157.237/pausi/about.html");


        //--------------------------- on click -----------------------

        about_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
