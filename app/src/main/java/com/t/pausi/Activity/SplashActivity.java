package com.t.pausi.Activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.database.FirebaseDatabase;
import com.t.pausi.Bean.MyLanguageSession;
import com.t.pausi.Bean.MySharedPref;
import com.t.pausi.Bean.PrefManager;
import com.t.pausi.R;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SplashActivity extends AppCompatActivity {
    MyLanguageSession myLanguageSession;
    private String language = "";

    MySharedPref sp;
    String ldata, type, types;
    protected Context context;
    private static final int REQUEST_CODE_PERMISSION = 2;
    String[] mPermission = {
            Manifest.permission.INTERNET,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLanguageSession = new MyLanguageSession(this);
        language = myLanguageSession.getLanguage();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());
        setContentView(R.layout.activity_splash);
        context = this;
        printHashKey();
        try {
            if (ActivityCompat.checkSelfPermission(this, mPermission[0])
                    != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, mPermission[1])
                            != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, mPermission[2])
                            != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, mPermission[3])
                            != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, mPermission[4])
                            != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, mPermission[5])
                            != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, mPermission[6])
                            != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        mPermission, REQUEST_CODE_PERMISSION);

                // If any permission aboe not allowed by user, this condition will execute every tim, else your else part will work
            } else {


                sp = new MySharedPref();
                ldata = sp.getData(getApplicationContext(), "ldata", "null");
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (ldata == null || ldata.equalsIgnoreCase("null")) {

                            Intent intent = new Intent(SplashActivity.this, WelcomeSliderActivity.class);
                            //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_right);
                            startActivity(intent);
                            finish();
                        } else {

                            Intent in = new Intent(SplashActivity.this, HomeActivity.class);
                            in.putExtra("ss", "home");
                            in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            SplashActivity.this.startActivity(in);
                            finish();
                        }

                    }
                }, 3000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


//        byte[] sha1 = {
//                (byte) 0xCF, (byte) 0x0F, (byte) 0xF4, (byte) 0xFA, (byte) 0xD3, (byte) 0x43, (byte) 0xB8, (byte) 0x81, (byte) 0x00, (byte) 0x5E, (byte) 0x35, (byte) 0x26, (byte) 0x94, 0x78, (byte) 0x78, (byte) 0x87, (byte) 0x35, (byte) 0xF9, (byte) 0xBF, (byte) 0x35
//                //SHA1: CF:0F:F4:FA:D3:43:B8:81:00:5E:35:26:94:78:78:87:35:F9:BF:35
//                // C6:CC:60:1E:56:83:97:0A:35:D8:2C:0F:F7:F0:A3:A7:92:B4:A6:9B
//        };
//
//        byte[] sha2 = {
//                0x5D, (byte) 0x89, (byte) 0xE2, (byte) 0x8E, (byte) 0x01, (byte) 0x6E, (byte) 0x2D, (byte) 0x4A, (byte) 0xC7, (byte) 0x82, (byte) 0xC5, (byte) 0x45, (byte) 0x2D, (byte) 0x78, (byte) 0xE3, (byte) 0xA8, (byte) 0xA7, (byte) 0xA6, (byte) 0x31, (byte) 0xB0
//
//                // SHA1: 5D:89:E2:8E:01:6E:2D:4A:C7:82:C5:45:2D:78:E3:A8:A7:A6:31:B0
//                // C6:CC:60:1E:56:83:97:0A:35:D8:2C:0F:F7:F0:A3:A7:92:B4:A6:9B
//        };
//        System.out.println("keyhashGooglePlaySignIn1:" + Base64.encodeToString(sha1, Base64.NO_WRAP));
//        System.out.println("keyhashGooglePlaySignIn2:" + Base64.encodeToString(sha2, Base64.NO_WRAP));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e("Req Code", "" + requestCode);
        System.out.println(grantResults[0] == PackageManager.PERMISSION_GRANTED);
        System.out.println(grantResults[1] == PackageManager.PERMISSION_GRANTED);
        System.out.println(grantResults[2] == PackageManager.PERMISSION_GRANTED);
        System.out.println(grantResults[3] == PackageManager.PERMISSION_GRANTED);
        System.out.println(grantResults[4] == PackageManager.PERMISSION_GRANTED);
        System.out.println(grantResults[5] == PackageManager.PERMISSION_GRANTED);
        System.out.println(grantResults[6] == PackageManager.PERMISSION_GRANTED);


        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.length == 7 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[2] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[3] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[4] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[5] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[6] == PackageManager.PERMISSION_GRANTED
                    )

            {

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (ldata == null || ldata.equalsIgnoreCase("null")) {

                            Intent intent = new Intent(SplashActivity.this, WelcomeSliderActivity.class);
                            //  overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_right);
                            startActivity(intent);
                            finish();
                        } else {

                            Intent in = new Intent(SplashActivity.this, HomeActivity.class);
                            in.putExtra("ss", "home");
                            in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            SplashActivity.this.startActivity(in);
                            finish();
                        }

                    }
                }, 3000);

            } else {
                Toast.makeText(SplashActivity.this, "Denied", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

    }

    public void printHashKey() {
        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.t.pausi",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));

            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

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
