package com.t.pausi.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.t.pausi.Bean.AlertConnection;
import com.t.pausi.Bean.ConnectionDetector;
import com.t.pausi.Bean.MyLanguageSession;
import com.t.pausi.Bean.MySharedPref;
import com.t.pausi.Interface.Config;
import com.t.pausi.Interface.UserInterface;
import com.t.pausi.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.t.pausi.Constant.Constants.SHARED_PREF;

public class UpdateMyInfoActivity extends AppCompatActivity {

    ImageView upinfo_back;
    EditText update_fname_edittext, update_lanme_edittext, update_email_edittext, update_phone_edittext;
    String fname, lname, email, phone, ldata, logid, old_pass, new_pass;
    Button update_profile_info_btn, update_password_btn;
    ConnectionDetector cd;
    Boolean isInternetPresent = false;
    ProgressDialog progressDialog;
    MySharedPref sp;
    EditText update_new_pass_edittext, update_old_pass_edittext, update_cpassword_edittext;
    MultipartBody.Part body, body1;
    MyLanguageSession myLanguageSession;
    private String language = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLanguageSession = new MyLanguageSession(this);
        language = myLanguageSession.getLanguage();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());

        setContentView(R.layout.activity_update_my_info);


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


        //------------------------------ get intent ----------------------------

        Bundle bundle = new Bundle();
        bundle = getIntent().getExtras();
        if (bundle != null) {
            fname = bundle.getString("fname");
            lname = bundle.getString("lname");
            phone = bundle.getString("phone");
            email = bundle.getString("email");
        }


        //---------------------------------- find view ----------------------------

        upinfo_back = findViewById(R.id.upinfo_back);
        update_fname_edittext = findViewById(R.id.update_fname_edittext);
        update_lanme_edittext = findViewById(R.id.update_lanme_edittext);
        update_email_edittext = findViewById(R.id.update_email_edittext);
        update_phone_edittext = findViewById(R.id.update_phone_edittext);
        update_new_pass_edittext = findViewById(R.id.update_new_pass_edittext);
        update_old_pass_edittext = findViewById(R.id.update_old_pass_edittext);
        update_cpassword_edittext = findViewById(R.id.update_cpassword_edittext);
        update_profile_info_btn = findViewById(R.id.update_profile_info_btn);
        update_password_btn = findViewById(R.id.update_password_btn);

        //---------------------------------- set data -----------------------------

        update_fname_edittext.setText(fname);
        update_lanme_edittext.setText(lname);
        update_email_edittext.setText(phone);
        update_phone_edittext.setText(email);

        //----------------------------------- on click ----------------------------

        upinfo_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        update_profile_info_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fname = update_fname_edittext.getText().toString().trim();
                lname = update_lanme_edittext.getText().toString().trim();
                email = update_email_edittext.getText().toString().trim();
                phone = update_phone_edittext.getText().toString().trim();
                body = MultipartBody.Part.createFormData("id_proof", "");
                body1 = MultipartBody.Part.createFormData("image", "");

                if (isInternetPresent) {

                    UpdateUser_call();

                } else {
                    AlertConnection.showAlertDialog(getApplicationContext(), getString(R.string.no_internal_connection),
                            getString(R.string.donothaveinternet), false);
                }
            }
        });

        update_password_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isInternetPresent) {
                    if (update_old_pass_edittext.getText().toString().trim().equalsIgnoreCase("")) {
                        update_old_pass_edittext.setError(getString(R.string.can_not_be_empty));
                        update_old_pass_edittext.requestFocus();
                    } else if (update_new_pass_edittext.getText().toString().trim().equalsIgnoreCase("")) {
                        update_new_pass_edittext.setError(getString(R.string.can_not_be_empty));
                        update_new_pass_edittext.requestFocus();
                    } else if (update_cpassword_edittext.getText().toString().trim().equalsIgnoreCase("")) {
                        update_cpassword_edittext.setError(getString(R.string.can_not_be_empty));
                        update_cpassword_edittext.requestFocus();
                    } else if (!update_new_pass_edittext.getText().toString().trim().equalsIgnoreCase(update_cpassword_edittext.getText().toString().trim())) {
                        Toast.makeText(UpdateMyInfoActivity.this, R.string.passmismatch, Toast.LENGTH_SHORT).show();
                    } else {
                        old_pass = update_old_pass_edittext.getText().toString().trim();
                        new_pass = update_new_pass_edittext.getText().toString().trim();
                        ChangePassword_call();
                    }

                } else {
                    AlertConnection.showAlertDialog(UpdateMyInfoActivity.this, getString(R.string.no_internal_connection),
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

    //------------------------------------ UpdateUser_call call -----------------------------------

    private void UpdateUser_call() {
        progressDialog = new ProgressDialog(UpdateMyInfoActivity.this);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.show();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.Base_Url).client(client)
                .addConverterFactory(GsonConverterFactory.create(new Gson())).build();
        UserInterface signupInterface = retrofit.create(UserInterface.class);
        Call<ResponseBody> resultCall = signupInterface.user_update(logid, fname, lname, "", "USER", phone, "","","","","", body, body1);
        resultCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    try {

                        String responedata = response.body().string();
                        Log.e("update user response** ", " " + responedata);
                        JSONObject object = new JSONObject(responedata);
                        String error = object.getString("status");

                        if (error.equals("1")) {

                            Toast.makeText(UpdateMyInfoActivity.this, R.string.infoupdate, Toast.LENGTH_SHORT).show();

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

    //------------------------------------ change password call -----------------------------------

    private void ChangePassword_call() {
        progressDialog = new ProgressDialog(UpdateMyInfoActivity.this);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.show();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.Base_Url).client(client)
                .addConverterFactory(GsonConverterFactory.create(new Gson())).build();
        UserInterface signupInterface = retrofit.create(UserInterface.class);
        Call<ResponseBody> resultCall = signupInterface.change_password(logid, old_pass, new_pass);
        resultCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    try {

                        String responedata = response.body().string();
                        Log.e("change pass response** ", " " + responedata);
                        JSONObject object = new JSONObject(responedata);
                        String error = object.getString("status");

                        if (error.equals("1")) {

                            Toast.makeText(UpdateMyInfoActivity.this, R.string.passupdate, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            finish();

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

}
