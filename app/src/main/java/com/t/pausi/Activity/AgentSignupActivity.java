package com.t.pausi.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickCancel;
import com.vansuita.pickimage.listeners.IPickResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.t.pausi.Constant.Constants.SHARED_PREF;

public class AgentSignupActivity extends AppCompatActivity {
    String service_id;
    ImageView agent_signup_back, id_proof_imageview;
    CircleImageView agent_signup_image;
    String path = "", path2 = "";
    EditText agent_fname_edittext, agent_lanme_edittext, agent_broker_edittext, agent_email_edittext,
            agent_phone_edittext, agent_password_edittext;
    CheckBox confirmation_checkbox_agent_signup;
    Button agent_account_create_btn;
    TextView privacy_text_agent, terms_text_agent;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    String FId, fname, lname, broker, email, password, phone;
    GPSTracker tracker;
    double P_latitude, P_longitude;
    ProgressDialog progressDialog;
    MySharedPref sp;
    MultipartBody.Part body, body1;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    FirebaseAuth mAuth;
    String userId;
    MyLanguageSession myLanguageSession;
    private String language = "",image="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLanguageSession = new MyLanguageSession(this);
        language = myLanguageSession.getLanguage();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());
        setContentView(R.layout.activity_agent_signup);

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

        //--------------------------- find view ---------------------------

        agent_signup_back = findViewById(R.id.agent_signup_back);
        agent_signup_image = findViewById(R.id.agent_signup_image);
        id_proof_imageview = findViewById(R.id.id_proof_imageview);
        agent_fname_edittext = findViewById(R.id.agent_fname_edittext);
        agent_lanme_edittext = findViewById(R.id.agent_lanme_edittext);
        agent_broker_edittext = findViewById(R.id.agent_broker_edittext);
        agent_email_edittext = findViewById(R.id.agent_email_edittext);
        agent_phone_edittext = findViewById(R.id.agent_phone_edittext);
        agent_password_edittext = findViewById(R.id.agent_password_edittext);
        confirmation_checkbox_agent_signup = findViewById(R.id.confirmation_checkbox_agent_signup);
        agent_account_create_btn = findViewById(R.id.agent_account_create_btn);
        privacy_text_agent = findViewById(R.id.privacy_text_agent);
        terms_text_agent = findViewById(R.id.terms_text_agent);

        //------------------------------- get bundle data -----------------------------

        Bundle bundle = new Bundle();
        bundle = getIntent().getExtras();
        if (bundle != null) {
            service_id = bundle.getString("service_id");
            Log.e("service_id", service_id);
        }


        //--------------------------- on click --------------------------------

        agent_signup_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        privacy_text_agent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), PrivacyPolicyActivity.class));
            }
        });

        terms_text_agent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), TermsAndSetrviceActivity.class));
            }
        });

        agent_signup_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final PickImageDialog dialog = PickImageDialog.build(new PickSetup());
                dialog.setOnPickCancel(new IPickCancel() {
                    @Override
                    public void onCancelClick() {
                        dialog.dismiss();
                    }
                }).setOnPickResult(new IPickResult() {
                    @Override
                    public void onPickResult(PickResult r) {

                        if (r.getError() == null) {

                            path = r.getPath();
                            Glide.with(getApplicationContext()).load(path).into(agent_signup_image);

                        } else {
                            //Handle possible errors
                            //TODO: do what you have to do with r.getError();
                            Toast.makeText(AgentSignupActivity.this, r.getError().getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }

                }).show(AgentSignupActivity.this);
            }
        });

        id_proof_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final PickImageDialog dialog = PickImageDialog.build(new PickSetup());
                dialog.setOnPickCancel(new IPickCancel() {
                    @Override
                    public void onCancelClick() {
                        dialog.dismiss();
                    }
                }).setOnPickResult(new IPickResult() {
                    @Override
                    public void onPickResult(PickResult r) {

                        if (r.getError() == null) {

                            path2 = r.getPath();
                            Glide.with(getApplicationContext()).load(path2).into(id_proof_imageview);

                        } else {
                            //Handle possible errors
                            //TODO: do what you have to do with r.getError();
                            Toast.makeText(AgentSignupActivity.this, r.getError().getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }

                }).show(AgentSignupActivity.this);
            }
        });


        agent_account_create_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isInternetPresent) {
                    SharedPreferences pref = getApplicationContext().getSharedPreferences(SHARED_PREF, 0);
                    FId = pref.getString("regId", "");
                    Log.e("firebase id ", " " + FId);
                    if (agent_fname_edittext.getText().toString().trim().equalsIgnoreCase("")) {
                        agent_fname_edittext.setError(getString(R.string.can_not_be_empty));
                        agent_fname_edittext.requestFocus();
                    } else if (agent_lanme_edittext.getText().toString().trim().equalsIgnoreCase("")) {
                        agent_lanme_edittext.setError(getString(R.string.can_not_be_empty));
                        agent_lanme_edittext.requestFocus();
                    } else if (agent_broker_edittext.getText().toString().trim().equalsIgnoreCase("")) {
                        agent_broker_edittext.setError(getString(R.string.can_not_be_empty));
                        agent_broker_edittext.requestFocus();
                    } else if (agent_email_edittext.getText().toString().trim().equalsIgnoreCase("")) {
                        agent_email_edittext.setError(getString(R.string.can_not_be_empty));
                        agent_email_edittext.requestFocus();
                    } else if (agent_phone_edittext.getText().toString().trim().equalsIgnoreCase("")) {
                        agent_phone_edittext.setError(getString(R.string.can_not_be_empty));
                        agent_phone_edittext.requestFocus();
                    } else if (agent_password_edittext.getText().toString().trim().equalsIgnoreCase("")) {
                        agent_password_edittext.setError(getString(R.string.can_not_be_empty));
                        agent_password_edittext.requestFocus();
                    } else if (path.equalsIgnoreCase("")) {
                        Toast.makeText(AgentSignupActivity.this, R.string.plz_select_profile_image, Toast.LENGTH_SHORT).show();
                    } else if (path2.equalsIgnoreCase("")) {
                        Toast.makeText(AgentSignupActivity.this, R.string.pz_select_id_proof, Toast.LENGTH_SHORT).show();
                    } else if (confirmation_checkbox_agent_signup.isChecked() == false) {
                        Toast.makeText(AgentSignupActivity.this, R.string.pz_check_agreement, Toast.LENGTH_SHORT).show();
                    } else {
                        fname = agent_fname_edittext.getText().toString().trim();
                        lname = agent_lanme_edittext.getText().toString().trim();
                        broker = agent_broker_edittext.getText().toString().trim();
                        email = agent_email_edittext.getText().toString().trim();
                        phone = agent_phone_edittext.getText().toString().trim();
                        password = agent_password_edittext.getText().toString().trim();


                        boolean isemail = isValidMail(email);
                        boolean valid_mobile = isValidMobile(phone);

                        if (isemail == false) {

                            agent_email_edittext.setError(getString(R.string.invalidemail));
                            agent_email_edittext.requestFocus();

                        } else {

                            if (valid_mobile == false) {
                                agent_phone_edittext.setError(getString(R.string.notvalidnumber));
                                agent_phone_edittext.requestFocus();
                            } else {
                                path = resizeAndCompressImageBeforeSend(AgentSignupActivity.this, path, "agent_image");
                                path2 = resizeAndCompressImageBeforeSend(AgentSignupActivity.this, path2, "Id_Proof_image");
                                File file = new File(path);
                                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                                body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

                                File file2 = new File(path2);
                                RequestBody requestFile2 = RequestBody.create(MediaType.parse("multipart/form-data"), file2);
                                body1 = MultipartBody.Part.createFormData("id_proof", file2.getName(), requestFile2);

                                Signup_call();
                            }
                        }
                    }

                } else {
                    AlertConnection.showAlertDialog(AgentSignupActivity.this, getString(R.string.no_internal_connection),
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

    //------------------------------------ signup call -----------------------------------

    private void Signup_call() {
        progressDialog = new ProgressDialog(AgentSignupActivity.this);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.show();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.Base_Url).client(client)
                .addConverterFactory(GsonConverterFactory.create(new Gson())).build();
        UserInterface signupInterface = retrofit.create(UserInterface.class);
        Call<ResponseBody> resultCall = signupInterface.signup(fname, lname, email, phone, "", password, broker, service_id, "AGENT", FId, String.valueOf(P_latitude), String.valueOf(P_longitude), "Android", body, body1);
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
                                        Toast.makeText(AgentSignupActivity.this, R.string.account_created, Toast.LENGTH_LONG).show();
                                        sp = new MySharedPref();
                                        sp.saveData(getApplicationContext(), "ldata", ressult + "");
                                        Intent intent = new Intent(AgentSignupActivity.this, HomeActivity.class);
                                        startActivity(intent);
                                        finish();

                                    } else {
                                        Log.e("createUserWithEmail ", "" + task.getException());
                                        //Toast.makeText(AgentSignupActivity.this, R.string.could_not_create_account, Toast.LENGTH_SHORT).show();
                                        mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(AgentSignupActivity.this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (!task.isSuccessful()) {
                                                    Toast.makeText(AgentSignupActivity.this, R.string.signinerroer, Toast.LENGTH_SHORT).show();
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
                    Intent intent = new Intent(AgentSignupActivity.this, HomeActivity.class);
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

    private void createUser(String name, String email, String profile_pic, String token, String profile) {
        // TODO
        // In real apps this userId should be fetched
        // by implementing firebase auth
        User user = new User(name, email, profile_pic, token, profile);
        mFirebaseDatabase.child(userId).child("credentials").setValue(user);
        addUserChangeListener();
    }

    private void addUserChangeListener() {
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

    private String resizeAndCompressImageBeforeSend(Context context, String filePath, String fileName) {
        final int MAX_IMAGE_SIZE = 512 * 512; // max final file size in kilobytes

        // First decode with inJustDecodeBounds=true to check dimensions of image
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize(First we are going to resize the image to 800x800 image, in order to not have a big but very low quality image.
        //resizing the image will already reduce the file size, but after resizing we will check the file size and start to compress image
        options.inSampleSize = calculateInSampleSize(options, 800, 800);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        Bitmap bmpPic = BitmapFactory.decodeFile(filePath, options);


        int compressQuality = 100; // quality decreasing by 5 every loop.
        int streamLength;
        do {
            ByteArrayOutputStream bmpStream = new ByteArrayOutputStream();
            Log.d("compressBitmap", "Quality: " + compressQuality);
            bmpPic.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream);
            byte[] bmpPicByteArray = bmpStream.toByteArray();
            streamLength = bmpPicByteArray.length;
            compressQuality -= 5;
            Log.d("compressBitmap", "Size: " + streamLength / 1024 + " kb");
        } while (streamLength >= MAX_IMAGE_SIZE);

        try {
            //save the resized and compressed file to disk cache
            Log.d("compressBitmap", "cacheDir: " + context.getCacheDir());
            FileOutputStream bmpFile = new FileOutputStream(context.getCacheDir() + fileName);
            bmpPic.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpFile);
            bmpFile.flush();
            bmpFile.close();
        } catch (Exception e) {
            Log.e("compressBitmap", "Error on saving file");
        }
        //return the path of resized and compressed file
        return context.getCacheDir() + fileName;
    }


    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        String debugTag = "MemoryInformation";
        // Image nin islenmeden onceki genislik ve yuksekligi
        final int height = options.outHeight;
        final int width = options.outWidth;
        Log.d(debugTag, "image height: " + height + "---image width: " + width);
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        Log.d(debugTag, "inSampleSize: " + inSampleSize);
        return inSampleSize;
    }
}
