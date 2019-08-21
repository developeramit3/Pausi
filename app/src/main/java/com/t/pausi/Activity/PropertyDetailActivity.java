package com.t.pausi.Activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.client.Firebase;
import com.google.android.gms.ads.internal.overlay.zzo;
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
import com.t.pausi.Bean.FadeOutTransformation;
import com.t.pausi.Bean.MyLanguageSession;
import com.t.pausi.Bean.MySharedPref;
import com.t.pausi.Bean.SliderBean;
import com.t.pausi.Bean.User;
import com.t.pausi.Bean.UserChat;
import com.t.pausi.Bean.ViewPagerCustomDuration;
import com.t.pausi.Fragment.HomeFragment;
import com.t.pausi.Fragment.MessageFragment;
import com.t.pausi.Fragment.NewsFragment;
import com.t.pausi.Interface.Config;
import com.t.pausi.Interface.UserInterface;
import com.t.pausi.Pojo.PropertyDetailResponse;
import com.t.pausi.Pojo.PropertyDetails;
import com.t.pausi.Pojo.PropertyImage;
import com.t.pausi.Pojo.SimilarListingList;
import com.t.pausi.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import me.relex.circleindicator.CircleIndicator;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PropertyDetailActivity extends AppCompatActivity {
    ImageView property_detail_back;
    RelativeLayout property_detail_back_lay;
    ConnectionDetector cd;
    Boolean isInternetPresent = false;
    ProgressDialog progressDialog;
    MySharedPref sp;
    String ldata, logid = "", path = "", property_id = "", property_id_listing = "";
    int counter;
    ViewPagerCustomDuration VP_banner_slidder;
    private static int currentPage = 0;
    CircleIndicator CI_indicator;
    private static int NUM_PAGES = 0;
    List<PropertyImage> propertyImageList;
    SliderBean sliderBean;
    TextView properyty_name_tittle, total_photos_text, detail_address_text, detail_price_text,
            detail_property_type_text, detail_beds_text, detail_baths_text, detail_sqft_text, detail_year_built_text, detail_descrp_text,
            detail_agent_name_text, detail_agent_broker_text, detail_app_about_text, notify_textviews;
    Button detail_sale_type_text;
    RecyclerView detail_similar_listing_recycler_view;
    CircleImageView detail_agent_image;
    Button ask_question_btn;
    String agent_id = "", agent_name = "", fav = "", notify = "";
    boolean status = true;
    RelativeLayout notify_for_latest_update_lay;
    AnimationSet animationSet;
    Animation fadeOutAnimation, fadeInAnimation;
    ImageView slider_imageview2;
    int currentImageIndex = 0;
    private FirebaseAuth mAuth;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    String agent_email = "";
    ImageView prop_detail_camera_image;
    List<PropertyImage> plist;
    MyLanguageSession myLanguageSession;
    private String language = "";
    TextView price_history_date_text, price_history_sale_text, price_history_price_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLanguageSession = new MyLanguageSession(this);
        language = myLanguageSession.getLanguage();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());
        setContentView(R.layout.activity_club_profile1);

        cd = new ConnectionDetector(this);
        isInternetPresent = cd.isConnectingToInternet();
        super.onStart();

        //-------------------------------- get login id -----------------------------------

        sp = new MySharedPref();
        ldata = sp.getData(getApplicationContext(), "ldata", "null");
        if (ldata != null || !ldata.equals("") || !ldata.equalsIgnoreCase("null")) {
            try {
                JSONObject jsonObject = new JSONObject(ldata);
                logid = jsonObject.getString("id");
                Log.e("logid ", " " + logid);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        property_detail_back = findViewById(R.id.property_detail_back);
        VP_banner_slidder = findViewById(R.id.vp_viewpager2);
        CI_indicator = (CircleIndicator) findViewById(R.id.ci_indicator2);
        properyty_name_tittle = findViewById(R.id.properyty_name_tittle);
        detail_address_text = findViewById(R.id.detail_address_text);
        detail_sale_type_text = findViewById(R.id.detail_sale_type_text);
        detail_price_text = findViewById(R.id.detail_price_text);
        detail_property_type_text = findViewById(R.id.detail_property_type_text);
        total_photos_text = findViewById(R.id.total_photos_text);
        detail_similar_listing_recycler_view = findViewById(R.id.detail_similar_listing_recycler_view);
        detail_beds_text = findViewById(R.id.detail_beds_text);
        detail_baths_text = findViewById(R.id.detail_baths_text);
        detail_sqft_text = findViewById(R.id.detail_sqft_text);
        detail_year_built_text = findViewById(R.id.detail_year_built_text);
        detail_descrp_text = findViewById(R.id.detail_descrp_text);
        detail_agent_image = findViewById(R.id.detail_agent_image);
        detail_agent_name_text = findViewById(R.id.detail_agent_name_text);
        detail_agent_broker_text = findViewById(R.id.detail_agent_broker_text);
        detail_app_about_text = findViewById(R.id.detail_app_about_text);
        ask_question_btn = findViewById(R.id.ask_question_btn);
        notify_for_latest_update_lay = findViewById(R.id.notify_for_latest_update_lay);
        notify_textviews = findViewById(R.id.notify_textviews);
        prop_detail_camera_image = findViewById(R.id.prop_detail_camera_image);
        price_history_date_text = findViewById(R.id.price_history_date_text);
        price_history_sale_text = findViewById(R.id.price_history_sale_text);
        price_history_price_text = findViewById(R.id.price_history_price_text);
        property_detail_back_lay = findViewById(R.id.property_detail_back_lay);

        detail_similar_listing_recycler_view.setNestedScrollingEnabled(false);

        Firebase.setAndroidContext(this);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("users");

        //------------------ get intent data -----------------

        Bundle bundle = new Bundle();
        bundle = getIntent().getExtras();
        if (bundle != null) {
            property_id = bundle.getString("prop_id");
        }

        //---------------------------- get news call -----------------------

        if (isInternetPresent) {
            if (property_id != null && !property_id.equalsIgnoreCase("")) {

                GetPropertyDetail_call();
            }

        } else {
            AlertConnection.showAlertDialog(getApplicationContext(), getString(R.string.no_internal_connection),
                    getString(R.string.donothaveinternet), false);
        }


        property_detail_back_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ask_question_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ldata == null || ldata.equalsIgnoreCase("") || ldata.equalsIgnoreCase("null")) {

                    final Dialog dialog = new Dialog(PropertyDetailActivity.this);
                    dialog.setContentView(R.layout.login_selection_dialog);
                    dialog.setCancelable(false);
                    Button cancel = dialog.findViewById(R.id.login_selection_cancel_btn);
                    Button ok = dialog.findViewById(R.id.login_selection_ok_btn);

                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });

                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            finish();
                        }
                    });

                    dialog.show();

                } else {

                    mFirebaseDatabase.orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    String key = ds.getKey();
                                    User user = ds.child("credentials").getValue(User.class);
                                    String email = user.email;
                                    String profile = user.profileUrl;

                                    if (email != null && agent_email != null && agent_email.equalsIgnoreCase(email)) {
                                        agent_id = key;
                                        if (key != null && mAuth.getCurrentUser().getUid() != null && key.equalsIgnoreCase(mAuth.getCurrentUser().getUid())) {

                                            Toast.makeText(PropertyDetailActivity.this, getString(R.string.youcantnotchatown), Toast.LENGTH_SHORT).show();

                                        } else {
                                            Log.e("agent key", agent_id);
                                            Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                                            intent.putExtra("receiver_id", agent_id);
                                            intent.putExtra("username", agent_name);
                                            intent.putExtra("userprofile", profile);
                                            intent.putExtra("property_id", property_id);
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

            }
        });

        notify_for_latest_update_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (notify_textviews.getText().toString() != null && notify_textviews.getText().toString().equalsIgnoreCase("Notify me for latest updates")) {
                    notify_textviews.setText(R.string.turnoffnoti);

                    if (isInternetPresent) {
                        AddRemoveNotify_call();
                    } else {
                        AlertConnection.showAlertDialog(getApplicationContext(), getString(R.string.no_internal_connection),
                                getString(R.string.donothaveinternet), false);
                    }


                } else {
                    notify_textviews.setText(R.string.notifyme);

                    if (isInternetPresent) {
                        AddRemoveNotify_call();
                    } else {
                        AlertConnection.showAlertDialog(getApplicationContext(), getString(R.string.no_internal_connection),
                                getString(R.string.donothaveinternet), false);
                    }

                }
            }
        });


        prop_detail_camera_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ldata == null || ldata.equalsIgnoreCase("") || ldata.equalsIgnoreCase("null")) {

                    final Dialog dialog = new Dialog(PropertyDetailActivity.this);
                    dialog.setContentView(R.layout.login_selection_dialog);
                    dialog.setCancelable(false);
                    Button cancel = dialog.findViewById(R.id.login_selection_cancel_btn);
                    Button ok = dialog.findViewById(R.id.login_selection_ok_btn);

                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });

                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            finish();
                        }
                    });

                    dialog.show();

                } else {
                    startActivity(new Intent(getApplicationContext(), AddPropertyActivity.class));
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

    //--------------------------- GetPropertyDetail_call  -----------------------------------

    private void GetPropertyDetail_call() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.Base_Url).client(client)
                .addConverterFactory(GsonConverterFactory.create(new Gson())).build();
        UserInterface signupInterface = retrofit.create(UserInterface.class);
        Call<ResponseBody> resultCall = signupInterface.listing_details(property_id, logid);
        resultCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {

                        String responedata = response.body().string();
                        Log.e("property detail ", " " + responedata);
                        JSONObject object = new JSONObject(responedata);
                        String error = object.getString("status");

                        if (error.equals("1")) {

                            if (PropertyDetailActivity.this != null) {
                                Gson gson = new Gson();
                                PropertyDetailResponse propertyDetailResponse = gson.fromJson(responedata, PropertyDetailResponse.class);

                                properyty_name_tittle.setText(propertyDetailResponse.getResult().get(0).getPropertyName());
                                detail_address_text.setText(propertyDetailResponse.getResult().get(0).getAddress());
                                detail_price_text.setText(propertyDetailResponse.getResult().get(0).getPropertyPrice() + getString(R.string.xaf));
                                price_history_price_text.setText(propertyDetailResponse.getResult().get(0).getPropertyPrice() + getString(R.string.xaf));
                                price_history_date_text.setText(propertyDetailResponse.getResult().get(0).getCreatedDate());
                                detail_property_type_text.setText(propertyDetailResponse.getResult().get(0).getPropertyType());
                                detail_beds_text.setText(propertyDetailResponse.getResult().get(0).getBeds());
                                detail_baths_text.setText(propertyDetailResponse.getResult().get(0).getBaths());
                                detail_sqft_text.setText(propertyDetailResponse.getResult().get(0).getSqFeet() + getString(R.string.m2));
                                detail_app_about_text.setText(getString(R.string.ifound) + propertyDetailResponse.getResult().get(0).getPropertyName() + "," + propertyDetailResponse.getResult().get(0).getAddress() + getString(R.string.learnmore));
                                detail_year_built_text.setText(propertyDetailResponse.getResult().get(0).getBuildYear());
                                detail_descrp_text.setText(propertyDetailResponse.getResult().get(0).getPropertyDescription());
                                detail_agent_name_text.setText(propertyDetailResponse.getResult().get(0).getAgentDetails().getFirstName());
                                detail_agent_broker_text.setText(propertyDetailResponse.getResult().get(0).getAgentDetails().getBroker());
                                agent_email = propertyDetailResponse.getResult().get(0).getAgentDetails().getEmail();

                                propertyDetailResponse.getResult().get(0).getNotified();

                                if (propertyDetailResponse.getResult().get(0).getNotified() != null && propertyDetailResponse.getResult().get(0).getNotified().equalsIgnoreCase("NO")) {
                                    notify_textviews.setText(getString(R.string.notify_me_for_latest_updates));
                                } else {
                                    notify_textviews.setText(getString(R.string.turnoffnoti));
                                }

                                //agent_id = propertyDetailResponse.getResult().get(0).getAgentDetails().getId();
                                agent_name = propertyDetailResponse.getResult().get(0).getAgentDetails().getFirstName() + propertyDetailResponse.getResult().get(0).getAgentDetails().getLastName();

                                if (propertyDetailResponse.getResult().get(0).getAgentDetails().getImage() != null && !propertyDetailResponse.getResult().get(0).getAgentDetails().getImage().equalsIgnoreCase("")) {

                                    Picasso.with(getApplicationContext()).load(propertyDetailResponse.getResult().get(0).getAgentDetails().getImage()).placeholder(R.drawable.user).into(detail_agent_image);
                                }

                                // for (int i = 0; i < propertyDetailResponse.getResult().size(); i++) {

                                if (propertyDetailResponse.getResult().get(0).getPropertyImages() != null && propertyDetailResponse.getResult().get(0).getPropertyImages().size() > 0) {

                                    CustomPagerAdapter customPagerAdapter = new CustomPagerAdapter(PropertyDetailActivity.this, propertyDetailResponse.getResult().get(0).getPropertyImages());
//                                    VP_banner_slidder.setScrollDurationFactor(2);
                                    VP_banner_slidder.setPageTransformer(false, new FadeOutTransformation());
                                    VP_banner_slidder.setAdapter(customPagerAdapter);
                                    // CI_indicator.setViewPager(VP_banner_slidder);
                                    NUM_PAGES = propertyDetailResponse.getResult().get(0).getPropertyImages().size();


                                    total_photos_text.setText(String.valueOf(propertyDetailResponse.getResult().get(0).getPropertyImages().size()));

                                    final Handler handler = new Handler();
                                    final Runnable Update = new Runnable() {
                                        public void run() {
                                            if (currentPage == NUM_PAGES) {
                                                currentPage = 0;
                                            }
                                            VP_banner_slidder.setCurrentItem(currentPage++, true);
                                        }
                                    };
                                    Timer swipeTimer = new Timer();
                                    swipeTimer.schedule(new TimerTask() {
                                        @Override
                                        public void run() {
                                            handler.post(Update);
                                        }
                                    }, 5000, 5000);

                                    VP_banner_slidder.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                                        @Override
                                        public void onPageSelected(int position) {
                                            currentPage = position;
                                            Log.e("Curent Page :", "" + currentPage);
                                        }

                                        @Override
                                        public void onPageScrolled(int pos, float arg1, int arg2) {
                                        }

                                        @Override
                                        public void onPageScrollStateChanged(int pos) {
                                        }
                                    });
                                }

                                SimilarListingAdapter adaper = new SimilarListingAdapter(PropertyDetailActivity.this, propertyDetailResponse.getResult().get(0).getSimilarListingList());
                                detail_similar_listing_recycler_view.setLayoutManager(new LinearLayoutManager(PropertyDetailActivity.this, LinearLayoutManager.VERTICAL, false));
                                detail_similar_listing_recycler_view.setAdapter(adaper);
                                //detail_similar_listing_recycler_view.setNestedScrollingEnabled(false);
                                adaper.notifyDataSetChanged();


                            }

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


    public class CustomPagerAdapter extends PagerAdapter {

        // private ArrayList<Integer> IMAGES;
        public List<PropertyImage> sliderBeanList;
        private LayoutInflater inflater;
        private Context context;


        public CustomPagerAdapter(Context context, List<PropertyImage> sliderBeanList) {
            this.context = context;
            this.sliderBeanList = sliderBeanList;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return sliderBeanList.size();
        }

        @Override
        public Object instantiateItem(ViewGroup view, final int position) {
            View imageLayout = inflater.inflate(R.layout.welcome_three_layout, view, false);

            assert imageLayout != null;
            final ImageView imageView = (ImageView) imageLayout.findViewById(R.id.property_detail_imageview);


           // Picasso.with(context).load(sliderBeanList.get(position).getPropertyImage()).placeholder(R.drawable.dummy_img).into(imageView);
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
            Glide.with(context)
                    .load(sliderBeanList.get(position).getPropertyImage()).apply(requestOptions)
                    .into(imageView);


            view.addView(imageLayout, 0);


            return imageLayout;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }


    }

    //-------------------------------- home property adapter ---------------------------

    public class SimilarListingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private Context context;
        private LayoutInflater inflater;
        public List<SimilarListingList> dataHolderList;
        PropertyDetails propertyDetails;
        RecyclerView recyclerView;
        View view;

        public SimilarListingAdapter(Context context, List<SimilarListingList> dataHolderList) {
            this.context = context;
            inflater = LayoutInflater.from(context);
            this.dataHolderList = dataHolderList;
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.similat_list_prop_pojo_lay, parent, false);
            MyHolder holder = new MyHolder(view);
            return holder;
        }

        @SuppressLint("ResourceAsColor")
        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

            System.out.println("Dattasize***" + dataHolderList.size());
            final MyHolder myHolder = (MyHolder) holder;
            if (dataHolderList.size() > 0) {

                myHolder.detail_property_price_text.setText(dataHolderList.get(position).getPropertyPrice() + getString(R.string.xaf));

                myHolder.detail_property_sale_type_text.setText(dataHolderList.get(position).getSaleType());

                myHolder.similar_listing_share_pojo_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        shareTextUrl();
                    }
                });

                myHolder.similar_listing_add_to_fav_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (ldata == null || ldata.equalsIgnoreCase("") || ldata.equalsIgnoreCase("null")) {

                            final Dialog dialog = new Dialog(PropertyDetailActivity.this);
                            dialog.setContentView(R.layout.login_selection_dialog);
                            dialog.setCancelable(false);
                            Button cancel = dialog.findViewById(R.id.login_selection_cancel_btn);
                            Button ok = dialog.findViewById(R.id.login_selection_ok_btn);

                            cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                }
                            });

                            ok.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                    finish();
                                }
                            });

                            dialog.show();

                        } else {

                            property_id_listing = dataHolderList.get(position).getId();
                            if (status) {
                                fav = getString(R.string.fav);
                                status = false;
                            } else {
                                fav = getString(R.string.unfavorite);
                                status = true;
                            }
                            if (isInternetPresent) {
                                AddToFav();
                            } else {
                                AlertConnection.showAlertDialog(getApplicationContext(), getString(R.string.no_internal_connection),
                                        getString(R.string.donothaveinternet), false);
                            }
                        }
                    }
                });

                myHolder.similer_listing_send_in_message_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (ldata == null || ldata.equalsIgnoreCase("") || ldata.equalsIgnoreCase("null")) {

                            final Dialog dialog = new Dialog(PropertyDetailActivity.this);
                            dialog.setContentView(R.layout.login_selection_dialog);
                            dialog.setCancelable(false);
                            Button cancel = dialog.findViewById(R.id.login_selection_cancel_btn);
                            Button ok = dialog.findViewById(R.id.login_selection_ok_btn);

                            cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                }
                            });

                            ok.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                    finish();
                                }
                            });

                            dialog.show();

                        } else {
                            mFirebaseDatabase.orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                            String key = ds.getKey();
                                            User user = ds.child("credentials").getValue(User.class);
                                            String email = user.email;


                                            if (email != null && dataHolderList.get(position).getAgentDetails().getEmail() != null && dataHolderList.get(position).getAgentDetails().getEmail().equalsIgnoreCase(email)) {
                                                if (key != null && mAuth.getCurrentUser().getUid() != null && key.equalsIgnoreCase(mAuth.getCurrentUser().getUid())) {

                                                    Toast.makeText(context, getString(R.string.youcantnotchatown), Toast.LENGTH_SHORT).show();

                                                } else {
                                                    agent_id = key;
                                                    Intent intent = new Intent(context, ChatActivity.class);
                                                    intent.putExtra("receiver_id", agent_id);
                                                    intent.putExtra("username", dataHolderList.get(position).getAgentDetails().getFirstName() + " " + dataHolderList.get(position).getAgentDetails().getLastName());
                                                    intent.putExtra("userprofile", dataHolderList.get(position).getAgentDetails().getImage());
                                                    intent.putExtra("property_id", dataHolderList.get(position).getId());
                                                    startActivity(intent);
                                                }
                                            }
                                        }
                                    }


                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }

                    }
                });

                if (dataHolderList.get(position).getPropertyImages() != null && !dataHolderList.get(position).getPropertyImages().isEmpty()) {

                    //Picasso.with(context).load(dataHolderList.get(position).getPropertyImageList().get(0).getPropertyImage()).into(myHolder.home_property_imageview);

                    propertyDetails = new PropertyDetails();
                    propertyDetails.setId(dataHolderList.get(position).getId());
                    propertyDetails.setAcreArea(dataHolderList.get(position).getAcreArea());
                    propertyDetails.setAddress(dataHolderList.get(position).getAddress());
                    propertyDetails.setBaths(dataHolderList.get(position).getBaths());
                    propertyDetails.setBeds(dataHolderList.get(position).getBeds());
                    propertyDetails.setPropertyPrice(dataHolderList.get(position).getPropertyPrice());
                    propertyDetails.setSaleType(dataHolderList.get(position).getSaleType());
                    propertyDetails.setSqFeet(dataHolderList.get(position).getSqFeet());
                    SectionListDataAdapter itemListDataAdapter = new SectionListDataAdapter(context, dataHolderList.get(position).getPropertyImages(), propertyDetails);
                    myHolder.similar_listing_recycler_view.setHasFixedSize(true);
                    myHolder.similar_listing_recycler_view.setItemViewCacheSize(20);
                    myHolder.similar_listing_recycler_view.setDrawingCacheEnabled(true);
                    myHolder.similar_listing_recycler_view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                    myHolder.similar_listing_recycler_view.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                    myHolder.similar_listing_recycler_view.setAdapter(itemListDataAdapter);
                    itemListDataAdapter.notifyDataSetChanged();

                }
            }
        }

        @Override
        public int getItemCount() {
            return dataHolderList.size();
        }

        class MyHolder extends RecyclerView.ViewHolder {

            TextView detail_property_price_text, detail_property_sale_type_text;
            RecyclerView home_secand_recycler_view;
            RecyclerView similar_listing_recycler_view;
            ImageView similar_listing_add_to_fav_image, similar_listing_share_pojo_image,
                    similer_listing_send_in_message_image;


            // create constructor to get widget reference
            public MyHolder(View itemView) {
                super(itemView);

                detail_property_price_text = itemView.findViewById(R.id.detail_property_price_text);
                detail_property_sale_type_text = itemView.findViewById(R.id.detail_property_sale_type_text);
                similar_listing_recycler_view = itemView.findViewById(R.id.similar_listing_recycler_view);
                similar_listing_add_to_fav_image = itemView.findViewById(R.id.similar_listing_add_to_fav_image);
                similar_listing_share_pojo_image = itemView.findViewById(R.id.similar_listing_share_pojo_image);
                similer_listing_send_in_message_image = itemView.findViewById(R.id.similer_listing_send_in_message_image);

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

//                    RequestOptions requestOptions = new RequestOptions();
//                    requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
//                    Glide.with(context)
//                            .load(dataHolderList.get(position).getPropertyImage()).apply(requestOptions)
//                            .into(myHolder.home_property_imageview);

                    Picasso.with(context).load(dataHolderList.get(position).getPropertyImage()).placeholder(R.drawable.dummy_img).into(myHolder.home_property_imageview);
                    System.out.println("ssssssssssssss " + dataHolderList.get(position).getPropertyImage());
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
        progressDialog = new ProgressDialog(PropertyDetailActivity.this);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.show();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.Base_Url).client(client)
                .addConverterFactory(GsonConverterFactory.create(new Gson())).build();
        UserInterface signupInterface = retrofit.create(UserInterface.class);
        Call<ResponseBody> resultCall = signupInterface.add_to_favourite(logid, property_id_listing);
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

                            Toast.makeText(getApplicationContext(), fav, Toast.LENGTH_SHORT).show();
                            //Toast.makeText(getActivity(), "Unfavourite", Toast.LENGTH_SHORT).show();


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

    //------------------------------------ add_remove_notify  -----------------------------------

    private void AddRemoveNotify_call() {
        progressDialog = new ProgressDialog(PropertyDetailActivity.this);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.show();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.Base_Url).client(client)
                .addConverterFactory(GsonConverterFactory.create(new Gson())).build();
        UserInterface signupInterface = retrofit.create(UserInterface.class);
        Call<ResponseBody> resultCall = signupInterface.add_remove_notify(property_id, logid);
        resultCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    try {

                        String responedata = response.body().string();
                        Log.e("add_remove_notify ", " " + responedata);
                        JSONObject object = new JSONObject(responedata);
                        String error = object.getString("status");

                        if (error.equals("1")) {

                        } else {
                            String message = object.getString("message");
                            //Toast.makeText(getActivity(), "" + message, Toast.LENGTH_SHORT).show();
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
