package com.t.pausi.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;

import com.t.pausi.Bean.MyLanguageSession;
import com.t.pausi.R;

public class HelpActivity extends AppCompatActivity {
    ImageView help_back;
    WebView help_webview;
    MyLanguageSession myLanguageSession;
    private String language = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLanguageSession = new MyLanguageSession(this);
        language = myLanguageSession.getLanguage();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());
        setContentView(R.layout.activity_help);

        //------------------------------ find view ----------------------

        help_back = findViewById(R.id.help_back);
        help_webview = findViewById(R.id.help_webview);
        help_webview.loadUrl("http://35.180.157.237/pausi/help.html");


        //--------------------------- on click -----------------------

        help_back.setOnClickListener(new View.OnClickListener() {
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
