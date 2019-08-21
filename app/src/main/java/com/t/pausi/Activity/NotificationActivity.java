package com.t.pausi.Activity;

import android.annotation.SuppressLint;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.t.pausi.Bean.AlertConnection;
import com.t.pausi.Bean.ConnectionDetector;
import com.t.pausi.Bean.MyLanguageSession;
import com.t.pausi.Bean.MySharedPref;
import com.t.pausi.Fragment.NewsFragment;
import com.t.pausi.Interface.Config;
import com.t.pausi.Interface.UserInterface;
import com.t.pausi.Pojo.NewsList;
import com.t.pausi.Pojo.NewsResposne;
import com.t.pausi.Pojo.NotificationList;
import com.t.pausi.Pojo.NotificationResponse;
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

public class NotificationActivity extends AppCompatActivity {

    RecyclerView notification_recycler_view;
    ImageView noti_back;
    ConnectionDetector cd;
    Boolean isInternetPresent = false;
    ProgressDialog progressDialog;
    MySharedPref sp;
    String ldata, logid, path = "", property_id, fav = "";
    MyLanguageSession myLanguageSession;
    private String language = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLanguageSession = new MyLanguageSession(this);
        language = myLanguageSession.getLanguage();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());

        setContentView(R.layout.activity_notification);

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

        //------------------------- find view ------------------------------

        notification_recycler_view = findViewById(R.id.notification_recycler_view);
        noti_back = findViewById(R.id.noti_back);

        //---------------------------- get news call -----------------------
        if (isInternetPresent) {
            GetNotiCalll();
        } else {
            AlertConnection.showAlertDialog(getApplicationContext(), getString(R.string.no_internal_connection),
                    getString(R.string.donothaveinternet), false);
        }

        //------------------------- on click ---------------------------

        noti_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
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

    //--------------------------- GetNotiCalll -----------------------------------

    private void GetNotiCalll() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.Base_Url).client(client)
                .addConverterFactory(GsonConverterFactory.create(new Gson())).build();
        UserInterface signupInterface = retrofit.create(UserInterface.class);
        Call<ResponseBody> resultCall = signupInterface.get_property_notification(logid);
        resultCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {

                        String responedata = response.body().string();
                        Log.e("noti response ", " " + responedata);
                        JSONObject object = new JSONObject(responedata);
                        String error = object.getString("status");

                        if (error.equals("1")) {


                            Gson gson = new Gson();
                            NotificationResponse newsResposne = gson.fromJson(responedata, NotificationResponse.class);
                            NotificationAdapter adaper = new NotificationAdapter(NotificationActivity.this, newsResposne.getResult());
                            notification_recycler_view.setLayoutManager(new LinearLayoutManager(NotificationActivity.this, LinearLayoutManager.VERTICAL, false));
                            notification_recycler_view.setAdapter(adaper);
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
                Toast.makeText(getApplicationContext(), getString(R.string.server_problem), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //-------------------------------- NotificationAdapter adapter ---------------------------

    public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private Context context;
        private LayoutInflater inflater;
        public List<NotificationList> notificationLists;
        RecyclerView recyclerView;
        View view;

        public NotificationAdapter(Context context, List<NotificationList> notificationLists) {
            this.context = context;
            inflater = LayoutInflater.from(context);
            this.notificationLists = notificationLists;
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            view = inflater.inflate(R.layout.notification_pojo_layout, parent, false);
            MyHolder holder = new MyHolder(view);
            return holder;
        }

        @SuppressLint("ResourceAsColor")
        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

            System.out.println("Dattasize***" + notificationLists.size());
            final MyHolder myHolder = (MyHolder) holder;
            if (notificationLists.size() > 0) {

                myHolder.noti_date_time_text.setText(notificationLists.get(position).getDateTime());
                myHolder.noti_tittle_text.setText(notificationLists.get(position).getFirstName() + " " + notificationLists.get(position).getLastName());
                Picasso.with(context).load(notificationLists.get(position).getImage()).into(myHolder.noti_imageview);

                if (notificationLists.get(position).getTips().equalsIgnoreCase("")) {
                    myHolder.noti_tips_text.setText(notificationLists.get(position).getNotification_key());
                } else {
                    myHolder.noti_tips_text.setText(notificationLists.get(position).getTips());
                    myHolder.noti_see_detail_btn.setVisibility(View.GONE);
                }


                myHolder.noti_see_detail_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), PropertyDetailActivity.class);
                        intent.putExtra("prop_id", notificationLists.get(position).getPropertyId());
                        startActivity(intent);
                    }
                });

            }
        }

        @Override
        public int getItemCount() {
            return notificationLists.size();
        }

        class MyHolder extends RecyclerView.ViewHolder {

            TextView noti_date_time_text, noti_tittle_text, noti_tips_text;
            CircleImageView noti_imageview;
            Button noti_see_detail_btn;

            // create constructor to get widget reference
            public MyHolder(View itemView) {
                super(itemView);
                noti_date_time_text = itemView.findViewById(R.id.noti_date_time_text);
                noti_imageview = itemView.findViewById(R.id.noti_imageview);
                noti_tittle_text = itemView.findViewById(R.id.noti_tittle_text);
                noti_see_detail_btn = itemView.findViewById(R.id.noti_see_detail_btn);
                noti_tips_text = itemView.findViewById(R.id.noti_tips_text);


            }

        }
    }
}
