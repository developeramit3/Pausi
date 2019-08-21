package com.t.pausi.Activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.t.pausi.Bean.AlertConnection;
import com.t.pausi.Bean.ConnectionDetector;
import com.t.pausi.Bean.MyLanguageSession;
import com.t.pausi.Bean.MySharedPref;
import com.t.pausi.Interface.Config;
import com.t.pausi.Interface.UserInterface;
import com.t.pausi.Pojo.AllUserList;
import com.t.pausi.Pojo.AllUserResponse;
import com.t.pausi.Pojo.MyPropertyList;
import com.t.pausi.Pojo.MyPropertyResponse;
import com.t.pausi.Pojo.PropertyDetails;
import com.t.pausi.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AllUserListActivity extends AppCompatActivity {

    ImageView all_user_back;
    RecyclerView all_user_recycler_view;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    ProgressDialog progressDialog;
    MySharedPref sp;
    String ldata, logid, property_id = "", status = "";
    MyLanguageSession myLanguageSession;
    private String language = "";
    String price = "", property_status = "", assign_id = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLanguageSession = new MyLanguageSession(this);
        language = myLanguageSession.getLanguage();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());
        setContentView(R.layout.activity_all_user_list);

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

        Bundle bundle = new Bundle();
        bundle = getIntent().getExtras();
        if (bundle != null) {
            property_id = bundle.getString("property_id");
            property_status = bundle.getString("property_status");
        }

        all_user_back = findViewById(R.id.all_user_back);
        all_user_recycler_view = findViewById(R.id.all_user_recycler_view);

        //------------------------------------ my prop list --------------------------------

        if (isInternetPresent) {
            AllUser_call();
        } else {
            AlertConnection.showAlertDialog(getApplicationContext(), getString(R.string.no_internal_connection),
                    getString(R.string.donothaveinternet), false);
        }


        //----------------- on click ----------------

        all_user_back.setOnClickListener(new View.OnClickListener() {
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
        if (isInternetPresent) {
            AllUser_call();
        } else {
            AlertConnection.showAlertDialog(getApplicationContext(), getString(R.string.no_internal_connection),
                    getString(R.string.donothaveinternet), false);
        }

    }

    //---------------------------  AllUser_call call -----------------------------------

    private void AllUser_call() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.Base_Url).client(client)
                .addConverterFactory(GsonConverterFactory.create(new Gson())).build();
        UserInterface signupInterface = retrofit.create(UserInterface.class);
        Call<ResponseBody> resultCall = signupInterface.get_all_user_list();
        resultCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                //progressDialog.dismiss();
                if (response.isSuccessful()) {
                    try {

                        String responedata = response.body().string();
                        Log.e("all user response ", " " + responedata);
                        JSONObject object = new JSONObject(responedata);
                        String error = object.getString("status");

                        if (error.equals("1")) {

                            Gson gson = new Gson();
                            AllUserResponse allUserResponse = gson.fromJson(responedata, AllUserResponse.class);
                            AllUserAdapter adaper = new AllUserAdapter(AllUserListActivity.this, allUserResponse.getResult());
                            all_user_recycler_view.setLayoutManager(new LinearLayoutManager(AllUserListActivity.this, LinearLayoutManager.VERTICAL, false));
                            all_user_recycler_view.setHasFixedSize(true);
                            all_user_recycler_view.setAdapter(adaper);
                            adaper.notifyDataSetChanged();

                        } else {
                            String message = object.getString("message");
                            Toast.makeText(getApplicationContext(), R.string.nouserfound, Toast.LENGTH_SHORT).show();
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
                //progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), getString(R.string.server_problem), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //--------------------------------------- MyPropAdapter adapter ---------------------------

    public class AllUserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private Context context;
        private LayoutInflater inflater;
        public List<AllUserList> allUserLists;
        View view;

        public AllUserAdapter(Context context, List<AllUserList> allUserLists) {
            this.context = context;
            inflater = LayoutInflater.from(context);
            this.allUserLists = allUserLists;
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            view = inflater.inflate(R.layout.all_user_pojo_layout, parent, false);
            MyHolder holder = new MyHolder(view);
            return holder;
        }

        @SuppressLint("ResourceAsColor")
        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

            System.out.println("Dattasize***" + allUserLists.size());
            final MyHolder myHolder = (MyHolder) holder;
            if (allUserLists.size() > 0) {

                if (allUserLists.get(position).getFirstName() != null && allUserLists.get(position).getLastName() != null) {

                    myHolder.all_user_pojo_name_text.setText(allUserLists.get(position).getFirstName() + " " + allUserLists.get(position).getLastName());

                    if (allUserLists.get(position).getImage() != null) {
                        Picasso.with(context).load(allUserLists.get(position).getImage()).placeholder(R.drawable.user).into(myHolder.all_user_pojo_imageview);
                    }
                }

                myHolder.all_user_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        assign_id = allUserLists.get(position).getId();

                        final Dialog dialog = new Dialog(context);
                        dialog.setContentView(R.layout.prop_sold_final_dialog);

                        final EditText sold_price_edittext = dialog.findViewById(R.id.sold_price_edittext);
                        Button sold_price_btn = dialog.findViewById(R.id.sold_price_btn);

                        sold_price_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                price = sold_price_edittext.getText().toString();
                                if (isInternetPresent) {
                                    ChangePropertyStatus();
                                } else {

                                    AlertConnection.showAlertDialog(getApplicationContext(), getString(R.string.no_internal_connection),
                                            getString(R.string.donothaveinternet), false);
                                }

                                dialog.dismiss();
                            }
                        });

                        dialog.show();

                    }
                });

            }
        }

        @Override
        public int getItemCount() {
            return allUserLists.size();
        }

        class MyHolder extends RecyclerView.ViewHolder {
            CircleImageView all_user_pojo_imageview;
            TextView all_user_pojo_name_text;
            LinearLayout all_user_layout;

            // create constructor to get widget reference
            public MyHolder(View itemView) {
                super(itemView);
                all_user_pojo_imageview = itemView.findViewById(R.id.all_user_pojo_imageview);
                all_user_pojo_name_text = itemView.findViewById(R.id.all_user_pojo_name_text);
                all_user_layout = itemView.findViewById(R.id.all_user_layout);

            }

        }
    }

    private void ChangePropertyStatus() {
        progressDialog = new ProgressDialog(AllUserListActivity.this);
        progressDialog.setMessage("Please wait..");
        progressDialog.show();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.Base_Url).client(client)
                .addConverterFactory(GsonConverterFactory.create(new Gson())).build();
        UserInterface signupInterface = retrofit.create(UserInterface.class);
        Call<ResponseBody> resultCall = signupInterface.update_property_status(logid, property_id, "", assign_id, price, property_status);
        resultCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    try {

                        String responedata = response.body().string();
                        Log.e("update response ", " " + responedata);
                        JSONObject object = new JSONObject(responedata);
                        String error = object.getString("status");

                        if (error.equals("1")) {

                            Toast.makeText(AllUserListActivity.this, R.string.propertystatusupdated, Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(new Intent(getApplicationContext(), MyPropertyActivity.class));

                        } else {
                            String message = object.getString("result");
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
