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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.t.pausi.Bean.AlertConnection;
import com.t.pausi.Bean.ConnectionDetector;
import com.t.pausi.Bean.DataHolder;
import com.t.pausi.Bean.MyLanguageSession;
import com.t.pausi.Bean.MySharedPref;
import com.t.pausi.Bean.User;
import com.t.pausi.Fragment.NewsFragment;
import com.t.pausi.Interface.Config;
import com.t.pausi.Interface.UserInterface;
import com.t.pausi.Pojo.FavPropList;
import com.t.pausi.Pojo.FavPropResponse;
import com.t.pausi.Pojo.GetWallpaperList;
import com.t.pausi.Pojo.GetWallpaperResponse;
import com.t.pausi.Pojo.PropertyDetails;
import com.t.pausi.Pojo.PropertyImage;
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

public class GetFavoriteActivity extends AppCompatActivity {
    ImageView fav_back;
    RecyclerView get_favorite_recycler_view;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    ProgressDialog progressDialog;
    MySharedPref sp;
    String ldata, logid, property_id;
    Dialog dialog;
    private FirebaseAuth mAuth;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    MyLanguageSession myLanguageSession;
    private String language = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLanguageSession = new MyLanguageSession(this);
        language = myLanguageSession.getLanguage();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());
        setContentView(R.layout.activity_get_favorite);

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


        //--------------------- find view ---------------------------------

        fav_back = findViewById(R.id.fav_back);
        get_favorite_recycler_view = findViewById(R.id.get_favorite_recycler_view);

        Firebase.setAndroidContext(this);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("users");

        //------------------------------------ get fav prop list --------------------------------

        if (isInternetPresent) {

            GetFavProp_call();

        } else {
            AlertConnection.showAlertDialog(getApplicationContext(), getString(R.string.no_internal_connection),
                    getString(R.string.donothaveinternet), false);
        }

        //--------------------- on click -----------------------------

        fav_back.setOnClickListener(new View.OnClickListener() {
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

    //--------------------------- get av prop list call -----------------------------------

    private void GetFavProp_call() {
        progressDialog = new ProgressDialog(GetFavoriteActivity.this);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.show();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.Base_Url).client(client)
                .addConverterFactory(GsonConverterFactory.create(new Gson())).build();
        UserInterface signupInterface = retrofit.create(UserInterface.class);
        Call<ResponseBody> resultCall = signupInterface.get_favourite_property_lists(logid);
        resultCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    try {

                        String responedata = response.body().string();
                        Log.e("get_fav_prop response ", " " + responedata);
                        JSONObject object = new JSONObject(responedata);
                        String error = object.getString("status");

                        if (error.equals("1")) {

                            Gson gson = new Gson();
                            FavPropResponse favPropResponse = gson.fromJson(responedata, FavPropResponse.class);
                            FavPropAdapter adaper = new FavPropAdapter(GetFavoriteActivity.this, favPropResponse.getResult());
                            get_favorite_recycler_view.setLayoutManager(new LinearLayoutManager(GetFavoriteActivity.this, LinearLayoutManager.VERTICAL, false));
                            get_favorite_recycler_view.setHasFixedSize(true);
                            get_favorite_recycler_view.setAdapter(adaper);
                            get_favorite_recycler_view.setNestedScrollingEnabled(false);
                            adaper.notifyDataSetChanged();

                        } else {
                            String message = object.getString("message");
                            Toast.makeText(getApplicationContext(), R.string.nofavproperty, Toast.LENGTH_SHORT).show();
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

    public class FavPropAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private Context context;
        private LayoutInflater inflater;
        public List<FavPropList> favPropLists;
        RecyclerView recyclerView;
        View view;

        public FavPropAdapter(Context context, List<FavPropList> favPropLists) {
            this.context = context;
            inflater = LayoutInflater.from(context);
            this.favPropLists = favPropLists;
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            view = inflater.inflate(R.layout.fav_prop_pojo_lay, parent, false);
            MyHolder holder = new MyHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

            System.out.println("Dattasize***" + favPropLists.size());
            final MyHolder myHolder = (MyHolder) holder;
            if (favPropLists.size() > 0) {


                myHolder.fav_prop_price_text.setText(favPropLists.get(position).getPropertyDetails().getPropertyPrice() + " XAF");
                myHolder.fav_sale_type_text.setText(favPropLists.get(position).getPropertyDetails().getSaleType());
                //Picasso.with(context).load(favPropLists.get(position).getPropertyDetails().getPropertyImages().get(0).getPropertyImage()).into(myHolder.get_fav_imageview);

                myHolder.fav_more_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog = new Dialog(GetFavoriteActivity.this);
                        dialog.setContentView(R.layout.fav_more_dialog);
                        dialog.setCancelable(true);

                        TextView fav_share_text = dialog.findViewById(R.id.fav_share_text);
                        TextView fav_unfav_text = dialog.findViewById(R.id.fav_unfav_text);
                        TextView fav_send_in_message_text = dialog.findViewById(R.id.fav_send_in_message_text);


                        fav_send_in_message_text.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                                mFirebaseDatabase.orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                String key = ds.getKey();
                                                User user = ds.child("credentials").getValue(User.class);
                                                String email = user.email;



                                                    if (email != null && favPropLists.get(position).getPropertyDetails().getAgentDetails().getEmail() != null && favPropLists.get(position).getPropertyDetails().getAgentDetails().getEmail().equalsIgnoreCase(email)) {
                                                        String agent_id = key;
                                                        if (key != null && mAuth.getCurrentUser().getUid() != null && key.equalsIgnoreCase(mAuth.getCurrentUser().getUid())) {

                                                            Toast.makeText(context, getString(R.string.youcantnotchatown), Toast.LENGTH_SHORT).show();

                                                        } else {
                                                        Intent intent = new Intent(context, ChatActivity.class);
                                                        intent.putExtra("receiver_id", agent_id);
                                                        intent.putExtra("username", favPropLists.get(position).getPropertyDetails().getAgentDetails().getFirstName() + " " + favPropLists.get(position).getPropertyDetails().getAgentDetails().getLastName());
                                                        intent.putExtra("userprofile", favPropLists.get(position).getPropertyDetails().getAgentDetails().getImage());
                                                        intent.putExtra("property_id", favPropLists.get(position).getPropertyDetails().getId());
                                                        startActivity(intent);
                                                    }
                                                }
                                            }

                                        } else {

                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        });

                        fav_share_text.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                                shareTextUrl();
                            }
                        });

                        fav_unfav_text.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                                property_id = favPropLists.get(position).getPropertyId();
                                if (isInternetPresent) {
                                    AddToFav();

                                } else {
                                    AlertConnection.showAlertDialog(getApplicationContext(), getString(R.string.no_internal_connection),
                                            getString(R.string.donothaveinternet), false);
                                }
                            }
                        });

                        dialog.show();
                    }
                });

                if (favPropLists.get(position).getPropertyDetails().getPropertyImages() != null && !favPropLists.get(position).getPropertyDetails().getPropertyImages().isEmpty()) {

                    SectionListDataAdapter itemListDataAdapter = new SectionListDataAdapter(context, favPropLists.get(position).getPropertyDetails().getPropertyImages(), favPropLists.get(position).getPropertyDetails());
                    myHolder.news_secand_recycler_view.setHasFixedSize(true);
                    myHolder.news_secand_recycler_view.setItemViewCacheSize(20);
                    myHolder.news_secand_recycler_view.setDrawingCacheEnabled(true);
                    myHolder.news_secand_recycler_view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                    myHolder.news_secand_recycler_view.getRecycledViewPool().setMaxRecycledViews(0, 0);
                    myHolder.news_secand_recycler_view.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                    myHolder.news_secand_recycler_view.setAdapter(itemListDataAdapter);
                    itemListDataAdapter.notifyDataSetChanged();


                }
            }
        }

        @Override
        public int getItemCount() {
            return favPropLists.size();
        }

        class MyHolder extends RecyclerView.ViewHolder {
            ImageView get_fav_imageview, fav_more_image;
            TextView fav_prop_price_text, fav_sale_type_text;
            RecyclerView news_secand_recycler_view;

            // create constructor to get widget reference
            public MyHolder(View itemView) {
                super(itemView);

                fav_prop_price_text = itemView.findViewById(R.id.fav_prop_price_text);
                fav_sale_type_text = itemView.findViewById(R.id.fav_sale_type_text);
                fav_more_image = itemView.findViewById(R.id.fav_more_image);
                news_secand_recycler_view = itemView.findViewById(R.id.news_secand_recycler_view);

            }
        }
    }


    public class SectionListDataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private Context context;
        private LayoutInflater inflater;
        public List<PropertyImage> dataHolderList;
        public List<DataHolder> ssdataHolderList;
        RecyclerView recyclerView;
        PropertyDetails propertyDetails;
        View view;


        public SectionListDataAdapter(Context context, List<PropertyImage> dataHolderList, PropertyDetails propertyDetails) {
            this.context = context;
            inflater = LayoutInflater.from(context);
            this.dataHolderList = dataHolderList;
            this.propertyDetails = propertyDetails;
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            view = inflater.inflate(R.layout.home_prop_pojo_lay2, parent, false);
            MyHolder holder = new MyHolder(view);
            return holder;
        }

        @SuppressLint("ResourceAsColor")
        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            System.out.println("Dattasize***" + dataHolderList.size());
            final MyHolder myHolder = (MyHolder) holder;
            if (dataHolderList.size() > 0) {


                if (position == dataHolderList.size()) {
                    myHolder.home_property_imageview.setVisibility(View.GONE);
                    myHolder.home_prop_detail_layout.setVisibility(View.VISIBLE);
                    myHolder.home_property_price_text2.setText(propertyDetails.getPropertyPrice() + getString(R.string.xaf));
                    myHolder.home_property_sel_type_text2.setText(propertyDetails.getSaleType());
                    myHolder.home_property_address_text2.setText(propertyDetails.getAddress());
                    myHolder.home_property_bads_text2.setText(propertyDetails.getBeds() + getString(R.string.beds));
                    myHolder.home_property_baths_text2.setText(propertyDetails.getBaths() + getString(R.string.baths));
                    myHolder.home_property_acres_text2.setText(propertyDetails.getAcreArea() + getString(R.string.acres));
                    myHolder.home_property_sqft_text2.setText(propertyDetails.getSqFeet() + getString(R.string.m2));

                    myHolder.more_info_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getApplicationContext(), PropertyDetailActivity.class);
                            intent.putExtra("prop_id", propertyDetails.getId());
                            startActivity(intent);
                        }
                    });

                } else {

                    Picasso.with(context).load(dataHolderList.get(position).getPropertyImage()).placeholder(R.drawable.dummy_img).into(myHolder.home_property_imageview);
                   /* RequestOptions requestOptions = new RequestOptions();
                    requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
                    Glide.with(context)
                            .load(dataHolderList.get(position).getPropertyImage()).apply(requestOptions)
                            .into(myHolder.home_property_imageview);*/
                }

//                else {
//                    myHolder.home_property_imageview.setVisibility(View.GONE);
//                    myHolder.home_prop_detail_layout.setVisibility(View.VISIBLE);
//                }


            }

            myHolder.home_property_imageview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), PropertyDetailActivity.class);
                    intent.putExtra("prop_id", dataHolderList.get(position).getPropertyId());
                    startActivity(intent);
                }
            });
        }


        @Override
        public int getItemCount() {
            return dataHolderList.size() + 1;
        }


        class MyHolder extends RecyclerView.ViewHolder {
            ImageView home_property_imageview;
            LinearLayout home_slide_lay;
            LinearLayout home_prop_detail_layout;
            TextView home_property_price_text2, home_property_sel_type_text2, home_property_address_text2,
                    home_property_bads_text2, home_property_baths_text2, home_property_acres_text2,
                    home_property_sqft_text2;
            Button more_info_btn;

            // create constructor to get widget reference
            public MyHolder(View itemView) {
                super(itemView);
                home_property_imageview = itemView.findViewById(R.id.home_property_imageview);
                home_slide_lay = itemView.findViewById(R.id.home_slide_lay);
                home_prop_detail_layout = itemView.findViewById(R.id.home_prop_detail_layout);
                home_property_price_text2 = itemView.findViewById(R.id.home_property_price_text2);
                home_property_sel_type_text2 = itemView.findViewById(R.id.home_property_sel_type_text2);
                home_property_address_text2 = itemView.findViewById(R.id.home_property_address_text2);
                home_property_bads_text2 = itemView.findViewById(R.id.home_property_bads_text2);
                home_property_baths_text2 = itemView.findViewById(R.id.home_property_baths_text2);
                home_property_acres_text2 = itemView.findViewById(R.id.home_property_acres_text2);
                home_property_sqft_text2 = itemView.findViewById(R.id.home_property_sqft_text2);
                more_info_btn = itemView.findViewById(R.id.more_info_btn);
            }


        }
    }


    private void shareTextUrl() {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        // Add data to the intent, the receiving app will decide
        // what to do with it.
        share.putExtra(Intent.EXTRA_SUBJECT, "Pausi App");
        share.putExtra(Intent.EXTRA_TEXT, "Welcome to Pausi! You can download app from Play Store:- https://play.google.com/store/apps/details?id=com.t.pausi");
        startActivity(Intent.createChooser(share, "Share link!"));
    }

    //------------------------------------ add to favourite call -----------------------------------

    private void AddToFav() {
        progressDialog = new ProgressDialog(GetFavoriteActivity.this);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.show();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.Base_Url).client(client)
                .addConverterFactory(GsonConverterFactory.create(new Gson())).build();
        UserInterface signupInterface = retrofit.create(UserInterface.class);
        Call<ResponseBody> resultCall = signupInterface.add_to_favourite(logid, property_id);
        resultCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    try {

                        String responedata = response.body().string();
                        Log.e("addtofav response* ", " " + responedata);
                        JSONObject object = new JSONObject(responedata);
                        String error = object.getString("status");

                        if (error.equals("1")) {

                            Toast.makeText(getApplicationContext(), R.string.unfavourite, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), GetFavoriteActivity.class));

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
