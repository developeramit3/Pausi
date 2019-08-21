package com.t.pausi.Activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.t.pausi.Bean.AlertConnection;
import com.t.pausi.Bean.ConnectionDetector;
import com.t.pausi.Bean.FadeOutTransformation;
import com.t.pausi.Bean.GPSTracker;
import com.t.pausi.Bean.MyLanguageSession;
import com.t.pausi.Bean.MySharedPref;
import com.t.pausi.Bean.User;
import com.t.pausi.Bean.UserChat;
import com.t.pausi.Constant.Constants;
import com.t.pausi.Fragment.MessageFragment;
import com.t.pausi.Fragment.NewsFragment;
import com.t.pausi.Interface.Config;
import com.t.pausi.Interface.UserInterface;
import com.t.pausi.Pojo.GetChatList;
import com.t.pausi.Pojo.GetChatResponse;
import com.t.pausi.Pojo.PropertyDetailResponse;
import com.t.pausi.R;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickCancel;
import com.vansuita.pickimage.listeners.IPickResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatActivity extends AppCompatActivity {
    ImageView chat_back, add_image_in_chat, location_imageview, chating_prop_imageview;
    TextView chat_tittle_text, chat_online_text, chating_prop_name_textview, chating_prop_rooms_textview,
            chating_prop_baths_textview, chating_prop_squre_text;
    RecyclerView get_chat_recycler_view;
    Button message_send_btn;
    EditText chat_message_editext;
    MySharedPref sp;
    String ldata, logid, receiver_id = "", username, chat_message = "", message = "", userprofile;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    ProgressDialog progressDialog;
    String path = "";
    Dialog dialog1, dialog2;
    MultipartBody.Part body;
    int size = 0, i;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    FirebaseAuth mAuth;
    String user_id, format, token;
    GPSTracker tracker;
    double P_latitude, P_longitude;
    private Timestamp timestamp;
    int totalUsers = 0;
    List<UserChat> get_message_list;
    MyLanguageSession myLanguageSession;
    private String language = "", final_path = "", lat = "", lon = "", property_id = "";
    String location_send = "";
    public static boolean isVisible = true;
    String first_name = "", image = "";
    boolean check = true;
    GetChatAdapter adaper;
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLanguageSession = new MyLanguageSession(this);
        language = myLanguageSession.getLanguage();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());
        setContentView(R.layout.activity_chat);

        //--------------------- connection detector -----------------------------------

        cd = new ConnectionDetector(this);
        isInternetPresent = cd.isConnectingToInternet();
        super.onStart();

        //-------------------- Get current lat lon ------------------------------------

        tracker = new GPSTracker(getApplicationContext());
        if (tracker.canGetLocation()) {
            P_latitude = tracker.getLatitude();
            P_longitude = tracker.getLongitude();
            Log.e("current_lat ", " " + P_latitude);
            Log.e("current_Lon ", " " + P_longitude);
            lat = String.valueOf(P_latitude);
            lon = String.valueOf(P_longitude);
        }


        //------------------------ get logid -----------------------------------------

        sp = new MySharedPref();
        ldata = sp.getData(getApplicationContext(), "ldata", "null");
        if (!ldata.equals("")) {
            try {
                JSONObject jsonObject = new JSONObject(ldata);
                logid = jsonObject.getString("id");
                first_name = jsonObject.getString("first_name");
                image = jsonObject.getString("image");


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //-------------------------- --- firebase chat code ----------------------
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        Firebase.setAndroidContext(this);
        //Get Firebase auth instance
        mAuth = FirebaseAuth.getInstance();
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("conversations");


        //------------------------- find view -------------------------

        chat_back = findViewById(R.id.chat_back);
        chat_tittle_text = findViewById(R.id.chat_tittle_text);
        get_chat_recycler_view = findViewById(R.id.get_chat_recycler_view);
        message_send_btn = findViewById(R.id.message_send_btn);
        chat_message_editext = findViewById(R.id.chat_message_editext);
        add_image_in_chat = findViewById(R.id.add_image_in_chat);
        chat_online_text = findViewById(R.id.chat_online_text);
        location_imageview = findViewById(R.id.location_imageview);
        chating_prop_imageview = findViewById(R.id.chating_prop_imageview);
        chating_prop_name_textview = findViewById(R.id.chating_prop_name_textview);
        chating_prop_rooms_textview = findViewById(R.id.chating_prop_rooms_textview);
        chating_prop_baths_textview = findViewById(R.id.chating_prop_baths_textview);
        chating_prop_squre_text = findViewById(R.id.chating_prop_squre_text);

        //------------------- get intent ------------

        Bundle bundle = new Bundle();
        bundle = getIntent().getExtras();
        if (bundle != null) {
            receiver_id = bundle.getString("receiver_id");
            username = bundle.getString("username");
            userprofile = bundle.getString("userprofile");
            property_id = bundle.getString("property_id");
            chat_tittle_text.setText(username);
            Log.e("receiver_id ", receiver_id);
            Log.e("property_id ", " " + property_id);
            Log.e("userprofile", " " + userprofile);
        }


//        try {
//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            format = simpleDateFormat.format(new Date());
//            i = (int) (new Date().getTime() / 1000);
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


        //----------------- get property detail -------------------------


        if (isInternetPresent) {

            if (property_id != null && !property_id.equalsIgnoreCase("")) {
                GetPropertyDetail_call();
            }

        } else {
            AlertConnection.showAlertDialog(getApplicationContext(), getString(R.string.no_internal_connection),
                    getString(R.string.donothaveinternet), false);
        }


        //----------------------------- get firebase messges ---------------------


        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("users");


        if (mAuth != null && mAuth.getCurrentUser() != null && mAuth.getCurrentUser().getUid() != null && rootRef.child(mAuth.getCurrentUser().getUid()).child("conversations").child(receiver_id) != null) {

            if (receiver_id != null) {
                rootRef.child(receiver_id).child("credentials").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {

                            User user = dataSnapshot.getValue(User.class);
                            token = user.token;
                            if (token != null) {
                                Log.e("token>>>> ", token);
                            }
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            rootRef.child(mAuth.getCurrentUser().getUid()).child("conversations").child(property_id).child(receiver_id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        UserChat userChat = dataSnapshot.getValue(UserChat.class);
                        String location = userChat.location;
                        getAllMessages(location);

                    } else {

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


        //------------------------ on click ---------------------------------

        chat_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        add_image_in_chat.setOnClickListener(new View.OnClickListener() {
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
                    public void onPickResult(final PickResult r) {
                        dialog.dismiss();
                        if (r.getError() == null) {
                            path = r.getPath();
                            Log.e("path", path);
                            dialog1 = new Dialog(ChatActivity.this);
                            dialog1.setCancelable(false);
                            dialog1.setContentView(R.layout.insert_image_with_text_dialog);
                            ImageView send_image_imageview = dialog1.findViewById(R.id.send_image_imageview);
                            Button image_text_send_btn = dialog1.findViewById(R.id.image_text_send_btn);

                            send_image_imageview.setImageBitmap(r.getBitmap());

                            image_text_send_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    try {
                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        format = simpleDateFormat.format(new Date());
                                        i = (int) (new Date().getTime() / 1000);


                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    path = resizeAndCompressImageBeforeSend(ChatActivity.this, path, "pausi_image");
                                    uploadFile(path);

                                }
                            });

                            send_image_imageview.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    dialog2 = new Dialog(ChatActivity.this);
                                    dialog2.setContentView(R.layout.image_remove_layout);
                                    dialog2.setCancelable(false);

                                    TextView remove_attachment_remove_textview = dialog2.findViewById(R.id.remove_attachment_remove_textview);
                                    TextView remove_attachment_cancel_textview = dialog2.findViewById(R.id.remove_attachment_cancel_textview);
                                    remove_attachment_remove_textview.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dialog1.dismiss();
                                            dialog2.dismiss();
                                        }
                                    });

                                    remove_attachment_cancel_textview.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dialog2.dismiss();
                                        }
                                    });

                                    dialog2.show();

                                }
                            });

                            dialog1.show();
                        } else {
                            //TODO: do what you have to do with r.getError();
                            Toast.makeText(ChatActivity.this, r.getError().getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }

                }).show(ChatActivity.this);
            }
        });

        //---------------------------- firebase text message send code -----------------------

        message_send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isInternetPresent) {
                    if (chat_message_editext.getText().toString().equalsIgnoreCase("")) {
                        Toast.makeText(ChatActivity.this, R.string.msg_cant_be_empty, Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            format = simpleDateFormat.format(new Date());
                            i = (int) (new Date().getTime() / 1000);


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        chat_message = chat_message_editext.getText().toString();
                        user_id = mAuth.getCurrentUser().getUid();

                        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("users");

                        if (user_id != null && rootRef.child(user_id).child("conversations").child(receiver_id) != null) {
                            rootRef.child(user_id).child("conversations").child(property_id).child(receiver_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.exists()) {
                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                            UserChat userChat = dataSnapshot.getValue(UserChat.class);
                                            String location = userChat.location;

                                            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("conversations");
                                            //UserChat user = new UserChat(chat_message, first_name, username, image, userprofile, user_id, receiver_id, "text", false, i, property_id);

                                            Map<Object, Object> params = new HashMap<>();
                                            params.put("content", chat_message);
                                            params.put("senderName", first_name);
                                            params.put("receiverName", username);
                                            params.put("senderProfile", image);
                                            params.put("receiverProfile", userprofile);
                                            params.put("fromID", user_id);
                                            params.put("toID", receiver_id);
                                            params.put("type", "text");
                                            params.put("isRead", false);
                                            params.put("timestamp", i);
                                            params.put("property_id", property_id);
                                            params.put(user_id, "0");
                                            params.put(receiver_id, "0");

                                            rootRef.child(location).push().setValue(params);

                                            chat_message_editext.getText().clear();
                                            if (token != null) {
                                                sendNotification(token);
                                            }
                                            //getAllMessages2(location);

                                        }
                                    } else {
                                        //UserChat user = new UserChat(chat_message, first_name, username, image, userprofile, user_id, receiver_id, "text", false, i, property_id);
                                        Map<Object, Object> params = new HashMap<>();
                                        params.put("content", chat_message);
                                        params.put("senderName", first_name);
                                        params.put("receiverName", username);
                                        params.put("senderProfile", image);
                                        params.put("receiverProfile", userprofile);
                                        params.put("fromID", user_id);
                                        params.put("toID", receiver_id);
                                        params.put("type", "text");
                                        params.put("isRead", false);
                                        params.put("timestamp", i);
                                        params.put("property_id", property_id);
                                        params.put(user_id, "0");
                                        params.put(receiver_id, "0");

                                        mFirebaseDatabase.push().push().setValue(params);
                                        addUserChangeListener();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        } else {
                            Toast.makeText(ChatActivity.this, "ttgjbsvksfkh", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    AlertConnection.showAlertDialog(ChatActivity.this, getString(R.string.no_internal_connection),
                            getString(R.string.donothaveinternet), false);
                }
            }
        });

        location_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (lat != null && lon != null) {
                    location_send = lat + ":" + lon;
                    AlertDialog.Builder dialog = new AlertDialog.Builder(ChatActivity.this);
                    dialog.setMessage(R.string.areyousurelocation);
                    dialog.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            paramDialogInterface.dismiss();
                            try {
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                format = simpleDateFormat.format(new Date());
                                i = (int) (new Date().getTime() / 1000);


                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //get gps
                            user_id = mAuth.getCurrentUser().getUid();
                            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("users");
                            if (user_id != null && rootRef.child(user_id).child("conversations").child(receiver_id) != null) {
                                rootRef.child(user_id).child("conversations").child(property_id).child(receiver_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        if (dataSnapshot.exists()) {
                                            for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                                UserChat userChat = dataSnapshot.getValue(UserChat.class);
                                                String location = userChat.location;
                                                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("conversations");
                                                //UserChat user = new UserChat(location_send, first_name, username, image, userprofile, user_id, receiver_id, "location", false, i, property_id);

                                                Map<Object, Object> params = new HashMap<>();
                                                params.put("content", location_send);
                                                params.put("senderName", first_name);
                                                params.put("receiverName", username);
                                                params.put("senderProfile", image);
                                                params.put("receiverProfile", userprofile);
                                                params.put("fromID", user_id);
                                                params.put("toID", receiver_id);
                                                params.put("type", "location");
                                                params.put("isRead", false);
                                                params.put("timestamp", i);
                                                params.put("property_id", property_id);
                                                params.put(user_id, "0");
                                                params.put(receiver_id, "0");
                                                rootRef.child(location).push().setValue(params);
                                                if (token != null) {
                                                    sendNotification(token);
                                                }
                                            }
                                        } else {

                                            // UserChat user = new UserChat(location_send, first_name, username, image, userprofile, user_id, receiver_id, "location", false, i, property_id);
                                            Map<Object, Object> params = new HashMap<>();
                                            params.put("content", location_send);
                                            params.put("senderName", first_name);
                                            params.put("receiverName", username);
                                            params.put("senderProfile", image);
                                            params.put("receiverProfile", userprofile);
                                            params.put("fromID", user_id);
                                            params.put("toID", receiver_id);
                                            params.put("type", "location");
                                            params.put("isRead", false);
                                            params.put("timestamp", i);
                                            params.put("property_id", property_id);
                                            params.put(user_id, "0");
                                            params.put(receiver_id, "0");
                                            mFirebaseDatabase.push().push().setValue(params);
                                            addUserChangeListener();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                    });
                    dialog.setNegativeButton(
                            getString(R.string.no), new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                    // TODO Auto-generated method stub

                                }
                            });
                    dialog.show();
                }
            }
        });
    }

    private void uploadFile(String path) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference mountainImagesRef = storage.getReference();
        Uri file = Uri.fromFile(new File(path));
        UploadTask uploadTask = mountainImagesRef.child("messagePics").child(UUID.randomUUID().toString()).putFile(file);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                dialog1.dismiss();
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                Log.d("downloadUrl-->", "" + downloadUrl);
                final_path = downloadUrl.toString();

                user_id = mAuth.getCurrentUser().getUid();

                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("users");

                if (user_id != null && rootRef.child(user_id).child("conversations").child(receiver_id) != null) {
                    rootRef.child(user_id).child("conversations").child(property_id).child(receiver_id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot.exists()) {
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                    UserChat userChat = dataSnapshot.getValue(UserChat.class);
                                    String location = userChat.location;
                                    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("conversations");
                                    //UserChat user = new UserChat(final_path, first_name, username, image, userprofile, user_id, receiver_id, "photo", false, i, property_id);

                                    Map<Object, Object> params = new HashMap<>();
                                    params.put("content", final_path);
                                    params.put("senderName", first_name);
                                    params.put("receiverName", username);
                                    params.put("senderProfile", image);
                                    params.put("receiverProfile", userprofile);
                                    params.put("fromID", user_id);
                                    params.put("toID", receiver_id);
                                    params.put("type", "photo");
                                    params.put("isRead", false);
                                    params.put("timestamp", i);
                                    params.put("property_id", property_id);
                                    params.put(user_id, "0");
                                    params.put(receiver_id, "0");
                                    rootRef.child(location).push().setValue(params);
                                    if (token != null) {
                                        sendNotification(token);
                                    }


                                }
                            } else {

                                //UserChat user = new UserChat(final_path, first_name, username, image, userprofile, user_id, receiver_id, "photo", false, i, property_id);
                                Map<Object, Object> params = new HashMap<>();
                                params.put("content", final_path);
                                params.put("senderName", first_name);
                                params.put("receiverName", username);
                                params.put("senderProfile", image);
                                params.put("receiverProfile", userprofile);
                                params.put("fromID", user_id);
                                params.put("toID", receiver_id);
                                params.put("type", "photo");
                                params.put("isRead", false);
                                params.put("timestamp", i);
                                params.put("property_id", property_id);
                                params.put(user_id, "0");
                                params.put(receiver_id, "0");
                                mFirebaseDatabase.push().push().setValue(params);
                                addUserChangeListener2();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }

                    });
                }


            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        isVisible = true;
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());
        String oldLanguage = language;
        language = myLanguageSession.getLanguage();
        if (!oldLanguage.equals(language)) {
            finish();
            startActivity(getIntent());
        }
    }

    private void addUserChangeListener() {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference deviceTokenRef = rootRef.child("conversations");
        // Query query = deviceTokenRef.orderByKey().limitToLast(1);
        Query query = deviceTokenRef.orderByKey();
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String key = ds.getKey();
                    Log.d("TAG", key);

                    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("users");
                    rootRef.child(mAuth.getCurrentUser().getUid()).child("conversations").child(property_id).child(receiver_id).child("location").setValue(key);
                    rootRef.child(receiver_id).child("conversations").child(property_id).child(mAuth.getCurrentUser().getUid()).child("location").setValue(key);
                    chat_message_editext.getText().clear();
                    if (token != null) {
                        sendNotification(token);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        query.addListenerForSingleValueEvent(eventListener);

    }

    private void addUserChangeListener2() {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference deviceTokenRef = rootRef.child("conversations");
        Query query = deviceTokenRef.orderByKey().limitToLast(1);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    String key = ds.getKey();
                    Log.d("TAG", key);

                    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("users");
                    rootRef.child(mAuth.getCurrentUser().getUid()).child("conversations").child(receiver_id).child("location").setValue(key);
                    rootRef.child(receiver_id).child("conversations").child(mAuth.getCurrentUser().getUid()).child("location").setValue(key);
                    if (token != null) {
                        sendNotification(token);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        query.addListenerForSingleValueEvent(eventListener);

    }

    private void getAllMessages(String location) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("conversations");
        rootRef.child(location).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                get_message_list = new ArrayList<>();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    boolean status = true;

                    Log.e("ds ", ds.getKey());
                    Log.e("ds2 ", ds.getValue().toString());


                    for (DataSnapshot dataSnapshot1 : ds.getChildren()) {
                        Log.e("dsssssss ", dataSnapshot1.getKey());
                        Log.e("dsssssss222 ", dataSnapshot1.getValue().toString());
                        if (dataSnapshot1.getKey().equalsIgnoreCase(mAuth.getCurrentUser().getUid()) && dataSnapshot1.getValue().toString().equalsIgnoreCase("1")) {
                            status = false;
                        }
                    }

                    if (status) {

                        UserChat userChat = ds.getValue(UserChat.class);
                        UserChat userChat1 = new UserChat();
                        String f_id = userChat.fromID;
                        String content = userChat.content;
                        String to_id = userChat.toID;
                        String type = userChat.type;
                        String property_id = userChat.property_id;
                        int timestamp1 = userChat.timestamp;

                        userChat1.setContent(content);
                        userChat1.setFromID(f_id);
                        userChat1.setToID(to_id);
                        userChat1.setType(type);
                        userChat1.setProperty_id(property_id);
                        get_message_list.add(userChat1);
                    }


                }

                Log.e("CHECK_RUN", " COUNT");
                adaper = new GetChatAdapter(ChatActivity.this, get_message_list);
                get_chat_recycler_view.setLayoutManager(new LinearLayoutManager(ChatActivity.this, LinearLayoutManager.VERTICAL, false));
                get_chat_recycler_view.setAdapter(adaper);
                get_chat_recycler_view.scrollToPosition(get_message_list.size() - 1);
                adaper.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

//================================== get chat adapter ===================================================================

    public class GetChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private Context context;
        private LayoutInflater inflater;
        public List<UserChat> userChatList;
        RecyclerView recyclerView;
        View view;

        public GetChatAdapter(Context context, List<UserChat> userChatList) {
            this.context = context;
            inflater = LayoutInflater.from(context);
            this.userChatList = userChatList;
            //Collections.reverse(userChatList);
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            view = inflater.inflate(R.layout.get_chat_layout_diglog, parent, false);
            MyHolder holder = new MyHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            Log.e("Dattasize***", "" + userChatList.size());
            final MyHolder myHolder = (MyHolder) holder;

            if (userChatList.size() > 0) {

                //if (userChatList.get(position).getProperty_id() != null && userChatList.get(position).getProperty_id().equalsIgnoreCase(property_id)) {

                if (userChatList.get(position).getFromID() != null && userChatList.get(position).getFromID().equalsIgnoreCase(mAuth.getCurrentUser().getUid())) {

                    myHolder.get_chat_sender_detail_layout.setVisibility(View.VISIBLE);
                    myHolder.get_chat_reciver_detail_layout.setVisibility(View.GONE);

                    if (userChatList.get(position).getType() != null && userChatList.get(position).getType().equalsIgnoreCase("text")) {
                        myHolder.get_chat_sender_cardview.setVisibility(View.GONE);
                        myHolder.sender_location_imageview.setVisibility(View.GONE);
                        myHolder.sender_relative_lay.setVisibility(View.VISIBLE);
                        myHolder.sender_text_message_textview.setText(userChatList.get(position).getContent());
                    }

                    if (userChatList.get(position).getType() != null && userChatList.get(position).getType().equalsIgnoreCase("photo")) {
                        myHolder.sender_relative_lay.setVisibility(View.GONE);
                        myHolder.sender_location_imageview.setVisibility(View.GONE);
                        myHolder.get_chat_sender_cardview.setVisibility(View.VISIBLE);
                        Picasso.with(context).load(userChatList.get(position).getContent()).into(myHolder.sender_imageview);
                    }

                    if (userChatList.get(position).getType() != null && userChatList.get(position).getType().equalsIgnoreCase("location")) {
                        myHolder.sender_relative_lay.setVisibility(View.GONE);
                        myHolder.get_chat_sender_cardview.setVisibility(View.GONE);
                        myHolder.sender_location_imageview.setVisibility(View.VISIBLE);

                    }


                } else {

                    myHolder.get_chat_reciver_detail_layout.setVisibility(View.VISIBLE);
                    myHolder.get_chat_sender_detail_layout.setVisibility(View.GONE);

                    if (userChatList.get(position).getType() != null && userChatList.get(position).getType().equalsIgnoreCase("text")) {
                        myHolder.get_chat_reciver_cardview.setVisibility(View.GONE);
                        myHolder.receiver_location_imageview.setVisibility(View.GONE);
                        myHolder.receiver_relative_lay.setVisibility(View.VISIBLE);
                        myHolder.reciver_text_message_textview.setText(userChatList.get(position).getContent());
                    }

                    if (userChatList.get(position).getType() != null && userChatList.get(position).getType().equalsIgnoreCase("photo")) {
                        myHolder.receiver_relative_lay.setVisibility(View.GONE);
                        myHolder.receiver_location_imageview.setVisibility(View.GONE);
                        myHolder.get_chat_reciver_cardview.setVisibility(View.VISIBLE);
                        Picasso.with(context).load(userChatList.get(position).getContent()).into(myHolder.receiver_imageview);
                    }

                    if (userChatList.get(position).getType() != null && userChatList.get(position).getType().equalsIgnoreCase("location")) {
                        myHolder.receiver_relative_lay.setVisibility(View.GONE);
                        myHolder.get_chat_reciver_cardview.setVisibility(View.GONE);
                        myHolder.receiver_location_imageview.setVisibility(View.VISIBLE);

                    }


                }

                myHolder.sender_location_imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String loc = userChatList.get(position).getContent();
                        Log.e("loc ", loc);
                        if (loc != null) {
                            List<String> elephantList = Arrays.asList(loc.split(":"));
                            String lat = elephantList.get(0);
                            String lon = elephantList.get(1);
                            Log.e("lattt !! ", lat);
                            Log.e("lonnnttt !! ", lon);
                            Intent intent = new Intent(getApplicationContext(), LocationActivity.class);
                            intent.putExtra("lat", lat);
                            intent.putExtra("lon", lon);
                            startActivity(intent);
                        }
                    }
                });

                myHolder.receiver_location_imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String loc = userChatList.get(position).getContent();
                        Log.e("loc ", loc);
                        if (loc != null) {
                            List<String> elephantList = Arrays.asList(loc.split(":"));
                            String lat = elephantList.get(0);
                            String lon = elephantList.get(1);
                            Log.e("lattt !! ", lat);
                            Log.e("lonnnttt !! ", lon);
                            Intent intent = new Intent(getApplicationContext(), LocationActivity.class);
                            intent.putExtra("lat", lat);
                            intent.putExtra("lon", lon);
                            startActivity(intent);
                        }
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return userChatList.size();
        }

        class MyHolder extends RecyclerView.ViewHolder {

            LinearLayout get_chat_sender_detail_layout, get_chat_reciver_detail_layout;
            RelativeLayout receiver_relative_lay, sender_relative_lay;
            TextView reciver_text_message_textview, sender_text_message_textview;
            CardView get_chat_reciver_cardview, get_chat_sender_cardview;
            ImageView receiver_imageview, receiver_location_imageview, sender_imageview, sender_location_imageview;

            // create constructor to get widget reference
            public MyHolder(View itemView) {
                super(itemView);

                get_chat_reciver_detail_layout = itemView.findViewById(R.id.get_chat_reciver_detail_layout);
                get_chat_sender_detail_layout = itemView.findViewById(R.id.get_chat_sender_detail_layout);
                receiver_relative_lay = itemView.findViewById(R.id.receiver_relative_lay);
                reciver_text_message_textview = itemView.findViewById(R.id.reciver_text_message_textview);
                get_chat_reciver_cardview = itemView.findViewById(R.id.get_chat_reciver_cardview);
                receiver_imageview = itemView.findViewById(R.id.receiver_imageview);
                receiver_location_imageview = itemView.findViewById(R.id.receiver_location_imageview);
                sender_relative_lay = itemView.findViewById(R.id.sender_relative_lay);
                sender_text_message_textview = itemView.findViewById(R.id.sender_text_message_textview);
                get_chat_sender_cardview = itemView.findViewById(R.id.get_chat_sender_cardview);
                sender_imageview = itemView.findViewById(R.id.sender_imageview);
                sender_location_imageview = itemView.findViewById(R.id.sender_location_imageview);

            }
        }

    }

    private String resizeAndCompressImageBeforeSend(Context context, String filePath, String fileName) {
        final int MAX_IMAGE_SIZE = 512 * 512; // max final file size in kilobytes

        // First decode with inJustDecodeBounds=true to check dimensions of image
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize(First we are going to resize the image to 800x800 image, in order to not have a big but very low quality image.
        //resizing the image will already reduce the file size, but after resizing we will check the file size and start to compress image
        options.inSampleSize = calculateInSampleSize(options, 800, 800);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        Bitmap bmpPic = BitmapFactory.decodeFile(filePath, options);


        int compressQuality = 100; // quality decreasing by 5 every loop.
        int streamLength;
        do {
            ByteArrayOutputStream bmpStream = new ByteArrayOutputStream();
            Log.d("compressBitmap", "Quality: " + compressQuality);
            bmpPic.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream);
            byte[] bmpPicByteArray = bmpStream.toByteArray();
            streamLength = bmpPicByteArray.length;
            compressQuality -= 5;
            Log.d("compressBitmap", "Size: " + streamLength / 1024 + " kb");
        } while (streamLength >= MAX_IMAGE_SIZE);

        try {
            //save the resized and compressed file to disk cache
            Log.d("compressBitmap", "cacheDir: " + context.getCacheDir());
            FileOutputStream bmpFile = new FileOutputStream(context.getCacheDir() + fileName);
            bmpPic.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpFile);
            bmpFile.flush();
            bmpFile.close();
        } catch (Exception e) {
            Log.e("compressBitmap", "Error on saving file");
        }
        //return the path of resized and compressed file
        return context.getCacheDir() + fileName;
    }


    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        String debugTag = "MemoryInformation";
        // Image nin islenmeden onceki genislik ve yuksekligi
        final int height = options.outHeight;
        final int width = options.outWidth;
        Log.d(debugTag, "image height: " + height + "---image width: " + width);
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        Log.d(debugTag, "inSampleSize: " + inSampleSize);
        return inSampleSize;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isVisible = false;
        check = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        check = false;
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

                            if (ChatActivity.this != null) {
                                Gson gson = new Gson();
                                PropertyDetailResponse propertyDetailResponse = gson.fromJson(responedata, PropertyDetailResponse.class);

                                chating_prop_name_textview.setText(propertyDetailResponse.getResult().get(0).getPropertyName());
                                chating_prop_rooms_textview.setText(propertyDetailResponse.getResult().get(0).getBeds() + getString(R.string.beds));
                                chating_prop_baths_textview.setText(propertyDetailResponse.getResult().get(0).getBaths() + getString(R.string.baths));
                                chating_prop_squre_text.setText(propertyDetailResponse.getResult().get(0).getSqFeet() + getString(R.string.m2));

                                Picasso.with(ChatActivity.this).load(propertyDetailResponse.getResult().get(0).getPropertyImages().get(0).getPropertyImage()).placeholder(R.drawable.p2).into(chating_prop_imageview);

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

    private void sendNotification(final String regToken) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    OkHttpClient client = new OkHttpClient();
                    JSONObject json = new JSONObject();
                    JSONObject dataJson = new JSONObject();
                    dataJson.put("body", "Pausi");
                    dataJson.put("title", "You have new chat message.");
                    json.put("notification", dataJson);
                    json.put("to", regToken);
                    RequestBody body = RequestBody.create(JSON, json.toString());
                    Request request = new Request.Builder()
                            .header("Authorization", "key=" + Constants.LEGACY_SERVER_KEY)
                            .url("https://fcm.googleapis.com/fcm/send")
                            .post(body)
                            .build();
                    Response response = client.newCall(request).execute();
                    String finalResponse = response.body().string();

                    Log.e("send noti >>> ", finalResponse);
                } catch (Exception e) {
                    Log.e("error in noti ", e.toString());
                }
                return null;
            }
        }.execute();

    }
}
