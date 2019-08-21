package com.t.pausi.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.t.pausi.Bean.AlertConnection;
import com.t.pausi.Bean.ConnectionDetector;
import com.t.pausi.Bean.GPSTracker;
import com.t.pausi.Bean.MyLanguageSession;
import com.t.pausi.Bean.MySharedPref;
import com.t.pausi.Bean.User;
import com.t.pausi.Interface.Config;
import com.t.pausi.Interface.UserInterface;
import com.t.pausi.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.t.pausi.Constant.Constants.SHARED_PREF;

public class SignUpTwoActivity extends AppCompatActivity {
    String email, password;
    EditText signup_fname_edittext, signup_lanme_edittext, signup_mobile_edittext;
    Button register_btn;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    String FId, fname, lname, mobile;
    GPSTracker tracker;
    double P_latitude, P_longitude;
    ProgressDialog progressDialog;
    MySharedPref sp;
    CheckBox confirmation_checkbox;
    MultipartBody.Part body, body1;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    FirebaseAuth mAuth;
    String userId, image = "";
    MyLanguageSession myLanguageSession;
    private String language = "";
    TextView sign_up_two_login_text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLanguageSession = new MyLanguageSession(this);
        language = myLanguageSession.getLanguage();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());

        setContentView(R.layout.activity_sign_up_two);

        //-------------------- get firebase id ---------------------------------------

        SharedPreferences pref = getApplicationContext().getSharedPreferences(SHARED_PREF, 0);
        FId = pref.getString("regId", "");
        Log.e("firebase id ", " " + FId);

        //--------------------- connection detector -----------------------------------

        cd = new ConnectionDetector(this);
        isInternetPresent = cd.isConnectingToInternet();
        super.onStart();

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


        //-------------------------- --- firebase chat code ----------------------

        Firebase.setAndroidContext(this);
        //Get Firebase auth instance
        mAuth = FirebaseAuth.getInstance();
        mFirebaseInstance = FirebaseDatabase.getInstance();
        // get reference to 'users' node
        mFirebaseDatabase = mFirebaseInstance.getReference("users");

        //---------------------------- get intent data -----------------------------------

        Bundle bundle = new Bundle();
        bundle = getIntent().getExtras();
        if (bundle != null) {
            email = bundle.getString("email");
            password = bundle.getString("password");
            Log.e("email and pass ", " " + email + " " + password);
        }

        //----------------------------- find view ------------------------------

        signup_fname_edittext = findViewById(R.id.signup_fname_edittext);
        signup_lanme_edittext = findViewById(R.id.signup_lanme_edittext);
        signup_mobile_edittext = findViewById(R.id.signup_mobile_edittext);
        register_btn = findViewById(R.id.register_btn);
        confirmation_checkbox = findViewById(R.id.confirmation_checkbox);
        sign_up_two_login_text = findViewById(R.id.sign_up_two_login_text);

        //---------------------------- on click ---------------------


        sign_up_two_login_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isInternetPresent) {


                    SharedPreferences pref = getApplicationContext().getSharedPreferences(SHARED_PREF, 0);
                    FId = pref.getString("regId", "");
                    Log.e("firebase id ", " " + FId);
                    if (signup_fname_edittext.getText().toString().trim().equalsIgnoreCase("")) {
                        signup_fname_edittext.setError(getString(R.string.can_not_be_empty));
                        signup_fname_edittext.requestFocus();
                    } else if (signup_lanme_edittext.getText().toString().trim().equalsIgnoreCase("")) {
                        signup_lanme_edittext.setError(getString(R.string.can_not_be_empty));
                        signup_lanme_edittext.requestFocus();
                    } else if (signup_mobile_edittext.getText().toString().trim().equalsIgnoreCase("")) {
                        signup_mobile_edittext.setError(getString(R.string.can_not_be_empty));
                        signup_mobile_edittext.requestFocus();
                    } else if (confirmation_checkbox.isChecked() == false) {
                        Toast.makeText(SignUpTwoActivity.this, R.string.checkregiasteragree, Toast.LENGTH_SHORT).show();
                    } else {
                        fname = signup_fname_edittext.getText().toString().trim();
                        lname = signup_lanme_edittext.getText().toString().trim();
                        mobile = signup_mobile_edittext.getText().toString().trim();
                        body = MultipartBody.Part.createFormData("id_proof", "");
                        body1 = MultipartBody.Part.createFormData("image", "");

                        boolean valid_mobile = isValidMobile(mobile);
                        if (valid_mobile == false) {
                            signup_mobile_edittext.setError(getString(R.string.notvalidnumber));
                            signup_mobile_edittext.requestFocus();

                        } else {
                            Signup_call();
                        }


                    }


                } else {
                    AlertConnection.showAlertDialog(SignUpTwoActivity.this, getString(R.string.no_internal_connection),
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

    private boolean isValidMobile(String phone) {
        boolean check = false;
        if (!Pattern.matches("[a-zA-Z]+", phone)) {
            if (phone.length() < 8 || phone.length() > 13) {
                // if(phone.length() != 10) {
                check = false;

            } else {
                check = true;
            }
        } else {
            check = false;
        }
        return check;
    }

    private void createUser(String name, String email, String profile_pic, String token, String profile) {
        // TODO
        // In real apps this userId should be fetched
        // by implementing firebase auth
        User user = new User(name, email, profile_pic, token, profile);
        mFirebaseDatabase.child(userId).child("credentials").setValue(user);
        addUserChangeListener();
    }

    /**
     * User data change listener
     */
    private void addUserChangeListener() {
        mFirebaseDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user == null) {
                    Log.e("", "User data is null!");
                    return;
                }
                Log.e("User data is changed!", user.name + ", " + user.email);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e("Failed to read user", "" + error.toException());
            }
        });
    }

    //------------------------------------ signup call -----------------------------------

    private void Signup_call() {
        progressDialog = new ProgressDialog(SignUpTwoActivity.this);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.show();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.Base_Url).client(client)
                .addConverterFactory(GsonConverterFactory.create(new Gson())).build();
        UserInterface signupInterface = retrofit.create(UserInterface.class);
        Call<ResponseBody> resultCall = signupInterface.signup(fname, lname, email, mobile, "", password, "", "", "USER", FId, String.valueOf(P_latitude), String.valueOf(P_longitude), "Android", body, body1);
        resultCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    try {

                        String responedata = response.body().string();
                        Log.e("get signup response** ", " " + responedata);
                        JSONObject object = new JSONObject(responedata);
                        String error = object.getString("status");

                        if (error.equals("1")) {

                            final JSONObject ressult = object.getJSONObject("result");
                            image = ressult.getString("image");
//                            sp = new MySharedPref();
//                            sp.saveData(getApplicationContext(), "ldata", ressult + "");

                            final String pass = "qwerty";

                            mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = mAuth.getCurrentUser(); //You Firebase user
                                        userId = user.getUid();
                                        createUser(fname, email, image, FId, "123");
                                        Toast.makeText(SignUpTwoActivity.this, getString(R.string.account_created), Toast.LENGTH_LONG).show();
                                        sp = new MySharedPref();
                                        sp.saveData(getApplicationContext(), "ldata", ressult + "");
                                        Intent intent = new Intent(SignUpTwoActivity.this, HomeActivity.class);
                                        startActivity(intent);
                                        finish();

                                    } else {
                                        Log.e("createUserWithEmail ", "" + task.getException());

                                        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(SignUpTwoActivity.this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {

                                                if (!task.isSuccessful()) {
                                                    Toast.makeText(SignUpTwoActivity.this, R.string.signinerroer, Toast.LENGTH_SHORT).show();
                                                } else
                                                    mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
                                                        @Override
                                                        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                                                            FirebaseUser user = firebaseAuth.getCurrentUser();
                                                            if (user != null && user.getUid() != null) {
                                                                userId = user.getUid();
                                                                sp = new MySharedPref();
                                                                sp.saveData(getApplicationContext(), "ldata", ressult + "");
                                                                addUserChangeListener1();
                                                            }
                                                        }
                                                    });


                                            }
                                        });
                                        //Toast.makeText(SignUpTwoActivity.this, getString(R.string.could_not_create_account), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


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

    public void addUserChangeListener1() {
        mFirebaseDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.child("credentials").getValue(User.class);
                if (user == null) {
                    Log.e("", "User data is null!");
                    return;
                } else {
                    String email = user.email;
                    String name = user.name;
                    String profile = user.profileUrl;
                    String token = user.token;

                    mFirebaseDatabase.child(userId).child("credentials").child("token").setValue(FId);
                    mFirebaseDatabase.child(userId).child("credentials").child("profileUrl").setValue(image);
                    Intent intent = new Intent(SignUpTwoActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e("Failed to read user", "" + error.toException());
            }
        });
    }
}
