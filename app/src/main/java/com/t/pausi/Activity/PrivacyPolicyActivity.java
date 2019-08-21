package com.t.pausi.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;

import com.t.pausi.Bean.MyLanguageSession;
import com.t.pausi.R;

public class PrivacyPolicyActivity extends AppCompatActivity {
    ImageView privacy_policy_back;
    WebView privacy_webview;
    MyLanguageSession myLanguageSession;
    private String language = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLanguageSession = new MyLanguageSession(this);
        language = myLanguageSession.getLanguage();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());
        setContentView(R.layout.activity_privacy_policy);

        //------------------------------ find view ----------------------

        privacy_policy_back = findViewById(R.id.privacy_policy_back);
        privacy_webview = findViewById(R.id.privacy_webview);
        privacy_webview.loadUrl("http://35.180.157.237/pausi/privacy_policy.html");


        //--------------------------- on click -----------------------

        privacy_policy_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());
        String oldLanguage = language;
        language = myLanguageSession.getLanguage();
        if (!oldLanguage.equals(language)) {
            finish();
            startActivity(getIntent());
        }
    }
}
