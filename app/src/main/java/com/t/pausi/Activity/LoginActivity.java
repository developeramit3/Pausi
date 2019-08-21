package com.t.pausi.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
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
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.t.pausi.Constant.Constants.SHARED_PREF;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    EditText login_email_edittext, login_password_edittext;
    Button login_btn;
    TextView forgot_pass_text, skip_textview;
    LinearLayout login_to_signup_lay;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    String FId, pass, email, user_id;
    GPSTracker tracker;
    double P_latitude, P_longitude;
    ProgressDialog progressDialog;
    MySharedPref sp;
    CircleImageView google_login_image, fb_login_image;
    private GoogleSignInOptions gso;
    private GoogleApiClient mGoogleApiClient;
    private int RC_SIGN_IN = 100;
    String googlename, googleid, googleemail;
    LoginButton loginButton;
    CallbackManager callbackManager = CallbackManager.Factory.create();
    private String facebook_id, f_name, m_name, l_name, gender, profile_image, full_name, email_id;
    String fb_name, fb_id, fb_email, fb_profile_url, facebook_status, g_status;
    private FirebaseAuth mAuth;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    String image, userId;
    MyLanguageSession myLanguageSession;
    private String language = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(LoginActivity.this);
        AppEventsLogger.activateApp(this);
        myLanguageSession = new MyLanguageSession(this);
        language = myLanguageSession.getLanguage();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());
        setContentView(R.layout.activity_login);

        //-------------------- get firebase id ---------------------------------------

        SharedPreferences pref = getApplicationContext().getSharedPreferences(SHARED_PREF, 0);
        FId = pref.getString("regId", "");
        Log.e("firebase id ", " " + FId);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("users");


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
        }

        //---------------- ---find view -----------------------------------------------

        login_email_edittext = findViewById(R.id.login_email_edittext);
        login_password_edittext = findViewById(R.id.login_password_edittext);
        login_btn = findViewById(R.id.login_btn);
        forgot_pass_text = findViewById(R.id.forgot_pass_text);
        login_to_signup_lay = findViewById(R.id.login_to_signup_lay);
        google_login_image = findViewById(R.id.google_login_image);
        fb_login_image = findViewById(R.id.fb_login_image);
        skip_textview = findViewById(R.id.skip_textview);

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile, email"));

        //--------------------- google login -------------------------------------------

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        //Initializing google api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        google_login_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences pref = getApplicationContext().getSharedPreferences(SHARED_PREF, 0);
                FId = pref.getString("regId", "");
                Log.e("firebase id ", " " + FId);
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        //-------------------------- facebook login ------------------------------------

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                facebook_id = f_name = m_name = l_name = gender = profile_image = full_name = email_id = "";

                if (AccessToken.getCurrentAccessToken() != null) {
                    RequestData();
                    Profile profile = Profile.getCurrentProfile();
                    if (profile != null) {
                        facebook_id = profile.getId();
                        Log.e("facebook_id", facebook_id);
                        f_name = profile.getFirstName();
                        Log.e("f_name", f_name);
                        m_name = profile.getMiddleName();
                        Log.e("m_name", m_name);
                        l_name = profile.getLastName();
                        Log.e("l_name", l_name);
                        full_name = profile.getName();
                        Log.e("full_name", full_name);
                        profile_image = profile.getProfilePictureUri(400, 400).toString();
                        Log.e("profile_image", profile_image);

                    }

                }
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException exception) {
            }
        });


        //------------------- on click -------------------------------------------------

        forgot_pass_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ForgotActivity.class));
            }
        });

        skip_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                finish();
            }
        });


        fb_login_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view == fb_login_image) {
                    SharedPreferences pref = getApplicationContext().getSharedPreferences(SHARED_PREF, 0);
                    FId = pref.getString("regId", "");
                    Log.e("firebase id ", " " + FId);
                    loginButton.performClick();
                }
            }
        });

        login_to_signup_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class));

            }
        });

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isInternetPresent) {
                    SharedPreferences pref = getApplicationContext().getSharedPreferences(SHARED_PREF, 0);
                    FId = pref.getString("regId", "");
                    Log.e("firebase id ", " " + FId);
                    if (login_email_edittext.getText().toString().trim().equalsIgnoreCase("")) {
                        login_email_edittext.setError(getString(R.string.can_not_be_empty));
                        login_email_edittext.requestFocus();
                    } else if (login_password_edittext.getText().toString().trim().equalsIgnoreCase("")) {
                        login_password_edittext.setError(getString(R.string.can_not_be_empty));
                        login_password_edittext.requestFocus();
                    } else {
                        email = login_email_edittext.getText().toString().trim();
                        pass = login_password_edittext.getText().toString().trim();
                        Login_call();
                    }

                } else {
                    AlertConnection.showAlertDialog(LoginActivity.this, getString(R.string.no_internal_connection),
                            getString(R.string.donothaveinternet), false);
                }
            }
        });
    }

    private void addUserChangeListener() {
        mFirebaseDatabase.child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
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

                    mFirebaseDatabase.child(user_id).child("credentials").child("token").setValue(FId);
                    mFirebaseDatabase.child(user_id).child("credentials").child("profileUrl").setValue(image);
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
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

    //--------------------------------- facebook request data ----------------------------

    public void RequestData() {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {

                JSONObject json = response.getJSONObject();
                System.out.println("Json data :" + json);
                try {
                    if (json != null) {
                        String text = "<b>Name :</b> " + json.getString("name") + "<br><br><b>Email :</b> " + json.getString("email");
                        JSONObject profile_pic_data = new JSONObject(json.get("picture").toString());
                        JSONObject profile_pic_url = new JSONObject(profile_pic_data.getString("data").toString());
                        System.out.println("profile_pic_url" + profile_pic_url.getString("url"));
                        System.out.println("emailsss" + json.getString("email"));
                        System.out.println("profilessss" + Html.fromHtml(text));
                        System.out.println("acebookid" + json.getString("id"));
                        fb_id = json.getString("id");
                        fb_name = json.getString("name");
                        fb_email = json.getString("email");
                        fb_profile_url = profile_pic_url.getString("url");

                        FB_Login_call();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,email,picture");
        request.setParameters(parameters);
        request.executeAsync();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            //Calling a new function to handle signin
            handleSignInResult(result);
        } else callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    //-------------------------- After the signing ----------------------------

    private void handleSignInResult(GoogleSignInResult result) {
        //If the login succeed
        if (result.isSuccess()) {
            //Getting google account
            GoogleSignInAccount acct = result.getSignInAccount();
            googlename = acct.getDisplayName();
            googleemail = acct.getEmail();
            //   String image=  acct.getPhotoUrl().toString();
            googleid = acct.getId();
            acct.getId();

            Toast.makeText(this, "" + googleemail, Toast.LENGTH_SHORT).show();
            Google_Login_call();
        } else {
            //If login fails
            Toast.makeText(this, R.string.loginfailed, Toast.LENGTH_LONG).show();
        }
    }

    //------------------------------------ login call -----------------------------------

    private void Login_call() {
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.show();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.Base_Url).client(client)
                .addConverterFactory(GsonConverterFactory.create(new Gson())).build();
        UserInterface signupInterface = retrofit.create(UserInterface.class);
        Call<ResponseBody> resultCall = signupInterface.login(email, pass, FId, "Android", String.valueOf(P_latitude), String.valueOf(P_longitude));
        resultCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    try {

                        String responedata = response.body().string();
                        Log.e("get login response** ", " " + responedata);
                        JSONObject object = new JSONObject(responedata);
                        String error = object.getString("status");

                        if (error.equals("1")) {

                            final JSONObject ressult = object.getJSONObject("result");
                            image = ressult.getString("image");


                            String pass = "qwerty";
                            mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        progressDialog.dismiss();
                                        Toast.makeText(LoginActivity.this, R.string.signinerroer, Toast.LENGTH_SHORT).show();
                                    } else {

                                        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
                                            @Override
                                            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                                if (user != null && user.getUid() != null) {

                                                    Log.e("id ", user.getUid());

                                                    sp = new MySharedPref();
                                                    sp.saveData(getApplicationContext(), "ldata", ressult + "");
                                                    user_id = user.getUid();
                                                    addUserChangeListener();
                                                }
                                            }
                                        });
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    //-------------------------------------- google login ----------------------------------

    private void Google_Login_call() {
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.show();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.Base_Url).client(client)
                .addConverterFactory(GsonConverterFactory.create(new Gson())).build();
        UserInterface signupInterface = retrofit.create(UserInterface.class);
        Call<ResponseBody> resultCall = signupInterface.social_login(googlename, "", "", googleemail, FId, googleid, "Android", "User", "", String.valueOf(P_latitude), String.valueOf(P_longitude));
        //Call<ResponseBody> resultCall = signupInterface.social_login("test gmail", "", "mutaemelda@gmail.com", "123", "123456", "Android", "User", String.valueOf(P_latitude), String.valueOf(P_longitude));
        resultCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    try {

                        String responedata = response.body().string();
                        Log.e("google login response ", " " + responedata);
                        JSONObject object = new JSONObject(responedata);
                        String error = object.getString("status");

                        if (error.equals("1")) {

                            final JSONObject ressult = object.getJSONObject("result");
                            image = ressult.getString("image");
                            String pass = "qwerty";
                            mAuth.createUserWithEmailAndPassword(googleemail, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = mAuth.getCurrentUser(); //You Firebase user
                                        userId = user.getUid();
                                        createUser(googlename, googleemail, image, FId, "123");
                                        //createUser("test gmail", googleemail, image, "123456", "123");
                                        sp = new MySharedPref();
                                        sp.saveData(getApplicationContext(), "ldata", ressult + "");
                                        Toast.makeText(LoginActivity.this, R.string.accountcreated, Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                        startActivity(intent);
                                        finish();

                                    } else {
                                        Log.e("createUserWithEmail ", "" + task.getException());

                                        String pass = "qwerty";
                                        mAuth.signInWithEmailAndPassword(googleemail, pass).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (!task.isSuccessful()) {
                                                    Toast.makeText(LoginActivity.this, R.string.signinerroer, Toast.LENGTH_SHORT).show();
                                                } else
                                                    mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
                                                        @Override
                                                        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                                                            FirebaseUser user = firebaseAuth.getCurrentUser();
                                                            if (user != null && user.getUid() != null) {
                                                                user_id = user.getUid();
                                                                sp = new MySharedPref();
                                                                sp.saveData(getApplicationContext(), "ldata", ressult + "");
                                                                addUserChangeListener();
                                                            }
                                                        }
                                                    });
                                            }
                                        });


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

    //-------------------------------------- fb login ----------------------------------

    private void FB_Login_call() {

        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.show();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.Base_Url).client(client)
                .addConverterFactory(GsonConverterFactory.create(new Gson())).build();
        UserInterface signupInterface = retrofit.create(UserInterface.class);
        Call<ResponseBody> resultCall = signupInterface.social_login(fb_name, "", "", fb_email, FId, fb_id, "Android", "User", "", String.valueOf(P_latitude), String.valueOf(P_longitude));
        resultCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    try {

                        String responedata = response.body().string();
                        Log.e("fb login response ", " " + responedata);
                        JSONObject object = new JSONObject(responedata);
                        String error = object.getString("status");

                        if (error.equals("1")) {

                            final JSONObject ressult = object.getJSONObject("result");
                            image = ressult.getString("image");

                            String pass = "qwerty";
                            mAuth.createUserWithEmailAndPassword(fb_email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = mAuth.getCurrentUser(); //You Firebase user
                                        userId = user.getUid();
                                        createUser(fb_name, fb_email, image, FId, "123");
                                        Toast.makeText(LoginActivity.this, getString(R.string.account_created), Toast.LENGTH_LONG).show();
                                        sp = new MySharedPref();
                                        sp.saveData(getApplicationContext(), "ldata", ressult + "");
                                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                        startActivity(intent);
                                        finish();

                                    } else {
                                        Log.e("createUserWithEmail ", "" + task.getException());
                                        Log.e("createUserWithEmail ", "" + task.getException());

                                        String pass = "qwerty";
                                        mAuth.signInWithEmailAndPassword(fb_email, pass).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (!task.isSuccessful()) {
                                                    Toast.makeText(LoginActivity.this, R.string.signinerroer, Toast.LENGTH_SHORT).show();
                                                } else
                                                    mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
                                                        @Override
                                                        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                                                            FirebaseUser user = firebaseAuth.getCurrentUser();
                                                            if (user != null && user.getUid() != null) {
                                                                user_id = user.getUid();
                                                                sp = new MySharedPref();
                                                                sp.saveData(getApplicationContext(), "ldata", ressult + "");
                                                                addUserChangeListener();
                                                            }
                                                        }
                                                    });
                                            }
                                        });

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

    private void createUser(String name, String email, String profile_pic, String token, String profile) {
        // TODO
        // In real apps this userId should be fetched
        // by implementing firebase auth
        User user = new User(name, email, profile_pic, token, profile);
        mFirebaseDatabase.child(userId).child("credentials").setValue(user);
        addUserChangeListener2();
    }

    /**
     * User data change listener
     */
    private void addUserChangeListener2() {
        mFirebaseDatabase.child(userId).addValueEventListener(new ValueEventListener() {
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


}
