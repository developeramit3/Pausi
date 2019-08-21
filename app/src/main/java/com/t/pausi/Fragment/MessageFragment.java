package com.t.pausi.Fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.t.pausi.Activity.ChatActivity;
import com.t.pausi.Activity.HomeActivity;
import com.t.pausi.Bean.ConnectionDetector;
import com.t.pausi.Bean.MySharedPref;
import com.t.pausi.Bean.User;
import com.t.pausi.Bean.UserChat;
import com.t.pausi.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageFragment extends Fragment {
    RecyclerView message_recycler_view;
    ConnectionDetector cd;
    Boolean isInternetPresent = false;
    ProgressDialog progressDialog;
    MySharedPref sp;
    String ldata, logid, receiver_id = "", sender_id = "", user_id;
    Dialog dialog;
    ImageView message_frag_all_user_image;
    Button all_user_cancel_btn;
    Dialog dialog1;
    private FirebaseAuth mAuth;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    List<User> userList;
    List<User> userList1;
    GridView all_user_gridview;
    private ChildEventListener mMessageListener;
    ArrayList<String> al = new ArrayList<>();
    int totalUsers = 0, count = 0, dataposition;
    ImageView empty_imageview;
    User user1;
    Handler handler = new Handler();
    List<User> detail_list;
    List<User> message_list;
    private ArrayList<DataSnapshot> dataSnapshotArrayList;
    String key = "", name = "", profile = "";
    MessageAdapter adaper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.message_fragment_layout, container, false);

        cd = new ConnectionDetector(getActivity());
        isInternetPresent = cd.isConnectingToInternet();
        super.onStart();

        message_recycler_view = view.findViewById(R.id.message_recycler_view);
        message_frag_all_user_image = view.findViewById(R.id.message_frag_all_user_image);
        empty_imageview = view.findViewById(R.id.empty_imageview);

        Firebase.setAndroidContext(getActivity());
        mAuth = FirebaseAuth.getInstance();
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("users");


        //----------------------------- get firebase messges ----------------------

        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("users");
        if (mAuth != null && rootRef != null && mAuth.getCurrentUser().getUid() != null) {
            if (rootRef.child(mAuth.getCurrentUser().getUid()) != null && rootRef.child(mAuth.getCurrentUser().getUid()).child("conversations") != null) {
                rootRef.child(mAuth.getCurrentUser().getUid()).child("conversations").orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.e("running ! ", " >>> its running");
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                key = ds.getKey();

                                rootRef.child(mAuth.getCurrentUser().getUid()).child("conversations").child(key).orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Log.e("running ! ", " >>> its running");
                                        userList = new ArrayList<>();
                                        if (dataSnapshot.exists()) {
                                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                String key = ds.getKey();
                                                UserChat userChat = ds.getValue(UserChat.class);
                                                final String location = userChat.location;
                                                System.out.println("sssss = " + ds + "   userChat= " + userChat);
                                                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("conversations");
                                                rootRef.child(location).orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                            if (ds.exists()) {
                                                                boolean status = true;


                                                                for (DataSnapshot dataSnapshot1 : ds.getChildren()) {
                                                                    if (dataSnapshot1.getKey().equalsIgnoreCase(mAuth.getCurrentUser().getUid()) && dataSnapshot1.getValue().toString().equalsIgnoreCase("1")) {
                                                                        status = false;
                                                                    }
                                                                }

                                                                if (status) {
                                                                    Log.e("running2 ! ", " >>> its running2");
                                                                    UserChat userChat = ds.getValue(UserChat.class);
                                                                    String content = userChat.content;
                                                                    String type = userChat.type;
                                                                    String toid = userChat.toID;
                                                                    String fromid = userChat.fromID;
                                                                    String senderName = userChat.senderName;
                                                                    String senderProfile = userChat.senderProfile;
                                                                    String receiverName = userChat.receiverName;
                                                                    String receiverProfile = userChat.receiverProfile;
                                                                    String property_id = userChat.property_id;
                                                                    boolean isRead = userChat.isRead;


                                                                    user1 = new User();
                                                                    user1.setKey(fromid);
                                                                    user1.setReceiver_key(toid);
                                                                    user1.setProfileUrl(senderProfile);
                                                                    user1.setName(senderName);
                                                                    user1.setReceiver_name(receiverName);
                                                                    user1.setReceiver_profile(receiverProfile);
                                                                    user1.setRead(isRead);
                                                                    user1.setProp_id(property_id);
                                                                    user1.setLocation(location);

                                                                    System.out.println("detail = " + fromid + " " + toid + " " + senderName + " " + receiverName);
                                                                    if (user1 != null) {
                                                                        if (type.equalsIgnoreCase("text")) {

                                                                            user1.setMessage(content);
                                                                        }

                                                                        if (type.equalsIgnoreCase("photo")) {
                                                                            user1.setMessage("photo");
                                                                        }
                                                                        if (type.equalsIgnoreCase("location")) {
                                                                            user1.setMessage("location");
                                                                        }
                                                                    }

                                                                    userList.add(user1);
                                                                }

                                                            }


                                                            if (userList != null && userList.size() > 0) {
                                                                if (getActivity() != null) {
                                                                    Log.e("COME IN CONDITION", "TRUE");
                                                                    adaper = new MessageAdapter(getActivity(), userList);
                                                                    empty_imageview.setVisibility(View.GONE);
                                                                    message_recycler_view.setVisibility(View.VISIBLE);
                                                                    message_recycler_view.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                                                                    message_recycler_view.setAdapter(adaper);
                                                                    adaper.notifyDataSetChanged();
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

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        }


        //


        //--------------------------------- on click -----------------------------

        message_frag_all_user_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog1 = new Dialog(getActivity());
                dialog1.setContentView(R.layout.activity_all_firebase_user);
                dialog1.setCancelable(false);

                all_user_gridview = dialog1.findViewById(R.id.all_user_gridview);
                all_user_cancel_btn = dialog1.findViewById(R.id.all_user_cancel_btn);

                FirebaseUser user = mAuth.getCurrentUser();
                user_id = user.getUid();
                //addUserChangeListener();

                String url = "https://pausi-54aa2.firebaseio.com/users.json";

                StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        // doOnSuccess(s);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        System.out.println("" + volleyError);
                    }
                });

                RequestQueue rQueue = Volley.newRequestQueue(getActivity());
                rQueue.add(request);


                all_user_cancel_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog1.dismiss();
                    }
                });

                dialog1.show();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    //-------------------------------- news adapter ---------------------------

    public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private Context context;
        private LayoutInflater inflater;
        public List<User> userList1;
        RecyclerView recyclerView;
        View view;

        public MessageAdapter(Context context, List<User> userList) {
            this.context = context;
            inflater = LayoutInflater.from(context);
            this.userList1 = userList;
            //  notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            view = inflater.inflate(R.layout.message_pojo_layout, parent, false);
            MyHolder holder = new MyHolder(view);
            return holder;
        }

        @SuppressLint("ResourceAsColor")
        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

            System.out.println("Dattasize***" + userList1.size() + " .... " + userList.size());
            final MyHolder myHolder = (MyHolder) holder;
            if (userList1 != null && userList1.size() > 0) {

                System.out.print("MyMessage = = " + userList1.get(position).getName() + " message = " + userList1.get(position).getMessage());
                if (mAuth != null && mAuth.getCurrentUser().getUid() != null) {
                    if (mAuth.getCurrentUser().getUid().equalsIgnoreCase(userList1.get(position).getKey())) {
//                        if (userList1.get(position).isRead() == false) {
//                            myHolder.message_text_message.setTextColor(Color.parseColor("#a773ec"));
//                        }
                        myHolder.message_text_username.setText(userList1.get(position).getReceiver_name());
                        myHolder.message_text_message.setText(userList1.get(position).getMessage());
                        final String[] okFileExtensions = new String[]{"jpg", "png", "gif", "jpeg"};
                        for (String extension : okFileExtensions) {
                            if (userList1.get(position).getReceiver_profile() != null && userList1.get(position).getProfileUrl().toLowerCase().endsWith(extension)) {
                                Glide.with(getActivity()).load(userList1.get(position).getReceiver_profile()).into(myHolder.get_conversation_profile_image);
                            }
                        }
                        System.out.print("reciverMessage = = " + userList1.get(position).getReceiver_name() + " message = " + userList1.get(position).getMessage());

                    } else {
//                        if (userList1.get(position).isRead() == false) {
//                            myHolder.message_text_message.setTextColor(Color.parseColor("#a773ec"));
//                        }
                        myHolder.message_text_username.setText(userList1.get(position).getName());
                        myHolder.message_text_message.setText(userList1.get(position).getMessage());
                        final String[] okFileExtensions = new String[]{"jpg", "png", "gif", "jpeg"};
                        for (String extension : okFileExtensions) {
                            if (userList1.get(position).getProfileUrl() != null && userList1.get(position).getProfileUrl().toLowerCase().endsWith(extension)) {
                                Glide.with(getActivity()).load(userList1.get(position).getProfileUrl()).into(myHolder.get_conversation_profile_image);
                            }
                        }


                    }
                }
                myHolder.delete_chat_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (userList1.get(position).getLocation() != null) {
                            if (context != null) {
                                AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(context);
                                dialog.setMessage(R.string.suredeletechat);
                                dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                        Log.e("hello >>>", "....");
                                        final String location = userList1.get(position).getLocation();
                                        Log.e("location for ", userList1.get(position).getLocation());

                                        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("conversations");
                                        rootRef.child(location).orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                    if (ds.exists()) {
                                                        String key = ds.getKey();
                                                        Log.e("running2 ! ", " >>> its running2");
                                                        UserChat userChat = ds.getValue(UserChat.class);
                                                        String content = userChat.content;
                                                        String type = userChat.type;
                                                        String toid = userChat.toID;
                                                        String fromid = userChat.fromID;
                                                        String senderName = userChat.senderName;
                                                        String senderProfile = userChat.senderProfile;
                                                        String receiverName = userChat.receiverName;
                                                        String receiverProfile = userChat.receiverProfile;
                                                        String property_id = userChat.property_id;
                                                        boolean isRead = userChat.isRead;
                                                        rootRef.child(location).child(key).child(mAuth.getCurrentUser().getUid()).setValue("1");
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });

                                        userList1.remove(position);
                                        notifyDataSetChanged();

                                        //get gps
                                    }
                                });
                                dialog.setNegativeButton(
                                        "No", new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                                // TODO Auto-generated method stub

                                            }
                                        });
                                dialog.show();


                            }


                        }
                    }
                });


                myHolder.get_conversation_card_view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dataposition = position;

                        if (mAuth != null && mAuth.getCurrentUser().getUid() != null) {
                            if (mAuth.getCurrentUser().getUid().equalsIgnoreCase(userList1.get(position).getKey())) {


                                String receiver_id = userList1.get(position).getReceiver_key();
                                String username = userList1.get(position).getReceiver_name();
                                String userprofile = userList1.get(position).getReceiver_profile();
                                String prop_id = userList1.get(position).getProp_id();
                                Intent intent = new Intent(getActivity(), ChatActivity.class);
                                intent.putExtra("receiver_id", receiver_id);
                                intent.putExtra("username", username);
                                intent.putExtra("userprofile", userprofile);
                                intent.putExtra("property_id", prop_id);
                                startActivity(intent);
                            } else {
                                String receiver_id = userList1.get(position).getKey();
                                String username = userList1.get(position).getName();
                                String userprofile = userList1.get(position).getProfileUrl();
                                String prop_id = userList1.get(position).getProp_id();
                                Intent intent = new Intent(getActivity(), ChatActivity.class);

                                intent.putExtra("receiver_id", receiver_id);
                                intent.putExtra("username", username);
                                intent.putExtra("userprofile", userprofile);
                                intent.putExtra("property_id", prop_id);
                                startActivity(intent);
                            }
                        }


                    }
                });


            }
        }

        @Override
        public int getItemCount() {
            return userList1 == null ? 0 : userList1.size();
        }

        class MyHolder extends RecyclerView.ViewHolder {
            CircleImageView get_conversation_profile_image;
            TextView message_time_text, message_text_message, message_text_username;
            CardView get_conversation_card_view;
            ImageView delete_chat_image;

            // create constructor to get widget reference
            public MyHolder(View itemView) {
                super(itemView);
                get_conversation_profile_image = itemView.findViewById(R.id.get_conversation_profile_image);
                message_time_text = itemView.findViewById(R.id.message_time_text);
                message_text_message = itemView.findViewById(R.id.message_text_message);
                get_conversation_card_view = itemView.findViewById(R.id.get_conversation_card_view);
                delete_chat_image = itemView.findViewById(R.id.delete_chat_image);
                message_text_username = itemView.findViewById(R.id.message_text_username);

            }

        }

    }
}
