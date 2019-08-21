package com.t.pausi.Activity;

import android.app.ProgressDialog;
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
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickCancel;
import com.vansuita.pickimage.listeners.IPickResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

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

public class AgentUpdateInfoActivity extends AppCompatActivity {
    ImageView agent_update_back;
    String service_id = "", ldata, logid;
    ImageView agent_signup_back, agent_id_proof_imageview;
    CircleImageView agent_update_info_image;
    String path = "", path2 = "";
    EditText agent_update_fname_edittext, agent_update_lanme_edittext, agent_update_broker_edittext, agent_update_email_edittext,
            agent_update_phone_edittext, agent_update_password_edittext;
    Button agent_update_info_btn;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    ProgressDialog progressDialog;
    MySharedPref sp;
    MultipartBody.Part body, body1;
    String fname, lname, email, phone, user_type, image = "", id_proof = "", broker;
    MyLanguageSession myLanguageSession;
    private String language = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLanguageSession = new MyLanguageSession(this);
        language = myLanguageSession.getLanguage();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());

        setContentView(R.layout.activity_agent_update_info);

        //--------------------- connection detector -----------------------------------

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


        //---------------------------- find view ------------------------------------

        agent_update_back = findViewById(R.id.agent_update_back);
        agent_update_info_image = findViewById(R.id.agent_update_info_image);
        agent_update_fname_edittext = findViewById(R.id.agent_update_fname_edittext);
        agent_update_lanme_edittext = findViewById(R.id.agent_update_lanme_edittext);
        agent_id_proof_imageview = findViewById(R.id.agent_id_proof_imageview);
        agent_update_broker_edittext = findViewById(R.id.agent_update_broker_edittext);
        agent_update_email_edittext = findViewById(R.id.agent_update_email_edittext);
        agent_update_phone_edittext = findViewById(R.id.agent_update_phone_edittext);
        agent_update_password_edittext = findViewById(R.id.agent_update_password_edittext);
        agent_update_info_btn = findViewById(R.id.agent_update_info_btn);


        sp = new MySharedPref();
        service_id = sp.getData(getApplicationContext(), "service_id", "");
        if (service_id != null) {
            Log.e("service_id ", service_id);
        }


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


            agent_update_fname_edittext.setText(fname);
            agent_update_lanme_edittext.setText(lname);
            agent_update_broker_edittext.setText(broker);
            agent_update_phone_edittext.setText(phone);

            Glide.with(getApplicationContext()).load(image).into(agent_update_info_image);
            Glide.with(getApplicationContext()).load(id_proof).into(agent_id_proof_imageview);
        }


        //----------------------------- on click -------------------------------


        agent_update_info_image.setOnClickListener(new View.OnClickListener() {
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
                            Glide.with(getApplicationContext()).load(path).into(agent_update_info_image);

                        } else {
                            //Handle possible errors
                            //TODO: do what you have to do with r.getError();
                            Toast.makeText(AgentUpdateInfoActivity.this, r.getError().getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }

                }).show(AgentUpdateInfoActivity.this);
            }
        });

        agent_id_proof_imageview.setOnClickListener(new View.OnClickListener() {
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
                            Glide.with(getApplicationContext()).load(path2).into(agent_id_proof_imageview);

                        } else {
                            //Handle possible errors
                            //TODO: do what you have to do with r.getError();
                            Toast.makeText(AgentUpdateInfoActivity.this, r.getError().getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }

                }).show(AgentUpdateInfoActivity.this);
            }
        });

        agent_update_info_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fname = agent_update_fname_edittext.getText().toString();
                lname = agent_update_lanme_edittext.getText().toString();
                broker = agent_update_broker_edittext.getText().toString();
                phone = agent_update_phone_edittext.getText().toString();

                if (path == null || path.equalsIgnoreCase("")) {
                    body = MultipartBody.Part.createFormData("image", image);
                } else {
                    File file = new File(path);
                    RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                    body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
                }

                if (path2 == null || path2.equalsIgnoreCase("")) {
                    body1 = MultipartBody.Part.createFormData("id_proof", id_proof);
                } else {
                    File file2 = new File(path2);
                    RequestBody requestFile2 = RequestBody.create(MediaType.parse("multipart/form-data"), file2);
                    body1 = MultipartBody.Part.createFormData("id_proof", file2.getName(), requestFile2);
                }

                if (isInternetPresent) {

                    UpdateUser_call();

                } else {
                    AlertConnection.showAlertDialog(getApplicationContext(), getString(R.string.no_internal_connection),
                            getString(R.string.donothaveinternet), false);
                }

            }
        });

        agent_update_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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
        progressDialog = new ProgressDialog(AgentUpdateInfoActivity.this);
        progressDialog.setMessage(getString(R.string.can_not_be_empty));
        progressDialog.show();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.Base_Url).client(client)
                .addConverterFactory(GsonConverterFactory.create(new Gson())).build();
        UserInterface signupInterface = retrofit.create(UserInterface.class);
        Call<ResponseBody> resultCall = signupInterface.user_update(logid, fname, lname, "", "AGENT", phone, broker,"","","","", body, body1);
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

                            Toast.makeText(AgentUpdateInfoActivity.this, getString(R.string.infoupdate), Toast.LENGTH_SHORT).show();

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
