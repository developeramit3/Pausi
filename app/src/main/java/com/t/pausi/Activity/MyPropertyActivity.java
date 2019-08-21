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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.t.pausi.Bean.AlertConnection;
import com.t.pausi.Bean.ConnectionDetector;
import com.t.pausi.Bean.DataHolder;
import com.t.pausi.Bean.MyLanguageSession;
import com.t.pausi.Bean.MySharedPref;
import com.t.pausi.Fragment.NewsFragment;
import com.t.pausi.Interface.Config;
import com.t.pausi.Interface.UserInterface;
import com.t.pausi.Pojo.FavPropList;
import com.t.pausi.Pojo.FavPropResponse;
import com.t.pausi.Pojo.MyPropertyList;
import com.t.pausi.Pojo.MyPropertyResponse;
import com.t.pausi.Pojo.NewsList;
import com.t.pausi.Pojo.NewsResposne;
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

public class MyPropertyActivity extends AppCompatActivity {
    RecyclerView my_property_recycler_view;
    ImageView my_property_back;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    ProgressDialog progressDialog;
    MySharedPref sp;
    String ldata, logid, property_id, status = "";
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

        setContentView(R.layout.activity_my_property);

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

        my_property_recycler_view = findViewById(R.id.my_property_recycler_view);
        my_property_back = findViewById(R.id.my_property_back);

        Firebase.setAndroidContext(this);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("users");

        //------------------------------------ my prop list --------------------------------

        if (isInternetPresent) {
            My_Property_call();
        } else {
            AlertConnection.showAlertDialog(getApplicationContext(), getString(R.string.no_internal_connection),
                    getString(R.string.donothaveinternet), false);
        }

        my_property_back.setOnClickListener(new View.OnClickListener() {
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
            My_Property_call();
        } else {
            AlertConnection.showAlertDialog(getApplicationContext(), getString(R.string.no_internal_connection),
                    getString(R.string.donothaveinternet), false);
        }

    }

    //--------------------------- my prop call -----------------------------------

    private void My_Property_call() {
//        progressDialog = new ProgressDialog(MyPropertyActivity.this);
//        progressDialog.setMessage(getString(R.string.please_wait));
//        progressDialog.show();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.Base_Url).client(client)
                .addConverterFactory(GsonConverterFactory.create(new Gson())).build();
        UserInterface signupInterface = retrofit.create(UserInterface.class);
        Call<ResponseBody> resultCall = signupInterface.my_listing_list(logid);
        resultCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                //progressDialog.dismiss();
                if (response.isSuccessful()) {
                    try {

                        String responedata = response.body().string();
                        Log.e("my prop response ", " " + responedata);
                        JSONObject object = new JSONObject(responedata);
                        String error = object.getString("status");

                        if (error.equals("1")) {

                            Gson gson = new Gson();
                            MyPropertyResponse newsResposne = gson.fromJson(responedata, MyPropertyResponse.class);
                            MyPropAdapter adaper = new MyPropAdapter(MyPropertyActivity.this, newsResposne.getResult());
                            my_property_recycler_view.setLayoutManager(new LinearLayoutManager(MyPropertyActivity.this, LinearLayoutManager.VERTICAL, false));
                            my_property_recycler_view.setHasFixedSize(true);
                            my_property_recycler_view.setAdapter(adaper);
                            adaper.notifyDataSetChanged();

                        } else {
                            String message = object.getString("message");
                            Toast.makeText(getApplicationContext(), R.string.property_not_found, Toast.LENGTH_SHORT).show();
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

    public class MyPropAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private Context context;
        private LayoutInflater inflater;
        public List<MyPropertyList> myPropertyLists;
        PropertyDetails propertyDetails;
        RecyclerView recyclerView;
        View view;

        public MyPropAdapter(Context context, List<MyPropertyList> myPropertyLists) {
            this.context = context;
            inflater = LayoutInflater.from(context);
            this.myPropertyLists = myPropertyLists;
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            view = inflater.inflate(R.layout.my_prop_pojo_lay, parent, false);
            MyHolder holder = new MyHolder(view);
            return holder;
        }

        @SuppressLint("ResourceAsColor")
        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

            System.out.println("Dattasize***" + myPropertyLists.size());
            final MyHolder myHolder = (MyHolder) holder;
            if (myPropertyLists.size() > 0) {

                myHolder.news_property_price.setText(myPropertyLists.get(position).getPropertyPrice() + getString(R.string.xaf));
                myHolder.news_baths.setText(myPropertyLists.get(position).getBaths() + getString(R.string.baths));
                myHolder.news_beds_texts.setText(myPropertyLists.get(position).getBeds() + getString(R.string.beds));
                myHolder.news_property_address.setText(myPropertyLists.get(position).getAddress());
                myHolder.property_add_or_new_text.setText(myPropertyLists.get(position).getStatus());

                myHolder.edit_prop_lay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), EditPropertyActivity.class);
                        intent.putExtra("prop_id", myPropertyLists.get(position).getId());
                        startActivity(intent);
                    }
                });
//                if (!myPropertyLists.get(position).getStatus().equalsIgnoreCase("New Property Added")) {
//                    myHolder.property_add_or_new_text.setBackgroundColor(R.color.colorAccent);
//                    myHolder.property_add_or_new_text.setText(myPropertyLists.get(position).getStatus());
//                }

                propertyDetails = new PropertyDetails();
                propertyDetails.setId(myPropertyLists.get(position).getId());
                propertyDetails.setAcreArea(myPropertyLists.get(position).getAcreArea());
                propertyDetails.setAddress(myPropertyLists.get(position).getAddress());
                propertyDetails.setBaths(myPropertyLists.get(position).getBaths());
                propertyDetails.setBeds(myPropertyLists.get(position).getBeds());
                propertyDetails.setPropertyPrice(myPropertyLists.get(position).getPropertyPrice());
                propertyDetails.setSaleType(myPropertyLists.get(position).getSaleType());
                propertyDetails.setSqFeet(myPropertyLists.get(position).getSqFeet());

                if (myPropertyLists.get(position).getPropertyImages() != null && !myPropertyLists.get(position).getPropertyImages().isEmpty()) {

                    SectionListDataAdapter itemListDataAdapter = new SectionListDataAdapter(context, myPropertyLists.get(position).getPropertyImages(), propertyDetails);
                    myHolder.news_secand_recycler_view.setHasFixedSize(true);
                    myHolder.news_secand_recycler_view.setItemViewCacheSize(20);
                    myHolder.news_secand_recycler_view.setDrawingCacheEnabled(true);
                    myHolder.news_secand_recycler_view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                    myHolder.news_secand_recycler_view.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                    myHolder.news_secand_recycler_view.setAdapter(itemListDataAdapter);
                    itemListDataAdapter.notifyDataSetChanged();

                }

                myHolder.sold_prop_lay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Toast.makeText(MyPropertyActivity.this, "jlgjlgjlglgljgl", Toast.LENGTH_SHORT).show();
                        property_id = myPropertyLists.get(position).getId();

                        if (myPropertyLists.get(position).getSaleType().equalsIgnoreCase("Sale")) {
                            status = "SOLD";
                        } else {
                            status = "NOT AVAILABLE";
                        }

                        Intent intent = new Intent(context, AllUserListActivity.class);
                        intent.putExtra("property_id", property_id);
                        intent.putExtra("property_status", status);
                        startActivity(intent);

//                        if (isInternetPresent) {
//                            ChangePropertyStatus();
//                        } else {
//
//                            AlertConnection.showAlertDialog(getApplicationContext(), getString(R.string.no_internal_connection),
//                                    getString(R.string.donothaveinternet), false);
//                        }
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return myPropertyLists.size();
        }

        class MyHolder extends RecyclerView.ViewHolder {
            ImageView get_news_imageview, edit_property_image, sold_pojo_image;
            TextView property_add_or_new_text, news_property_price, news_property_address,
                    news_beds_texts, news_baths;
            RecyclerView news_secand_recycler_view;
            RelativeLayout news_card_view;
            LinearLayout edit_prop_lay, sold_prop_lay;

            // create constructor to get widget reference
            public MyHolder(View itemView) {
                super(itemView);
                get_news_imageview = itemView.findViewById(R.id.get_news_imageview);
                property_add_or_new_text = itemView.findViewById(R.id.property_add_or_new_text);
                news_property_price = itemView.findViewById(R.id.news_property_price);
                news_property_address = itemView.findViewById(R.id.news_property_address);
                news_beds_texts = itemView.findViewById(R.id.news_beds_texts);
                news_baths = itemView.findViewById(R.id.news_baths);
                edit_property_image = itemView.findViewById(R.id.edit_property_image);
                sold_pojo_image = itemView.findViewById(R.id.sold_pojo_image);
                news_secand_recycler_view = itemView.findViewById(R.id.news_secand_recycler_view);
                news_card_view = itemView.findViewById(R.id.news_card_view);
                edit_prop_lay = itemView.findViewById(R.id.edit_prop_lay);
                sold_prop_lay = itemView.findViewById(R.id.sold_prop_lay);

            }

        }
    }


    public class SectionListDataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private Context context;
        private LayoutInflater inflater;
        public List<PropertyImage> dataHolderList;
        public List<DataHolder> ssdataHolderList;
        PropertyDetails propertyDetails;
        RecyclerView recyclerView;
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
                }


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


}
