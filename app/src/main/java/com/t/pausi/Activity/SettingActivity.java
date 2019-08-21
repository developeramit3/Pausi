package com.t.pausi.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.t.pausi.Bean.MyLanguageSession;
import com.t.pausi.Bean.MySharedPref;
import com.t.pausi.R;

public class SettingActivity extends AppCompatActivity {
    ImageView setting_back, setting_camera_image;
    TextView user_name_text_setting, email_text_setting, phone_text_setting;
    String fname, lname, email, phone, user_type = "", image, id_proof, broker;
    Button logout_btn, real_state_btn_setting;
    MySharedPref sp;
    LinearLayout help_lay, about_lay, follow_layout;
    TextView privacy_text, terms_text, update_my_info_text, my_pay_method_text, update_zip_code_text,
            noti_textview;
    private FirebaseAuth mAuth;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    MyLanguageSession myLanguageSession;
    private String language = "";
    public static String logout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(SettingActivity.this);
        AppEventsLogger.activateApp(this);
        myLanguageSession = new MyLanguageSession(this);
        language = myLanguageSession.getLanguage();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());

        setContentView(R.layout.activity_setting);


        //------------------------------ get intent ----------------------------

        Bundle bundle = new Bundle();
        bundle = getIntent().getExtras();
        if (bundle != null) {
            fname = bundle.getString("fname");
            lname = bundle.getString("lname");
            phone = bundle.getString("phone");
            email = bundle.getString("email");
            user_type = bundle.getString("user_type");
            image = bundle.getString("image");
            id_proof = bundle.getString("id_proof");
            broker = bundle.getString("broker");

        }

        //------------------------------- find view ------------------------------

        setting_back = findViewById(R.id.setting_back);
        user_name_text_setting = findViewById(R.id.user_name_text_setting);
        email_text_setting = findViewById(R.id.email_text_setting);
        phone_text_setting = findViewById(R.id.phone_text_setting);
        logout_btn = findViewById(R.id.logout_btn);
        help_lay = findViewById(R.id.help_lay);
        about_lay = findViewById(R.id.about_lay);
        privacy_text = findViewById(R.id.privacy_text);
        terms_text = findViewById(R.id.terms_text);
        update_my_info_text = findViewById(R.id.update_my_info_text);
        my_pay_method_text = findViewById(R.id.my_pay_method_text);
        real_state_btn_setting = findViewById(R.id.real_state_btn_setting);
        update_zip_code_text = findViewById(R.id.update_zip_code_text);
        setting_camera_image = findViewById(R.id.setting_camera_image);
        noti_textview = findViewById(R.id.noti_textview);
        follow_layout = findViewById(R.id.follow_layout);

        //-------------------------------- set data into text view ------------------


        user_name_text_setting.setText(fname + " " + lname);
        email_text_setting.setText(email);
        phone_text_setting.setText(phone);

        if (user_type != null && user_type.equalsIgnoreCase(getString(R.string.agent))) {
            real_state_btn_setting.setVisibility(View.GONE);
            my_pay_method_text.setVisibility(View.VISIBLE);
        }

        Firebase.setAndroidContext(this);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("users");


        //------------------------------- on click ------------------------------

        follow_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), FallowActivity.class));
            }
        });

        setting_camera_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AddPropertyActivity.class));
            }
        });

        noti_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), NotificationActivity.class));
            }
        });

        real_state_btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AgentSignupActivity.class);
                startActivity(intent);
            }
        });

        setting_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout = "yes";
                LoginManager.getInstance().logOut();
                if (mAuth.getCurrentUser() != null && mAuth.getCurrentUser().getUid() != null) {
                    mFirebaseDatabase.child(mAuth.getCurrentUser().getUid()).child("credentials").child("token").setValue("123");
                    mAuth.getInstance().signOut();
                }
                sp = new MySharedPref();
                Toast.makeText(SettingActivity.this, R.string.logout, Toast.LENGTH_SHORT).show();
                finish();
                Intent i = new Intent(getApplicationContext(), WelcomeSliderActivity.class);
                i.addFlags(i.FLAG_ACTIVITY_CLEAR_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                sp.removeFromSharedPreferences(getApplicationContext(), "remove");
                sp.DeleteData(getApplicationContext());


            }
        });

        help_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), HelpActivity.class));
            }
        });

        about_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AboutActivity.class));
            }
        });

        privacy_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), PrivacyPolicyActivity.class));
            }
        });

        terms_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), TermsAndSetrviceActivity.class));
            }
        });

        update_zip_code_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), UpdateZipCodeActivity.class));
            }
        });


        update_my_info_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (user_type.equalsIgnoreCase("AGENT")) {
                    Intent intent = new Intent(getApplicationContext(), AgentUpdateInfoActivity.class);
                    intent.putExtra("fname", fname);
                    intent.putExtra("lname", lname);
                    intent.putExtra("email", email);
                    intent.putExtra("phone", phone);
                    intent.putExtra("image", image);
                    intent.putExtra("id_proof", id_proof);
                    intent.putExtra("broker", broker);
                    startActivity(intent);
                } else {

                    Intent intent = new Intent(getApplicationContext(), UpdateMyInfoActivity.class);
                    intent.putExtra("fname", fname);
                    intent.putExtra("lname", lname);
                    intent.putExtra("email", email);
                    intent.putExtra("phone", phone);
                    startActivity(intent);
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
}
