package com.t.pausi.Fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.t.pausi.Activity.AgentEditProfileActivity;
import com.t.pausi.Activity.GetFavoriteActivity;
import com.t.pausi.Activity.MyPropertyActivity;
import com.t.pausi.Activity.WallpaperActivity;
import com.t.pausi.Bean.AlertConnection;
import com.t.pausi.Bean.ConnectionDetector;
import com.t.pausi.Bean.MySharedPref;
import com.t.pausi.Interface.Config;
import com.t.pausi.Interface.UserInterface;
import com.t.pausi.R;
import com.t.pausi.Activity.SettingActivity;
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

public class MeFragment extends Fragment {
    ImageView me_frag_setting_image, wall_image_view;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    ProgressDialog progressDialog;
    MySharedPref sp;
    String ldata, logid, wallpaper_image;
    CircleImageView me_frag_profile_imageview;
    TextView me_frag_name_textview, me_frag_broker_textview, me_friends_text, agent_edit_profile_textview;
    String fname, lname, phone, email, path = "", image, user_type, broker, id_proof;
    MultipartBody.Part body;
    LinearLayout more_lay;
    Dialog dialog;
    CardView user_welcome_card, agent_shortcuts_card;
    LinearLayout me_invite_lay;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.me_fragment_layout, container, false);

        //--------------------- connection detector -----------------------------------

        cd = new ConnectionDetector(getActivity());
        isInternetPresent = cd.isConnectingToInternet();
        super.onStart();

        //-------------------------------- get login id -----------------------------------

        sp = new MySharedPref();
        ldata = sp.getData(getActivity(), "ldata", "null");
        if (!ldata.equals("") || ldata != null) {
            try {
                JSONObject jsonObject = new JSONObject(ldata);
                logid = jsonObject.getString("id");
                fname = jsonObject.getString("first_name");
                lname = jsonObject.getString("last_name");
                image = jsonObject.getString("image");
                wallpaper_image = jsonObject.getString("wallpaper_image");
                Log.e("logid ", " " + logid);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //----------------------------------- find view -----------------------------------

        me_frag_setting_image = view.findViewById(R.id.me_frag_setting_image);
        me_frag_profile_imageview = view.findViewById(R.id.me_frag_profile_imageview);
        me_frag_name_textview = view.findViewById(R.id.me_frag_name_textview);
        more_lay = view.findViewById(R.id.more_lay);
        wall_image_view = view.findViewById(R.id.wall_image_view);
        user_welcome_card = view.findViewById(R.id.user_welcome_card);
        agent_shortcuts_card = view.findViewById(R.id.agent_shortcuts_card);
        me_frag_broker_textview = view.findViewById(R.id.me_frag_broker_textview);
        me_friends_text = view.findViewById(R.id.me_friends_text);
        me_invite_lay = view.findViewById(R.id.me_invite_lay);
        agent_edit_profile_textview = view.findViewById(R.id.agent_edit_profile_textview);


        me_frag_name_textview.setText(fname + " " + lname);
        Picasso.with(getActivity()).load(image).placeholder(R.drawable.user).into(me_frag_profile_imageview);
        Picasso.with(getActivity()).load(wallpaper_image).placeholder(R.drawable.profile_bg).into(wall_image_view);

//        if (image != null && !image.equalsIgnoreCase("http://technorizen.com/WORKSPACE1/pausi/uploads/images/")) {
//
//            Glide.with(getActivity()).load(image).into(me_frag_profile_imageview);
//        } else {
//            me_frag_profile_imageview.setImageResource(R.drawable.user);
//        }
//
//        if (wallpaper_image != null && !wallpaper_image.equalsIgnoreCase("http://technorizen.com/WORKSPACE1/pausi/uploads/images/")) {
//
//            Glide.with(getActivity()).load(wallpaper_image).into(wall_image_view);
//        } else {
//            wall_image_view.setImageResource(R.drawable.profile_bg);
//        }


        //------------------------------------ get profile --------------------------------

        if (isInternetPresent) {

            GetProfile_call();

        } else {
            AlertConnection.showAlertDialog(getActivity(), "No Internet Connection",
                    "You don't have internet connection.", false);
        }

        //------------------------------------ on click -------------------------------------

        agent_edit_profile_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AgentEditProfileActivity.class));
            }
        });


        me_invite_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareTextUrl();
            }
        });

        me_frag_profile_imageview.setOnClickListener(new View.OnClickListener() {
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
                            if (isInternetPresent) {
                                path = r.getPath();
                                Glide.with(getActivity()).load(path).into(me_frag_profile_imageview);

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
            }
        });

        me_frag_setting_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SettingActivity.class);
                intent.putExtra("fname", fname);
                intent.putExtra("lname", lname);
                intent.putExtra("email", email);
                intent.putExtra("phone", phone);
                intent.putExtra("user_type", user_type);
                intent.putExtra("image", image);
                intent.putExtra("id_proof", id_proof);
                intent.putExtra("broker", broker);
                startActivity(intent);
            }
        });

        more_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.more_options_dialog_lay);
                TextView change_profile_image_text = dialog.findViewById(R.id.change_profile_image_text);
                TextView change_wallpaper_text = dialog.findViewById(R.id.change_wallpaper_text);
                TextView view_fav_text = dialog.findViewById(R.id.view_fav_text);
                TextView my_property_fav_text = dialog.findViewById(R.id.my_property_fav_text);

                view_fav_text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        startActivity(new Intent(getActivity(), GetFavoriteActivity.class));
                    }
                });

                change_wallpaper_text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        startActivity(new Intent(getActivity(), WallpaperActivity.class));
                    }
                });

                my_property_fav_text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        startActivity(new Intent(getActivity(), MyPropertyActivity.class));
                    }
                });

                change_profile_image_text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
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
                                        Glide.with(getActivity()).load(path).into(me_frag_profile_imageview);
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
                    }
                });
                dialog.show();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isInternetPresent) {

            GetProfile_call();

        } else {
            AlertConnection.showAlertDialog(getActivity(), "No Internet Connection",
                    "You don't have internet connection.", false);
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

                            if (getActivity() != null) {

                                JSONObject jsonObject = object.getJSONObject("result");
                                fname = jsonObject.getString("first_name");
                                lname = jsonObject.getString("last_name");
                                phone = jsonObject.getString("mobile");
                                email = jsonObject.getString("email");
                                image = jsonObject.getString("image");
                                id_proof = jsonObject.getString("id_proof");
                                user_type = jsonObject.getString("user_type");
                                wallpaper_image = jsonObject.getString("wallpaper_image");
                                broker = jsonObject.getString("broker");

                                if (user_type.equalsIgnoreCase("AGENT"))

                                {
                                    user_welcome_card.setVisibility(View.GONE);
                                    agent_shortcuts_card.setVisibility(View.VISIBLE);
                                    me_frag_broker_textview.setVisibility(View.VISIBLE);
                                    me_frag_broker_textview.setText(broker);
                                    me_friends_text.setText("Clients");
                                }

                                me_frag_name_textview.setText(fname + " " + lname);

                                Picasso.with(getActivity()).load(image).placeholder(R.drawable.user).into(me_frag_profile_imageview);
                                Picasso.with(getActivity()).load(wallpaper_image).placeholder(R.drawable.profile_bg).into(wall_image_view);

                                final String[] okFileExtensions = new String[]{"jpg", "png", "gif", "jpeg"};

//                                for (String extension : okFileExtensions) {
//                                    if (image != null && image.toLowerCase().endsWith(extension)) {
//                                        Picasso.with(getActivity()).load(image).into(me_frag_profile_imageview);
//                                    } else {
//                                        me_frag_profile_imageview.setImageResource(R.drawable.user);
//                                    }
//
//
//                                }
//
//                                for (String extension : okFileExtensions) {
//                                    if (wallpaper_image != null && wallpaper_image.toLowerCase().endsWith(extension)) {
//                                        Picasso.with(getActivity()).load(wallpaper_image).into(wall_image_view);
//                                    } else {
//                                        wall_image_view.setImageResource(R.drawable.profile_bg);
//                                    }
//
//
//                                }


                                //Glide.with(getActivity()).load(wallpaper_image).error(R.drawable.profile_bg).into(wall_image_view);

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
}
