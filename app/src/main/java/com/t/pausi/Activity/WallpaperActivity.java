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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.t.pausi.Bean.AlertConnection;
import com.t.pausi.Bean.ConnectionDetector;
import com.t.pausi.Bean.MyLanguageSession;
import com.t.pausi.Bean.MySharedPref;
import com.t.pausi.Interface.Config;
import com.t.pausi.Interface.UserInterface;
import com.t.pausi.Pojo.GetWallpaperList;
import com.t.pausi.Pojo.GetWallpaperResponse;
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

public class WallpaperActivity extends AppCompatActivity {

    ImageView wallpaper_back;
    RecyclerView wallpaper_recycler_view;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    ProgressDialog progressDialog;
    MySharedPref sp;
    String ldata, logid, wall_id;
    MyLanguageSession myLanguageSession;
    private String language = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLanguageSession = new MyLanguageSession(this);
        language = myLanguageSession.getLanguage();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());
        setContentView(R.layout.activity_wallpaper);

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

        //-------------------------------- find view -------------------------

        wallpaper_back = findViewById(R.id.wallpaper_back);
        wallpaper_recycler_view = findViewById(R.id.wallpaper_recycler_view);

        //------------------------------------ get wallpaper --------------------------------

        if (isInternetPresent) {

            GetWallpaper_call();

        } else {
            AlertConnection.showAlertDialog(getApplicationContext(), getString(R.string.no_internal_connection),
                    getString(R.string.donothaveinternet), false);
        }

        //------------------------------- on click ----------------------------------

        wallpaper_back.setOnClickListener(new View.OnClickListener() {
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

    //------------------------------------ get wallpaper call -----------------------------------

    private void GetWallpaper_call() {
        progressDialog = new ProgressDialog(WallpaperActivity.this);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.show();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.Base_Url).client(client)
                .addConverterFactory(GsonConverterFactory.create(new Gson())).build();
        UserInterface signupInterface = retrofit.create(UserInterface.class);
        Call<ResponseBody> resultCall = signupInterface.all_wallpaper_list();
        resultCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    try {

                        String responedata = response.body().string();
                        Log.e("wallpaper response** ", " " + responedata);
                        JSONObject object = new JSONObject(responedata);
                        String error = object.getString("status");

                        if (error.equals("1")) {

                            Gson gson = new Gson();
                            GetWallpaperResponse getWallpaperResponse = gson.fromJson(responedata, GetWallpaperResponse.class);
                            WallpaperAdapter adaper = new WallpaperAdapter(WallpaperActivity.this, getWallpaperResponse.getResult());
                            wallpaper_recycler_view.setLayoutManager(new LinearLayoutManager(WallpaperActivity.this, LinearLayoutManager.VERTICAL, false));
                            wallpaper_recycler_view.setAdapter(adaper);
                            adaper.notifyDataSetChanged();

                        } else {
                            String message = object.getString("message");
                            Toast.makeText(getApplicationContext(), R.string.wallnotfound, Toast.LENGTH_SHORT).show();
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

    public class WallpaperAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private Context context;
        private LayoutInflater inflater;
        public List<GetWallpaperList> getWallpaperLists;
        RecyclerView recyclerView;
        View view;

        public WallpaperAdapter(Context context, List<GetWallpaperList> getWallpaperLists) {
            this.context = context;
            inflater = LayoutInflater.from(context);
            this.getWallpaperLists = getWallpaperLists;
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            view = inflater.inflate(R.layout.wellpaper_get_pojo_lay, parent, false);
            MyHolder holder = new MyHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

            System.out.println("Dattasize***" + getWallpaperLists.size());
            final MyHolder myHolder = (MyHolder) holder;
            if (getWallpaperLists.size() > 0) {

                Picasso.with(context).load(getWallpaperLists.get(position).getWallpaperImage()).into(myHolder.wallpaper_layout);

                myHolder.wallpaper_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        wall_id = getWallpaperLists.get(position).getId();
                        if (isInternetPresent) {
                            SetWallpaper_call();
                        } else {
                            AlertConnection.showAlertDialog(getApplicationContext(), getString(R.string.no_internal_connection),
                                    getString(R.string.donothaveinternet), false);
                        }
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return getWallpaperLists.size();
        }

        class MyHolder extends RecyclerView.ViewHolder {
            ImageView wallpaper_layout;

            // create constructor to get widget reference
            public MyHolder(View itemView) {
                super(itemView);
                wallpaper_layout = itemView.findViewById(R.id.wallpaper_layout);

            }

        }
    }

    //------------------------------------ set wallpaper call -----------------------------------

    private void SetWallpaper_call() {
        progressDialog = new ProgressDialog(WallpaperActivity.this);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.show();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.Base_Url).client(client)
                .addConverterFactory(GsonConverterFactory.create(new Gson())).build();
        UserInterface signupInterface = retrofit.create(UserInterface.class);
        Call<ResponseBody> resultCall = signupInterface.set_wallpaper(logid, wall_id);
        resultCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    try {

                        String responedata = response.body().string();
                        Log.e("set wallpaper response ", " " + responedata);
                        JSONObject object = new JSONObject(responedata);
                        String error = object.getString("status");

                        if (error.equals("1")) {

                            // Toast.makeText(WallpaperActivity.this, "", Toast.LENGTH_SHORT).show();

                            finish();

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
