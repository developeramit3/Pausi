package com.t.pausi.Activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.t.pausi.Bean.AlertConnection;
import com.t.pausi.Bean.BottomNavigationViewHelper;
import com.t.pausi.Bean.ConnectionDetector;
import com.t.pausi.Bean.MyLanguageSession;
import com.t.pausi.Bean.MySharedPref;
import com.t.pausi.Fragment.HomeFragment;
import com.t.pausi.Fragment.MeFragment;
import com.t.pausi.Fragment.MessageFragment;
import com.t.pausi.Fragment.NewsFragment;
import com.t.pausi.Interface.Config;
import com.t.pausi.Interface.UserInterface;
import com.t.pausi.Pojo.PropertyImage;
import com.t.pausi.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeActivity extends AppCompatActivity {
    public static BottomNavigationView navigation;
    private static FragmentManager fm;
    public static String ptype, to_beds, from_beds, to_baths, from_baths, to_price, from_price, fstatus,
            fsort, fdate, ftype, fkey, sale_type, p_amenities = "";
    public static List<PropertyImage> plist;
    public static List<String> p_e_list;
    String ldata, logid = "", time_zone, current_time;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    ProgressDialog progressDialog;
    MySharedPref sp;
    MyLanguageSession myLanguageSession;
    private String language = "";
    InterstitialAd mInterstitialAd;
    public static String updateback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLanguageSession = new MyLanguageSession(this);
        language = myLanguageSession.getLanguage();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());

        setContentView(R.layout.activity_home);

        //--------------------- connection detector -----------------------------------

        cd = new ConnectionDetector(this);
        isInternetPresent = cd.isConnectingToInternet();
        super.onStart();

        //-------------------------------- get login id -----------------------------------

        sp = new MySharedPref();
        ldata = sp.getData(getApplicationContext(), "ldata", "null");
        if (ldata != null || !ldata.equals("")) {
            try {
                JSONObject jsonObject = new JSONObject(ldata);
                logid = jsonObject.getString("id");
                Log.e("logid ", " " + logid);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //----------------------------- GOOGLE ADS ------------------------------

        mInterstitialAd = new InterstitialAd(this);
        // set the ad unit ID
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen));
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        // Load ads into Interstitial Ads
        mInterstitialAd.loadAd(adRequest);
        mInterstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {
                showInterstitial();
            }
        });

        //--------------------------------- get timezone and current time -----------------

        Calendar c = Calendar.getInstance();
        TimeZone tz = c.getTimeZone();
        Log.e("TIME ZONE >>", tz.getDisplayName());
        Log.e("TIME ZONE ID>>", tz.getID());
        time_zone = tz.getID();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        current_time = simpleDateFormat.format(new Date());


        //------------------------- bottom navigation code ------------------------------

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.removeShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        fm = getSupportFragmentManager();

        if (SettingActivity.logout != null && SettingActivity.logout.equalsIgnoreCase("yes")) {
            finish();
            startActivity(new Intent(getApplicationContext(), WelcomeSliderActivity.class));

        } else {

            if (updateback != null && updateback.equalsIgnoreCase("news")) {

                addFragment(new NewsFragment(), false);
                navigation.setSelectedItemId(R.id.news_buttom_nav);
            } else {

                addFragment(new HomeFragment(), false);
                navigation.setSelectedItemId(R.id.home_buttom_nav);
            }
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            ;
            switch (item.getItemId()) {
                case R.id.news_buttom_nav:
                    addFragment(new NewsFragment(), false);
                    return true;
                case R.id.message_buttom_nav:

                    if (ldata == null || ldata.equalsIgnoreCase("") || ldata.equalsIgnoreCase("null")) {

                        final Dialog dialog = new Dialog(HomeActivity.this);
                        dialog.setContentView(R.layout.login_selection_dialog);
                        dialog.setCancelable(false);
                        Button cancel = dialog.findViewById(R.id.login_selection_cancel_btn);
                        Button ok = dialog.findViewById(R.id.login_selection_ok_btn);

                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });

                        ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                finish();
                            }
                        });

                        dialog.show();

                    } else {
                        addFragment(new MessageFragment(), false);
                    }
                    return true;
                case R.id.home_buttom_nav:
                    addFragment(new HomeFragment(), false);
                    return true;
                case R.id.me_buttom_nav:

                    if (ldata == null || ldata.equalsIgnoreCase("") || ldata.equalsIgnoreCase("null")) {

                        final Dialog dialog = new Dialog(HomeActivity.this);
                        dialog.setContentView(R.layout.login_selection_dialog);
                        dialog.setCancelable(false);
                        Button cancel = dialog.findViewById(R.id.login_selection_cancel_btn);
                        Button ok = dialog.findViewById(R.id.login_selection_ok_btn);

                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });

                        ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                finish();
                            }
                        });

                        dialog.show();

                    } else {
                        addFragment(new MeFragment(), false);
                    }
                    return true;


            }
            return false;
        }
    };

    private void showInterstitial() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }


    public static void addFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.frame_container, fragment, "");
        //if (!tag.equals("Home"))
        if (addToBackStack)
            transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(HomeActivity.this);
        dialog.setMessage(R.string.areyousure);
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                Log.e("hello >>>", "....");
                Intent i = new Intent(Intent.ACTION_MAIN);
                i.addCategory(Intent.CATEGORY_HOME);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
                System.exit(0);

                //get gps
            }
        });
        dialog.setNegativeButton(
                "No", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        // TODO Auto-generated method stub

                    }
                });
        dialog.show();
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


//    //------------------------------------ offline online call -----------------------------------
//
//    private void OfflineCall() {
//        OkHttpClient client = new OkHttpClient.Builder()
//                .connectTimeout(100, TimeUnit.SECONDS)
//                .readTimeout(100, TimeUnit.SECONDS).build();
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(Config.Base_Url).client(client)
//                .addConverterFactory(GsonConverterFactory.create(new Gson())).build();
//        UserInterface signupInterface = retrofit.create(UserInterface.class);
//        Call<ResponseBody> resultCall = signupInterface.update_online_offline_status(logid, "Online", time_zone, current_time);
//        resultCall.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
//                // progressDialog.dismiss();
//                if (response.isSuccessful()) {
//                    try {
//
//                        String responedata = response.body().string();
//                        Log.e("ofline_online response ", " " + responedata);
//                        JSONObject object = new JSONObject(responedata);
//                        String error = object.getString("status");
//
//                        if (error.equals("1")) {
//
//
//                        } else {
//                            String message = object.getString("message");
//                            //Toast.makeText(getApplicationContext(), "" + message, Toast.LENGTH_SHORT).show();
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    Log.e("hgdhgfgdf", "dtrdfuydrfgjhjjfyt");
//
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                //   progressDialog.dismiss();
//                Toast.makeText(getApplicationContext(), "Server Problem Please try Next time...!", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    //------------------------------------ offline online call -----------------------------------
//
//    private void OfflineCall2() {
//        OkHttpClient client = new OkHttpClient.Builder()
//                .connectTimeout(100, TimeUnit.SECONDS)
//                .readTimeout(100, TimeUnit.SECONDS).build();
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(Config.Base_Url).client(client)
//                .addConverterFactory(GsonConverterFactory.create(new Gson())).build();
//        UserInterface signupInterface = retrofit.create(UserInterface.class);
//        Call<ResponseBody> resultCall = signupInterface.update_online_offline_status(logid, "Offline", time_zone, current_time);
//        resultCall.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
//                // progressDialog.dismiss();
//                if (response.isSuccessful()) {
//                    try {
//
//                        String responedata = response.body().string();
//                        Log.e("ofline_online response ", " " + responedata);
//                        JSONObject object = new JSONObject(responedata);
//                        String error = object.getString("status");
//
//                        if (error.equals("1")) {
//
//
//                        } else {
//                            String message = object.getString("message");
//                            Toast.makeText(getApplicationContext(), "" + message, Toast.LENGTH_SHORT).show();
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    Log.e("hgdhgfgdf", "dtrdfuydrfgjhjjfyt");
//
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                //   progressDialog.dismiss();
//                Toast.makeText(getApplicationContext(), "Server Problem Please try Next time...!", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
}
