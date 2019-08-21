package com.t.pausi.Activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.t.pausi.Bean.AlertConnection;
import com.t.pausi.Bean.ConnectionDetector;
import com.t.pausi.Bean.DataHolder;
import com.t.pausi.Bean.MyLanguageSession;
import com.t.pausi.Bean.MySharedPref;
import com.t.pausi.Fragment.HomeFragment;
import com.t.pausi.Interface.Config;
import com.t.pausi.Interface.UserInterface;
import com.t.pausi.Pojo.PropertyImage;
import com.t.pausi.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AgentEditProfileActivity extends AppCompatActivity {
    ImageView agent_edit_profile_back;
    TextView agent_deals_text, agent_year_text;
    Dialog dialog, dialog1;
    List<DataHolder> deals_list;
    List<DataHolder> year_list;
    String[] arrDeals = {"25+ Deals", "30+ Deals", "35+ Deals", "40+ Deals", "45+ Deals", "50+ Deals", "60+ Deals", "70+ Deals", "80+ Deals", "90+ Deals", "100+ Deals", "120+ Deals", "140+ Deals", "160+ Deals", "180+ Deals", "200+ Deals", "250+ Deals", "300+ Deals", "350+ Deals", "400+ Deals", "450+ Deals", "500+ Deals", "600+ Deals", "700+ Deals", "800+ Deals", "900+ Deals", "1000+ Deals"};
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    ProgressDialog progressDialog;
    MySharedPref sp;
    String ldata, logid;
    MyLanguageSession myLanguageSession;
    private String language = "";
    String broker = "", offer = "", close_deals = "", agent_since = "", fname = "", lname = "", phone = "",
            image = "", id_proof = "";
    EditText agent_edit_profile_broker_edittext, agent_edit_profile_affordable_edittext;
    Button agent_edit_profile_btn;
    MultipartBody.Part body, body1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLanguageSession = new MyLanguageSession(this);
        language = myLanguageSession.getLanguage();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());

        setContentView(R.layout.activity_agent_edit_profile);

        //--------------------- connection detector -----------------------------------

        cd = new ConnectionDetector(AgentEditProfileActivity.this);
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

        //------------------------------- find view -------------------------------------

        agent_edit_profile_back = findViewById(R.id.agent_edit_profile_back);
        agent_deals_text = findViewById(R.id.agent_deals_text);
        agent_year_text = findViewById(R.id.agent_year_text);
        agent_edit_profile_broker_edittext = findViewById(R.id.agent_edit_profile_broker_edittext);
        agent_edit_profile_affordable_edittext = findViewById(R.id.agent_edit_profile_affordable_edittext);
        agent_edit_profile_btn = findViewById(R.id.agent_edit_profile_btn);
        deals_list = new ArrayList<>();
        year_list = new ArrayList<>();

        //------------------------------------ get profile --------------------------------

        if (isInternetPresent) {
            GetProfile_call();
        } else {
            AlertConnection.showAlertDialog(getApplicationContext(), getString(R.string.no_internal_connection),
                    getString(R.string.donothaveinternet), false);
        }

        //------------------------------ add data into list -----------------------

        for (int i = 0; i < arrDeals.length; i++) {
            DataHolder dataHolder = new DataHolder();
            dataHolder.setDeals_name(arrDeals[i]);
            deals_list.add(dataHolder);
        }

        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 1950; i <= thisYear; i++) {
            DataHolder dataHolder = new DataHolder();
            dataHolder.setYear_name(getString(R.string.agentsince) + Integer.toString(i));
            year_list.add(dataHolder);
        }

        //------------------------------ on click ----------------------------------

        agent_deals_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new Dialog(AgentEditProfileActivity.this);
                dialog.setContentView(R.layout.agent_deals_layout);
                dialog.setCancelable(true);
                RecyclerView deals_recycler_view = dialog.findViewById(R.id.deals_recycler_view);

                DealsAdapter dealsAdapter = new DealsAdapter(AgentEditProfileActivity.this, deals_list);
                deals_recycler_view.setHasFixedSize(true);
                deals_recycler_view.setLayoutManager(new LinearLayoutManager(AgentEditProfileActivity.this, LinearLayoutManager.VERTICAL, false));
                deals_recycler_view.setAdapter(dealsAdapter);
                dealsAdapter.notifyDataSetChanged();

                dialog.show();
            }
        });


        agent_year_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog1 = new Dialog(AgentEditProfileActivity.this);
                dialog1.setContentView(R.layout.agent_years_layout);
                dialog1.setCancelable(true);
                RecyclerView agent_year_recycler_view = dialog1.findViewById(R.id.agent_year_recycler_view);

                YearAdapter yearAdapter = new YearAdapter(AgentEditProfileActivity.this, year_list);
                agent_year_recycler_view.setHasFixedSize(true);
                agent_year_recycler_view.setLayoutManager(new LinearLayoutManager(AgentEditProfileActivity.this, LinearLayoutManager.VERTICAL, false));
                agent_year_recycler_view.setAdapter(yearAdapter);
                yearAdapter.notifyDataSetChanged();

                dialog1.show();
            }
        });

        agent_edit_profile_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        agent_edit_profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                close_deals = agent_deals_text.getText().toString();
                offer = agent_edit_profile_affordable_edittext.getText().toString();
                broker = agent_edit_profile_broker_edittext.getText().toString();
                agent_since = agent_year_text.getText().toString();


                body = MultipartBody.Part.createFormData("image", image);
                body1 = MultipartBody.Part.createFormData("id_proof", id_proof);

                if (isInternetPresent) {
                    UpdateUser_call();
                } else {
                    AlertConnection.showAlertDialog(getApplicationContext(), getString(R.string.no_internal_connection),
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

    //----------------------------------- deals adapter ------------------------------------------

    public class DealsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private Context context;
        private LayoutInflater inflater;
        public List<DataHolder> stringList;
        RecyclerView recyclerView;
        View view;

        public DealsAdapter(Context context, List<DataHolder> stringList) {
            this.context = context;
            inflater = LayoutInflater.from(context);
            this.stringList = stringList;
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            view = inflater.inflate(R.layout.agent_deals_pojo_layout, parent, false);
            MyHolder holder = new MyHolder(view);
            return holder;
        }

        @SuppressLint("ResourceAsColor")
        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

            System.out.println("Dattasize***" + stringList.size());
            final MyHolder myHolder = (MyHolder) holder;
            if (stringList.size() > 0) {

                myHolder.agent_deals_dialog_text.setText(stringList.get(position).getDeals_name());

                myHolder.agent_deals_dialog_text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        agent_deals_text.setText(stringList.get(position).getDeals_name());
                        dialog.dismiss();
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return stringList.size();
        }

        class MyHolder extends RecyclerView.ViewHolder {
            TextView agent_deals_dialog_text;

            // create constructor to get widget reference
            public MyHolder(View itemView) {
                super(itemView);
                agent_deals_dialog_text = itemView.findViewById(R.id.agent_deals_dialog_text);

            }

        }
    }

    //----------------------------------- deals adapter ------------------------------------------

    public class YearAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private Context context;
        private LayoutInflater inflater;
        public List<DataHolder> stringList;
        RecyclerView recyclerView;
        View view;

        public YearAdapter(Context context, List<DataHolder> stringList) {
            this.context = context;
            inflater = LayoutInflater.from(context);
            this.stringList = stringList;
            Collections.reverse(stringList);
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            view = inflater.inflate(R.layout.agent_year_pojo_layout, parent, false);
            MyHolder holder = new MyHolder(view);
            return holder;
        }

        @SuppressLint("ResourceAsColor")
        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            Log.e("Dattasize ", "" + stringList.size());
            final MyHolder myHolder = (MyHolder) holder;
            if (stringList.size() > 0) {

                myHolder.agent_year_dialog_text.setText(stringList.get(position).getYear_name());
                myHolder.agent_year_dialog_text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        agent_year_text.setText(stringList.get(position).getYear_name());
                        dialog1.dismiss();
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return stringList.size();
        }

        class MyHolder extends RecyclerView.ViewHolder {
            TextView agent_year_dialog_text;

            // create constructor to get widget reference
            public MyHolder(View itemView) {
                super(itemView);
                agent_year_dialog_text = itemView.findViewById(R.id.agent_year_dialog_text);

            }
        }
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
                            fname = jsonObject.getString("first_name");
                            lname = jsonObject.getString("last_name");
                            phone = jsonObject.getString("mobile");
                            broker = jsonObject.getString("broker");
                            image = jsonObject.getString("image");
                            id_proof = jsonObject.getString("id_proof");
                            close_deals = jsonObject.getString("close_deals");
                            offer = jsonObject.getString("offer");
                            agent_since = jsonObject.getString("agent_since");


                            agent_deals_text.setText(close_deals);
                            agent_edit_profile_affordable_edittext.setText(offer);
                            agent_edit_profile_broker_edittext.setText(broker);
                            agent_year_text.setText(agent_since);


                            //Glide.with(getActivity()).load(wallpaper_image).error(R.drawable.profile_bg).into(wall_image_view);

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
                Toast.makeText(getApplicationContext(), R.string.server_problem, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //------------------------------------ UpdateUser_call call -----------------------------------

    private void UpdateUser_call() {
        progressDialog = new ProgressDialog(AgentEditProfileActivity.this);
        progressDialog.setMessage(getString(R.string.can_not_be_empty));
        progressDialog.show();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.Base_Url).client(client)
                .addConverterFactory(GsonConverterFactory.create(new Gson())).build();
        UserInterface signupInterface = retrofit.create(UserInterface.class);
        Call<ResponseBody> resultCall = signupInterface.user_update(logid, fname, lname, "", "AGENT", phone, broker, "", offer, close_deals, agent_since, body, body1);
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

                            Toast.makeText(AgentEditProfileActivity.this, getString(R.string.infoupdate), Toast.LENGTH_SHORT).show();

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
