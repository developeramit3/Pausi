package com.t.pausi.Fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
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
import com.t.pausi.Activity.AddPropertyActivity;
import com.t.pausi.Activity.ChatActivity;
import com.t.pausi.Activity.HomeActivity;
import com.t.pausi.Activity.LoginActivity;
import com.t.pausi.Activity.PropertyDetailActivity;
import com.t.pausi.Activity.UpdateZipCodeActivity;
import com.t.pausi.Bean.AlertConnection;
import com.t.pausi.Bean.ConnectionDetector;
import com.t.pausi.Bean.DataHolder;
import com.t.pausi.Bean.MySharedPref;
import com.t.pausi.Bean.User;
import com.t.pausi.Interface.Config;
import com.t.pausi.Interface.UserInterface;
import com.t.pausi.Pojo.NewsList;
import com.t.pausi.Pojo.NewsResposne;
import com.t.pausi.Pojo.PropertyDetails;
import com.t.pausi.Pojo.PropertyImage;
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
import java.net.URL;
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

public class NewsFragment extends Fragment {
    TextView news_to_me_frag_text, news_to_upload_zip_text, news_invite_text;
    LinearLayout news_scroll_view;
    RecyclerView news_recycler_view;
    ConnectionDetector cd;
    Boolean isInternetPresent = false;
    ProgressDialog progressDialog;
    MySharedPref sp;
    String ldata, logid, path = "", property_id, fav = "";
    MultipartBody.Part body;
    boolean status = true;
    ImageView news_frag_camera_image;
    private FirebaseAuth mAuth;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.news_fragment_layout, container, false);

        cd = new ConnectionDetector(getActivity());
        isInternetPresent = cd.isConnectingToInternet();
        super.onStart();

        //-------------------------------- get login id -----------------------------------

        sp = new MySharedPref();
        ldata = sp.getData(getActivity(), "ldata", "null");
        if (ldata != null || !ldata.equals("") || !ldata.equalsIgnoreCase("null")) {
            try {
                JSONObject jsonObject = new JSONObject(ldata);
                logid = jsonObject.getString("id");
                Log.e("logid ", " " + logid);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //----------------------------- find view ----------------------------------

        news_to_me_frag_text = view.findViewById(R.id.news_to_me_frag_text);
        news_to_upload_zip_text = view.findViewById(R.id.news_to_upload_zip_text);
        news_scroll_view = view.findViewById(R.id.news_scroll_view);
        news_recycler_view = view.findViewById(R.id.news_recycler_view);
        news_invite_text = view.findViewById(R.id.news_invite_text);
        news_frag_camera_image = view.findViewById(R.id.news_frag_camera_image);


        Firebase.setAndroidContext(getActivity());
        mAuth = FirebaseAuth.getInstance();
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("users");

        //---------------------------- get news call -----------------------


        if (isInternetPresent) {

            if (ldata != null || !ldata.equals("") || !ldata.equalsIgnoreCase("null")) {
                GetProfile_call();
                GetNews_call();
            }

        } else {
            AlertConnection.showAlertDialog(getActivity(), "No Internet Connection",
                    "You don't have internet connection.", false);
        }


        //---------------------------- on click -------------------------------------

        news_frag_camera_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ldata == null || ldata.equalsIgnoreCase("") || ldata.equalsIgnoreCase("null")) {

                    final Dialog dialog = new Dialog(getActivity());
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
                            startActivity(new Intent(getActivity(), LoginActivity.class));
                            getActivity().finish();
                        }
                    });

                    dialog.show();

                } else {
                    startActivity(new Intent(getActivity(), AddPropertyActivity.class));
                }
            }
        });

        news_invite_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareTextUrl();
            }
        });

        news_to_me_frag_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ldata == null || ldata.equalsIgnoreCase("") || ldata.equalsIgnoreCase("null")) {

                    final Dialog dialog = new Dialog(getActivity());
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
                            startActivity(new Intent(getActivity(), LoginActivity.class));
                            getActivity().finish();
                        }
                    });

                    dialog.show();

                } else {
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
                                if (isInternetPresent) {
                                    path = r.getPath();
                                    File file = new File(path);
                                    RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                                    body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
                                    Log.e("Imagepath", path);
                                    UpdateProfileImage_call();
                                } else {
                                    AlertConnection.showAlertDialog(getActivity(), "No Internet Connection",
                                            "You don't have internet connection.", false);
                                }
                                //r.getPath();
                            } else {
                                //Handle possible errors
                                //TODO: do what you have to do with r.getError();
                                Toast.makeText(getActivity(), r.getError().getMessage(), Toast.LENGTH_LONG).show();
                            }

                        }

                    }).show(getActivity());

                    //HomeActivity.navigation.setSelectedItemId(R.id.me_buttom_nav);
                }
            }
        });

        news_to_upload_zip_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ldata == null || ldata.equalsIgnoreCase("") || ldata.equalsIgnoreCase("null")) {

                    final Dialog dialog = new Dialog(getActivity());
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
                            startActivity(new Intent(getActivity(), LoginActivity.class));
                            getActivity().finish();
                        }
                    });

                    dialog.show();

                } else {
                    startActivity(new Intent(getActivity(), UpdateZipCodeActivity.class));
                    HomeActivity.updateback = "news";
                }
            }
        });


        return view;
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

    @Override
    public void onResume() {
        super.onResume();
        if (isInternetPresent) {

            if (ldata != null || !ldata.equals("") || !ldata.equalsIgnoreCase("null")) {
                GetProfile_call();
                GetNews_call();
            }

        } else {
            AlertConnection.showAlertDialog(getActivity(), "No Internet Connection",
                    "You don't have internet connection.", false);
        }
    }

    //----------------------- get profile call -----------------------

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
                            String zipcode = jsonObject.getString("zipcode");

                            if (zipcode == null || zipcode.equalsIgnoreCase("")) {
                                news_recycler_view.setVisibility(View.GONE);
                                news_scroll_view.setVisibility(View.VISIBLE);
                            } else {
                                news_scroll_view.setVisibility(View.GONE);
                                news_recycler_view.setVisibility(View.VISIBLE);

                            }

                        } else {
                            String message = object.getString("message");
                            if (getActivity() != null) {
                                Toast.makeText(getActivity(), "" + message, Toast.LENGTH_SHORT).show();
                            }
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
                Toast.makeText(getActivity(), "Server Problem Please try Next time...!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    //--------------------------- get news list call -----------------------------------

    private void GetNews_call() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.Base_Url).client(client)
                .addConverterFactory(GsonConverterFactory.create(new Gson())).build();
        final UserInterface signupInterface = retrofit.create(UserInterface.class);
        Call<ResponseBody> resultCall = signupInterface.news_lists(logid);
        resultCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {

                        String responedata = response.body().string();
                        Log.e("news list response ", " " + responedata);
                        JSONObject object = new JSONObject(responedata);
                        String error = object.getString("status");

                        if (error.equals("1")) {

                            if (getActivity() != null) {
                                Gson gson = new Gson();


                                NewsResposne newsResposne = gson.fromJson(responedata, NewsResposne.class);
                                NewsAdapter adaper = new NewsAdapter(getActivity(), newsResposne.getResult());
                                news_recycler_view.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                                news_recycler_view.setHasFixedSize(true);
                                news_recycler_view.setAdapter(adaper);
                                news_recycler_view.setNestedScrollingEnabled(false);
                                adaper.notifyDataSetChanged();
                            }

                        } else {
                            String message = object.getString("message");
                            if (getActivity() != null) {
                                Toast.makeText(getActivity(), "News Not Found", Toast.LENGTH_SHORT).show();
                            }
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
                Toast.makeText(getActivity(), "Server Problem Please try Next time...!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    //-------------------------------- news adapter ---------------------------

    public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private Context context;
        private LayoutInflater inflater;
        public List<NewsList> newsLists;
        RecyclerView recyclerView;
        View view;

        public NewsAdapter(Context context, List<NewsList> newsLists) {
            this.context = context;
            inflater = LayoutInflater.from(context);
            this.newsLists = newsLists;
            notifyDataSetChanged();
        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            view = inflater.inflate(R.layout.news_pojo_lay, parent, false);
            MyHolder holder = new MyHolder(view);
            return holder;
        }


        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

            System.out.println("Dattasize***" + newsLists.size());
            final MyHolder myHolder = (MyHolder) holder;
            if (newsLists.size() > 0) {

                myHolder.news_property_price.setText("$" + newsLists.get(position).getPropertyDetails().getPropertyPrice());
                myHolder.news_baths.setText(newsLists.get(position).getPropertyDetails().getBaths() + " BATHS");
                myHolder.news_beds_texts.setText(newsLists.get(position).getPropertyDetails().getBeds() + " BEDS");
                myHolder.news_property_address.setText(newsLists.get(position).getPropertyDetails().getAddress());
                myHolder.property_add_or_new_text.setText(newsLists.get(position).getPropertyDetails().getStatus());
                myHolder.news_share_pojo_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        shareTextUrl();
                    }
                });

                if (newsLists.get(position).getPropertyDetails().getPropertyImages() != null && !newsLists.get(position).getPropertyDetails().getPropertyImages().isEmpty()) {

                    SectionListDataAdapter itemListDataAdapter = new SectionListDataAdapter(context, newsLists.get(position).getPropertyDetails().getPropertyImages(), newsLists.get(position).getPropertyDetails());
                    myHolder.news_secand_recycler_view.setHasFixedSize(true);
                    myHolder.news_secand_recycler_view.setItemViewCacheSize(20);
                    myHolder.news_secand_recycler_view.setDrawingCacheEnabled(true);
                    myHolder.news_secand_recycler_view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                    myHolder.news_secand_recycler_view.getRecycledViewPool().setMaxRecycledViews(0, 0);
                    myHolder.news_secand_recycler_view.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                    myHolder.news_secand_recycler_view.setAdapter(itemListDataAdapter);
                    itemListDataAdapter.notifyDataSetChanged();


                }


                myHolder.news_add_to_fav_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        property_id = newsLists.get(position).getPropertyId();
                        if (status) {
                            fav = "Favourite";
                            status = false;
                        } else {
                            fav = "Unfavourite";
                            status = true;
                        }
                        if (isInternetPresent) {
                            AddToFav();
                        } else {
                            AlertConnection.showAlertDialog(getActivity(), "No Internet Connection",
                                    "You don't have internet connection.", false);
                        }

                    }
                });

                myHolder.news_send_in_message_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mFirebaseDatabase.orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        String key = ds.getKey();
                                        User user = ds.child("credentials").getValue(User.class);
                                        String email = user.email;


                                        if (email != null && newsLists.get(position).getPropertyDetails().getAgentDetails().getEmail() != null && newsLists.get(position).getPropertyDetails().getAgentDetails().getEmail().equalsIgnoreCase(email)) {
                                            Log.e("key >>> ", key);

                                            if (key != null && mAuth.getCurrentUser().getUid() != null && key.equalsIgnoreCase(mAuth.getCurrentUser().getUid())) {

                                                Toast.makeText(context, getString(R.string.youcantnotchatown), Toast.LENGTH_SHORT).show();

                                            } else {

                                                String agent_id = key;
                                                Intent intent = new Intent(context, ChatActivity.class);
                                                intent.putExtra("receiver_id", agent_id);
                                                intent.putExtra("username", newsLists.get(position).getPropertyDetails().getAgentDetails().getFirstName() + " " + newsLists.get(position).getPropertyDetails().getAgentDetails().getLastName());
                                                intent.putExtra("userprofile", newsLists.get(position).getPropertyDetails().getAgentDetails().getImage());
                                                intent.putExtra("property_id", newsLists.get(position).getPropertyId());
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
            }
        }

        @Override
        public int getItemCount() {
            return newsLists.size();
        }

        class MyHolder extends RecyclerView.ViewHolder {
            ImageView get_news_imageview, news_share_pojo_image, news_add_to_fav_image, news_send_in_message_image;
            TextView property_add_or_new_text, news_property_price, news_property_address,
                    news_beds_texts, news_baths;
            RecyclerView news_secand_recycler_view;
            RelativeLayout news_card_view;

            // create constructor to get widget reference
            public MyHolder(View itemView) {
                super(itemView);
                get_news_imageview = itemView.findViewById(R.id.get_news_imageview);
                property_add_or_new_text = itemView.findViewById(R.id.property_add_or_new_text);
                news_property_price = itemView.findViewById(R.id.news_property_price);
                news_property_address = itemView.findViewById(R.id.news_property_address);
                news_beds_texts = itemView.findViewById(R.id.news_beds_texts);
                news_baths = itemView.findViewById(R.id.news_baths);
                news_share_pojo_image = itemView.findViewById(R.id.news_share_pojo_image);
                news_add_to_fav_image = itemView.findViewById(R.id.news_add_to_fav_image);
                news_secand_recycler_view = itemView.findViewById(R.id.news_secand_recycler_view);
                news_card_view = itemView.findViewById(R.id.news_card_view);
                news_send_in_message_image = itemView.findViewById(R.id.news_send_in_message_image);
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


                getItemViewType(position);

                if (position == dataHolderList.size()) {
                    myHolder.home_property_imageview.setVisibility(View.GONE);
                    myHolder.home_prop_detail_layout.setVisibility(View.VISIBLE);
                    myHolder.home_property_price_text2.setText(propertyDetails.getPropertyPrice() + " XAF");
                    myHolder.home_property_sel_type_text2.setText(propertyDetails.getSaleType());
                    myHolder.home_property_address_text2.setText(propertyDetails.getAddress());
                    myHolder.home_property_bads_text2.setText(propertyDetails.getBeds() + " Beds .");
                    myHolder.home_property_baths_text2.setText(propertyDetails.getBaths() + " Baths .");
                    myHolder.home_property_acres_text2.setText(propertyDetails.getAcreArea() + " Acres .");
                    myHolder.home_property_sqft_text2.setText(propertyDetails.getSqFeet() + " m2");

                    myHolder.more_info_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(), PropertyDetailActivity.class);
                            intent.putExtra("prop_id", propertyDetails.getId());
                            startActivity(intent);
                        }
                    });

                } else {

                    final String[] okFileExtensions = new String[]{"jpg", "png", "gif", "jpeg"};


                    Picasso.with(context).load(dataHolderList.get(position).getPropertyImage()).placeholder(R.drawable.dummy_img).into(myHolder.home_property_imageview);

                  /*  RequestOptions requestOptions = new RequestOptions();
                    requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
                    Glide.with(context)
                            .load(dataHolderList.get(position).getPropertyImage()).apply(requestOptions)
                            .into(myHolder.home_property_imageview);*/
//

                }
            }

            myHolder.home_property_imageview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), PropertyDetailActivity.class);
                    intent.putExtra("prop_id", dataHolderList.get(position).getPropertyId());
                    startActivity(intent);
                }
            });

        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
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
            Button more_info_btn, dummy_btn;


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
                dummy_btn = itemView.findViewById(R.id.dummy_btn);

            }


        }
    }


    private Bitmap eraseBG(Bitmap src, int color) {
        int width = src.getWidth();
        int height = src.getHeight();
        Bitmap b = src.copy(Bitmap.Config.ARGB_8888, true);
        b.setHasAlpha(true);

        int[] pixels = new int[width * height];
        src.getPixels(pixels, 0, width, 0, 0, width, height);

        for (int i = 0; i < width * height; i++) {
            if (pixels[i] == color) {
                pixels[i] = 0;
            }
        }

        b.setPixels(pixels, 0, width, 0, 0, width, height);

        return b;
    }

    //------------------------------------ update profile image call -----------------------------------

    private void UpdateProfileImage_call() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait..");
        progressDialog.show();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.Base_Url).client(client)
                .addConverterFactory(GsonConverterFactory.create(new Gson())).build();
        UserInterface signupInterface = retrofit.create(UserInterface.class);
        Call<ResponseBody> resultCall = signupInterface.update_user_image(logid, body);
        resultCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    try {

                        String responedata = response.body().string();
                        Log.e("up_image response* ", " " + responedata);
                        JSONObject object = new JSONObject(responedata);
                        String error = object.getString("status");

                        if (error.equals("1")) {

                            Toast.makeText(getActivity(), "Profile Update Successfully", Toast.LENGTH_SHORT).show();

//                            JSONObject jsonObject = object.getJSONObject("result");
//                            image = jsonObject.getString("image");
//                            Picasso.with(getActivity()).load(image).error(R.drawable.user).into(me_frag_profile_imageview);

                        } else {
                            String message = object.getString("message");
                            if (getActivity() != null) {
                                Toast.makeText(getActivity(), "" + message, Toast.LENGTH_SHORT).show();
                            }
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
                Toast.makeText(getActivity(), "Server Problem Please try Next time...!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //------------------------------------ add to favourite call -----------------------------------

    private void AddToFav() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait..");
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

                            Toast.makeText(getActivity(), fav, Toast.LENGTH_SHORT).show();
                            //Toast.makeText(getActivity(), "Unfavourite", Toast.LENGTH_SHORT).show();


                        } else {
                            String message = object.getString("message");
                            if (getActivity() != null) {
                                Toast.makeText(getActivity(), "" + message, Toast.LENGTH_SHORT).show();
                            }
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
                Toast.makeText(getActivity(), "Server Problem Please try Next time...!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
