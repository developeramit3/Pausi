package com.t.pausi.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.t.pausi.Bean.MyLanguageSession;
import com.t.pausi.Bean.SliderBean;
import com.t.pausi.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import me.relex.circleindicator.CircleIndicator;

public class WelcomeSliderActivity extends AppCompatActivity {
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    RelativeLayout get_starrted_lay;
    int counter;
    ViewPager VP_banner_slidder;
    private static int currentPage = 0;
    CircleIndicator CI_indicator;
    private static int NUM_PAGES = 0;
    List<SliderBean> slist;
    MyLanguageSession myLanguageSession;
    private String language = "";
    Dialog dialog;
    LinearLayout language_linear_lay;
    RelativeLayout english, french;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLanguageSession = new MyLanguageSession(this);
        language = myLanguageSession.getLanguage();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());
        setContentView(R.layout.activity_welcome_slider);
        slist = new ArrayList<>();

        VP_banner_slidder = (ViewPager) findViewById(R.id.vp_viewpager);
        CI_indicator = (CircleIndicator) findViewById(R.id.ci_indicator);
        get_starrted_lay = findViewById(R.id.get_starrted_lay);
        language_linear_lay = findViewById(R.id.language_linear_lay);
        english = findViewById(R.id.english_layout);
        french = findViewById(R.id.french_layout);

        if (SettingActivity.logout != null) {
            SettingActivity.logout = "";
        }

        // add few more layouts if you want

        get_starrted_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                language_linear_lay.setVisibility(View.VISIBLE);
            }
        });

        english.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myLanguageSession.insertLanguage("en");
                myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());
                String oldLanguage = language;
                language = myLanguageSession.getLanguage();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                language_linear_lay.setVisibility(View.GONE);

            }
        });

        french.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myLanguageSession.insertLanguage("fr");
                myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());
                String oldLanguage = language;
                language = myLanguageSession.getLanguage();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                language_linear_lay.setVisibility(View.GONE);
            }
        });

        SliderBean sliderBean = new SliderBean();
        sliderBean.setImage(R.drawable.p1);
        sliderBean.setTittle(getString(R.string.weltext));
        slist.add(sliderBean);

        SliderBean sliderBean1 = new SliderBean();
        sliderBean1.setImage(R.drawable.p2);
        sliderBean1.setTittle(getString(R.string.weltext2));
        slist.add(sliderBean1);

        SliderBean sliderBean2 = new SliderBean();
        sliderBean2.setImage(R.drawable.p3);
        sliderBean2.setTittle(getString(R.string.weltext3));
        slist.add(sliderBean2);

        SliderBean sliderBean3 = new SliderBean();
        sliderBean3.setImage(R.drawable.p4);
        sliderBean3.setTittle(getString(R.string.weltext4));
        slist.add(sliderBean3);

        SliderBean sliderBean4 = new SliderBean();
        sliderBean4.setImage(R.drawable.p5);
        sliderBean4.setTittle(getString(R.string.weltext5));
        slist.add(sliderBean4);


        CustomPagerAdapter customPagerAdapter = new CustomPagerAdapter(WelcomeSliderActivity.this, slist);
        VP_banner_slidder.setAdapter(customPagerAdapter);
        CI_indicator.setViewPager(VP_banner_slidder);
        NUM_PAGES = slist.size();
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                VP_banner_slidder.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 2000, 2000);

        CI_indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                currentPage = position;
                Log.e("Curent Page :", "" + currentPage);
//                Toast.makeText(MainBuyerActivity.this, "Curent Page" + "" + currentPage, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int pos) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        String oldLanguage = language;
        language = myLanguageSession.getLanguage();
        if (!oldLanguage.equals(language)) {
            finish();
            startActivity(getIntent());
        }
    }

    public class CustomPagerAdapter extends PagerAdapter {

        // private ArrayList<Integer> IMAGES;
        public List<SliderBean> sliderBeanList;
        private LayoutInflater inflater;
        private Context context;


        public CustomPagerAdapter(Context context, List<SliderBean> sliderBeanList) {
            this.context = context;
            this.sliderBeanList = sliderBeanList;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return sliderBeanList.size();
        }

        @Override
        public Object instantiateItem(ViewGroup view, final int position) {
            View imageLayout = inflater.inflate(R.layout.welcome_one_layout, view, false);

            assert imageLayout != null;
            final ImageView imageView = (ImageView) imageLayout.findViewById(R.id.slider_imageview);
            final TextView slider_text = (TextView) imageLayout.findViewById(R.id.slider_text);


            Glide.with(context).load(sliderBeanList.get(position).getImage()).into(imageView);
            slider_text.setText(sliderBeanList.get(position).getTittle());
            view.addView(imageLayout, 0);


            return imageLayout;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }


    }

}
