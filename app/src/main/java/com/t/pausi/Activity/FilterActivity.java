package com.t.pausi.Activity;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.t.pausi.Bean.MyLanguageSession;
import com.t.pausi.Fragment.ForRentFragment;
import com.t.pausi.Fragment.ForSaleFragment;
import com.t.pausi.Fragment.HomeFragment;
import com.t.pausi.R;

public class FilterActivity extends AppCompatActivity {

    RelativeLayout for_sale_lay, for_rent_lay;
    TextView for_sale_text, for_rent_text;
    Fragment fragment;
    ImageView filter_back;
    private static FragmentManager fm;
    MyLanguageSession myLanguageSession;
    private String language = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLanguageSession = new MyLanguageSession(this);
        language = myLanguageSession.getLanguage();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());
        setContentView(R.layout.activity_filter);

        for_sale_lay = findViewById(R.id.for_sale_lay);
        for_rent_lay = findViewById(R.id.for_rent_lay);
        for_sale_text = findViewById(R.id.for_sale_text);
        for_rent_text = findViewById(R.id.for_rent_text);
        filter_back = findViewById(R.id.filter_back);


        for_sale_lay.setBackgroundColor(Color.parseColor("#F26C4F"));
        for_sale_text.setTextColor(Color.parseColor("#FFFFFF"));

        fm = getSupportFragmentManager();
        addFragment(new ForSaleFragment(), false);

        for_sale_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for_sale_lay.setBackgroundColor(Color.parseColor("#F26C4F"));
                for_sale_text.setTextColor(Color.parseColor("#FFFFFF"));
                for_rent_lay.setBackgroundColor(Color.parseColor("#a6a9ac"));
                for_rent_text.setTextColor(Color.parseColor("#000000"));
                addFragment(new ForSaleFragment(), false);
            }
        });

        for_rent_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for_rent_lay.setBackgroundColor(Color.parseColor("#F26C4F"));
                for_rent_text.setTextColor(Color.parseColor("#FFFFFF"));
                for_sale_lay.setBackgroundColor(Color.parseColor("#a6a9ac"));
                for_sale_text.setTextColor(Color.parseColor("#000000"));
                addFragment(new ForRentFragment(), false);
            }
        });

        filter_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public static void addFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.frame_container2, fragment, "");
        //if (!tag.equals("Home"))
        if (addToBackStack)
            transaction.addToBackStack(null);
        transaction.commit();
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
