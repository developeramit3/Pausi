package com.t.pausi.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.t.pausi.Bean.AlertConnection;
import com.t.pausi.Bean.ConnectionDetector;
import com.t.pausi.Bean.GPSTracker;
import com.t.pausi.Bean.MyLanguageSession;
import com.t.pausi.Bean.MySharedPref;
import com.t.pausi.Interface.Config;
import com.t.pausi.Interface.UserInterface;
import com.t.pausi.R;
import com.t.pausi.autocomplete.GeoAutoCompleteAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UpdateZipCodeActivity extends AppCompatActivity {
    ImageView zip_code_back;
    AutoCompleteTextView zip_code_edittext;
    Button update_zip_btn;
    String zip_code, ldata, logid;
    ConnectionDetector cd;
    Boolean isInternetPresent = false;
    ProgressDialog progressDialog;
    MySharedPref sp;
    int countDrop;
    GPSTracker tracker;
    double P_latitude, P_longitude;
    MyLanguageSession myLanguageSession;
    private String language = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLanguageSession = new MyLanguageSession(this);
        language = myLanguageSession.getLanguage();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());
        setContentView(R.layout.activity_update_zip_code);

        cd = new ConnectionDetector(this);
        isInternetPresent = cd.isConnectingToInternet();
        super.onStart();

        //-------------------------------- get login id -----------------------------------

        sp = new MySharedPref();
        ldata = sp.getData(getApplicationContext(), "ldata", "null");
        if (!ldata.equals("") || ldata != null) {
            try {
                JSONObject jsonObject = new JSONObject(ldata);
                logid = jsonObject.getString("id");
                Log.e("logid ", " " + logid);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //-------------------- Get current lat lon ------------------------------------

        tracker = new GPSTracker(getApplicationContext());
        if (tracker.canGetLocation()) {
            P_latitude = tracker.getLatitude();
            P_longitude = tracker.getLongitude();
            Log.e("current_lat ", " " + P_latitude);
            Log.e("current_Lon ", " " + P_longitude);
//            lat= String.valueOf(P_latitude);
//            lon= String.valueOf(P_longitude);
        }

        //------------------------ find view --------------------

        zip_code_back = findViewById(R.id.zip_code_back);
        zip_code_edittext = findViewById(R.id.zip_code_edittext);
        update_zip_btn = findViewById(R.id.update_zip_btn);


        //------------------------------------ get profile --------------------------------

        if (isInternetPresent) {

            GetProfile_call();

        } else {
            AlertConnection.showAlertDialog(getApplicationContext(), "No Internet Connection",
                    "You don't have internet connection.", false);
        }


        //---------------------------- on click ----------------------------

        zip_code_edittext.setThreshold(1);
        zip_code_edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    loadDataDrop(zip_code_edittext.getText().toString());
//                    if (s.length()>10) {
//                        getLatLongFromAddress(add_event_venue.getText().toString());
//                    }
                }
            }
        });

        zip_code_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        update_zip_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isInternetPresent) {
                    if (zip_code_edittext.getText().toString().trim().equalsIgnoreCase("")) {
                        zip_code_edittext.setError(getString(R.string.can_not_be_empty));
                        zip_code_edittext.requestFocus();
                    } else {
                        zip_code = zip_code_edittext.getText().toString().trim();

                        UpdateZipCode_call();
                    }

                } else {
                    AlertConnection.showAlertDialog(UpdateZipCodeActivity.this, getString(R.string.no_internal_connection),
                            getString(R.string.donothaveinternet), false);
                }

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

    private void loadDataDrop(String s) {
        try {
            if (countDrop == 0) {
                List<String> l1 = new ArrayList<>();
                if (s == null) {
                } else {
                    l1.add(s);
                    GeoAutoCompleteAdapter ga = new GeoAutoCompleteAdapter(UpdateZipCodeActivity.this, l1, "" + P_latitude, "" + P_longitude);
                    zip_code_edittext.setAdapter(ga);
                }

            }
            countDrop++;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //------------------------------------ update zip call -----------------------------------

    private void UpdateZipCode_call() {
        progressDialog = new ProgressDialog(UpdateZipCodeActivity.this);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.show();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.Base_Url).client(client)
                .addConverterFactory(GsonConverterFactory.create(new Gson())).build();
        UserInterface signupInterface = retrofit.create(UserInterface.class);
        Call<ResponseBody> resultCall = signupInterface.update_zipcode(logid, zip_code);
        resultCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    try {

                        String responedata = response.body().string();
                        Log.e("upzipcode response ", "" + responedata);
                        JSONObject object = new JSONObject(responedata);
                        String error = object.getString("status");

                        if (error.equals("1")) {
                            Toast.makeText(UpdateZipCodeActivity.this, R.string.zipupdate, Toast.LENGTH_SHORT).show();
                            zip_code_edittext.clearFocus();
                            startActivity(new Intent(getApplicationContext(),HomeActivity.class));

                        } else {
                            String message = object.getString("message");
                            Toast.makeText(getApplicationContext(), "" + message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("hgdhgfgdf", "dtrdfuydrfgjhjjfyt");

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), getString(R.string.server_problem), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //------------------------------------ get profile call -----------------------------------

    private void GetProfile_call() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.Base_Url).client(client)
                .addConverterFactory(GsonConverterFactory.create(new Gson())).build();
        UserInterface signupInterface = retrofit.create(UserInterface.class);
        Call<ResponseBody> resultCall = signupInterface.get_profile(logid);
        resultCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                // progressDialog.dismiss();
                if (response.isSuccessful()) {
                    try {

                        String responedata = response.body().string();
                        Log.e("get profile response** ", " " + responedata);
                        JSONObject object = new JSONObject(responedata);
                        String error = object.getString("status");

                        if (error.equals("1")) {

                            JSONObject jsonObject = object.getJSONObject("result");
                            String zipcode = jsonObject.getString("zipcode");
                            zip_code_edittext.setText(zipcode);

                        } else {
                            String message = object.getString("message");
                            Toast.makeText(getApplicationContext(), "" + message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("hgdhgfgdf", "dtrdfuydrfgjhjjfyt");

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //   progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), getString(R.string.server_problem), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
