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
import android.widget.TextView;
import android.widget.Toast;

import com.t.pausi.Bean.ConnectionDetector;
import com.t.pausi.Bean.GPSTracker;
import com.t.pausi.Bean.MyLanguageSession;
import com.t.pausi.Bean.MySharedPref;
import com.t.pausi.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MultipartBody;

import static com.t.pausi.Constant.Constants.SHARED_PREF;

public class SignUpActivity extends AppCompatActivity {
    EditText signup_email_edittext, signup_password_edittext, signup_cpassword_edittext;
    Boolean isInternetPresent = false;
    Button signup_btn, signup_real_state_btn;
    String email, password;
    TextView login_textview;
    MyLanguageSession myLanguageSession;
    private String language = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLanguageSession = new MyLanguageSession(this);
        language = myLanguageSession.getLanguage();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());
        setContentView(R.layout.activity_sign_up);

        //----------------------------- find view -------------------------------

        signup_email_edittext = findViewById(R.id.signup_email_edittext);
        signup_password_edittext = findViewById(R.id.signup_password_edittext);
        signup_cpassword_edittext = findViewById(R.id.signup_cpassword_edittext);
        signup_btn = findViewById(R.id.signup_btn);
        signup_real_state_btn = findViewById(R.id.signup_real_state_btn);
        login_textview = findViewById(R.id.login_textview);


        //------------------------------- on click -------------------------------

        signup_real_state_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AgentMLSActivity.class));
            }
        });

        login_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (signup_email_edittext.getText().toString().trim().equalsIgnoreCase("")) {
                    signup_email_edittext.setError(getString(R.string.can_not_be_empty));
                    signup_email_edittext.requestFocus();
                } else if (signup_password_edittext.getText().toString().trim().equalsIgnoreCase("")) {
                    signup_password_edittext.setError(getString(R.string.can_not_be_empty));
                    signup_password_edittext.requestFocus();

                } else if (signup_cpassword_edittext.getText().toString().trim().equalsIgnoreCase("")) {
                    signup_cpassword_edittext.setError(getString(R.string.can_not_be_empty));
                    signup_cpassword_edittext.requestFocus();

                } else if (!signup_password_edittext.getText().toString().equalsIgnoreCase(signup_cpassword_edittext.getText().toString())) {
                    Toast.makeText(SignUpActivity.this, R.string.passmismatch, Toast.LENGTH_SHORT).show();
                } else {

                    email = signup_email_edittext.getText().toString().trim();
                    boolean isemail = isValidMail(email);
                    if (isemail == false) {
                        signup_email_edittext.setError(getString(R.string.invalidemail));
                        signup_email_edittext.requestFocus();
                    } else {
                        email = signup_email_edittext.getText().toString().trim();
                        password = signup_password_edittext.getText().toString().trim();
                        Intent intent = new Intent(getApplicationContext(), SignUpTwoActivity.class);
                        intent.putExtra("email", email);
                        intent.putExtra("password", password);
                        startActivity(intent);
                    }

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

    private boolean isValidMail(String email) {
        boolean check;
        Pattern p;
        Matcher m;

        String EMAIL_STRING = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        p = Pattern.compile(EMAIL_STRING);

        m = p.matcher(email);
        check = m.matches();

        if (!check) {

        }
        return check;
    }
}
