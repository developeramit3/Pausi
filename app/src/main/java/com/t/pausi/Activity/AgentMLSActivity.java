package com.t.pausi.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.t.pausi.Pojo.FavPropList;
import com.t.pausi.Pojo.FavPropResponse;
import com.t.pausi.Pojo.MLSList;
import com.t.pausi.Pojo.MLSResponse;
import com.t.pausi.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AgentMLSActivity extends AppCompatActivity {
    ImageView mls_back;
    RecyclerView mls_recycler_view;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    ProgressDialog progressDialog;
    MyLanguageSession myLanguageSession;
    private String language = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLanguageSession = new MyLanguageSession(this);
        language = myLanguageSession.getLanguage();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());
        setContentView(R.layout.activity_agent_mls);

        //--------------------- connection detector -----------------------------------

        cd = new ConnectionDetector(this);
        isInternetPresent = cd.isConnectingToInternet();
        super.onStart();


        //------------------------------------ find view -----------------------------

        mls_back = findViewById(R.id.mls_back);
        mls_recycler_view = findViewById(R.id.mls_recycler_view);

        //------------------------------------ get fav prop list --------------------------------

        if (isInternetPresent) {

            GetMLS_call();

        } else {
            AlertConnection.showAlertDialog(getApplicationContext(), getString(R.string.no_internal_connection),
                    getString(R.string.donothaveinternet), false);
        }

        //------------------------------------ on click ------------------------------

        mls_back.setOnClickListener(new View.OnClickListener() {
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

    //--------------------------- get mls list call -----------------------------------

    private void GetMLS_call() {
        progressDialog = new ProgressDialog(AgentMLSActivity.this);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.show();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.Base_Url).client(client)
                .addConverterFactory(GsonConverterFactory.create(new Gson())).build();
        UserInterface signupInterface = retrofit.create(UserInterface.class);
        Call<ResponseBody> resultCall = signupInterface.all_services();
        resultCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    try {

                        String responedata = response.body().string();
                        Log.e("mls response ", " " + responedata);
                        JSONObject object = new JSONObject(responedata);
                        String error = object.getString("status");

                        if (error.equals("1")) {

                            Gson gson = new Gson();
                            MLSResponse mlsResponse = gson.fromJson(responedata, MLSResponse.class);
                            MLSAdapter adaper = new MLSAdapter(AgentMLSActivity.this, mlsResponse.getResult());
                            mls_recycler_view.setLayoutManager(new LinearLayoutManager(AgentMLSActivity.this, LinearLayoutManager.VERTICAL, false));
                            mls_recycler_view.setAdapter(adaper);
                            adaper.notifyDataSetChanged();

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

    //--------------------------------------- wallpaper adapter ---------------------------

    public class MLSAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private Context context;
        private LayoutInflater inflater;
        public List<MLSList> mlsLists;
        RecyclerView recyclerView;
        View view;

        public MLSAdapter(Context context, List<MLSList> mlsLists) {
            this.context = context;
            inflater = LayoutInflater.from(context);
            this.mlsLists = mlsLists;
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            view = inflater.inflate(R.layout.mls_pojo_lay, parent, false);
            MyHolder holder = new MyHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

            System.out.println("Dattasize***" + mlsLists.size());
            final MyHolder myHolder = (MyHolder) holder;

            myHolder.lms_tittle_text.setText(mlsLists.get(position).getSortName());
            myHolder.lms_decrp_text.setText(mlsLists.get(position).getServiceName());
            myHolder.mls_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MySharedPref sp = new MySharedPref();
                    sp.saveData(context, "service_id", mlsLists.get(position).getId());
                    Intent intent = new Intent(getApplicationContext(), AgentSignupActivity.class);
                    intent.putExtra("service_id", mlsLists.get(position).getId());
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mlsLists.size();
        }

        class MyHolder extends RecyclerView.ViewHolder {
            TextView lms_tittle_text, lms_decrp_text;
            CardView mls_card;

            // create constructor to get widget reference
            public MyHolder(View itemView) {
                super(itemView);
                lms_tittle_text = itemView.findViewById(R.id.lms_tittle_text);
                lms_decrp_text = itemView.findViewById(R.id.lms_decrp_text);
                mls_card = itemView.findViewById(R.id.mls_card);
            }
        }
    }
}
