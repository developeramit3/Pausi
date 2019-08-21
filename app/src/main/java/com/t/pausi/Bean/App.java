package com.t.pausi.Bean;

import android.content.SharedPreferences;
import android.support.multidex.MultiDexApplication;

import com.firebase.client.Firebase;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.FirebaseDatabase;
import com.t.pausi.R;

/**
 * Created by pintu22 on 15/9/17.
 */

public class App extends MultiDexApplication {
    private static App instance = null;
    public static SharedPreferences pref;
    public static SharedPreferences.Editor editor;

    @Override
    public void onCreate() {
        super.onCreate();
        setInstance(this);
        MobileAds.initialize(this, getString(R.string.admob_app_id));

//        pref = this.getSharedPreferences(Constant.APP_NAME,this.MODE_PRIVATE);
//        editor = pref.edit();
        //....
    }

    public static App getInstance() {
        return instance;
    }

    public void setInstance(App instance) {
        App.instance = instance;
    }


}
