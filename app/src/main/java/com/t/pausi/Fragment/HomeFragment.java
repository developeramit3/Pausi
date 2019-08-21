package com.t.pausi.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
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
import com.t.pausi.Activity.FilterActivity;
import com.t.pausi.Activity.HomeActivity;
import com.t.pausi.Activity.LoginActivity;
import com.t.pausi.Activity.PropertyDetailActivity;
import com.t.pausi.Bean.AlertConnection;
import com.t.pausi.Bean.ConnectionDetector;
import com.t.pausi.Bean.DataHolder;
import com.t.pausi.Bean.GPSTracker;
import com.t.pausi.Bean.MySharedPref;
import com.t.pausi.Bean.User;
import com.t.pausi.Interface.Config;
import com.t.pausi.Interface.UserInterface;
import com.t.pausi.Pojo.AgentDetails;
import com.t.pausi.Pojo.PropertyDetails;
import com.t.pausi.Pojo.PropertyImage;
import com.t.pausi.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment implements OnMapReadyCallback {
    public static GoogleMap mMap;
    GPSTracker tracker;
    double P_latitude, P_longitude;
    String lat, lon;
    Marker marker_start, marker1;
    LinearLayout zoom_out_home_lay, zoom_in_home_lay;
    ImageView current_location_image, map_back_in_image, home_camera_image, home_direction_imageview,
            home_school_imageview, home_satelite_imageview, home_normal_map_imageview, home_school1_imageview;
    SupportMapFragment mapFragment;
    CardView zoom_card_view;
    RelativeLayout rr;
    ProgressDialog progressDialog;
    MySharedPref sp;
    String ldata, logid = "", property_price, sale_type, property_image;
    BitmapDescriptor icon;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    LatLngBounds latLngBounds;
    List<DataHolder> property_list;
    List<PropertyImage> property_image_list;
    PropertyDetails propertyDetails;
    AgentDetails agentDetails;
    DataHolder dataHolder;
    RecyclerView home_property_recycler_view;
    private View.OnTouchListener mListener;
    NestedScrollView root_lay;
    Handler h = new Handler();
    int delay = 1 * 1000;
    Runnable runnable;
    Boolean stop = true;
    EditText home_search_edittext;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    TextView home_filter_text;
    boolean count = true, count1 = true, count2 = true, count3 = true;
    String property_id = "", fav = "";
    boolean status = true;
    static Boolean Is_MAP_Moveable = false;
    Projection projection;
    static PolygonOptions rectOptions;
    public static Polygon polygon;
    public static Polyline polyline;
    double latitude, longitude;
    Button cancel_drawing_btn;
    static LatLng firstGeoPoint;
    private GestureDetector mGestureDetector;
    public static ArrayList<LatLng> mLatlngs, latlong_newlist;
    private FirebaseAuth mAuth;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment_layout, container, false);

        //------------------------ get current location ---------------

        tracker = new GPSTracker(getActivity());
        if (tracker.canGetLocation()) {
            P_latitude = tracker.getLatitude();
            P_longitude = tracker.getLongitude();
            lat = String.valueOf(P_latitude);
            lon = String.valueOf(P_longitude);
            System.out.println("----------------------" + P_latitude);
            System.out.println("----------------------" + P_longitude);
        }

        //-------------------------------- get login id -----------------------------------

        sp = new MySharedPref();
        ldata = sp.getData(getActivity(), "ldata", "null");
        if (ldata != null || !ldata.equalsIgnoreCase("null") || !ldata.equals("")) {
            try {
                JSONObject jsonObject = new JSONObject(ldata);
                logid = jsonObject.getString("id");
                Log.e("logid ", " " + logid);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //--------------------- connection detector -----------------------------------

        cd = new ConnectionDetector(getActivity());
        isInternetPresent = cd.isConnectingToInternet();
        super.onStart();


        //-------------------------- find view -------------------------------------

        zoom_out_home_lay = view.findViewById(R.id.zoom_out_home_lay);
        zoom_in_home_lay = view.findViewById(R.id.zoom_in_home_lay);
        current_location_image = view.findViewById(R.id.current_location_image);
        map_back_in_image = view.findViewById(R.id.map_back_in_image);
        zoom_card_view = view.findViewById(R.id.zoom_card_view);
        rr = view.findViewById(R.id.rr);
        home_property_recycler_view = view.findViewById(R.id.home_property_recycler_view);
        root_lay = view.findViewById(R.id.root_lay);
        home_search_edittext = view.findViewById(R.id.home_search_edittext);
        home_filter_text = view.findViewById(R.id.home_filter_text);
        home_camera_image = view.findViewById(R.id.home_camera_image);
        home_direction_imageview = view.findViewById(R.id.home_direction_imageview);
        home_school_imageview = view.findViewById(R.id.home_school_imageview);
        home_satelite_imageview = view.findViewById(R.id.home_satelite_imageview);
        home_normal_map_imageview = view.findViewById(R.id.home_normal_map_imageview);
        home_school1_imageview = view.findViewById(R.id.home_school1_imageview);
        cancel_drawing_btn = view.findViewById(R.id.cancel_drawing_btn);
        home_property_recycler_view.setNestedScrollingEnabled(false);

        Firebase.setAndroidContext(getActivity());
        mAuth = FirebaseAuth.getInstance();
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("users");


        //------------------------- init map -----------------------------------------

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        icon = BitmapDescriptorFactory.fromResource(R.drawable.prop_marker);

        //------------------------------- all property list  --------------------------------

        if (HomeActivity.fkey != null && HomeActivity.fkey.equalsIgnoreCase("Filter")) {
            if (isInternetPresent) {
                Log.e("p_type ", HomeActivity.ptype);
                Log.e("beds_to ", HomeActivity.to_beds);
                Log.e("beds_from ", HomeActivity.from_beds);
                Log.e("baths_to ", HomeActivity.to_baths);
                Log.e("baths_from ", HomeActivity.from_baths);
                Log.e("price_to ", HomeActivity.to_price);
                Log.e("price_from ", HomeActivity.from_price);
                Log.e("status ", HomeActivity.fstatus);
                Log.e("date ", HomeActivity.fdate);
                Log.e("sort ", HomeActivity.fsort);
                Log.e("p_amenities ", HomeActivity.p_amenities);
                FilterPropertyList_call();
            } else {
                AlertConnection.showAlertDialog(getActivity(), "No Internet Connection",
                        "You don't have internet connection.", false);
            }
        } else {
            if (isInternetPresent) {
                PropertyList_call();
            } else {
                AlertConnection.showAlertDialog(getActivity(), "No Internet Connection",
                        "You don't have internet connection.", false);
            }
        }


        //---------------------------- on click -----------------------------------------

        home_search_edittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .build(getActivity());
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }

            }
        });

        zoom_out_home_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LatLng latLng = new LatLng(P_latitude, P_longitude);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
            }
        });

        zoom_in_home_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LatLng latLng = new LatLng(P_latitude, P_longitude);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19));
            }
        });

        home_camera_image.setOnClickListener(new View.OnClickListener() {
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

        home_school_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (count) {
                    home_satelite_imageview.setVisibility(View.VISIBLE);
                    home_normal_map_imageview.setVisibility(View.VISIBLE);
                    home_school1_imageview.setVisibility(View.VISIBLE);
                    count = false;
                } else {

                    home_satelite_imageview.setVisibility(View.GONE);
                    home_normal_map_imageview.setVisibility(View.GONE);
                    home_school1_imageview.setVisibility(View.GONE);
                    if (mMap != null) {
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        LatLng latLng = new LatLng(P_latitude, P_longitude);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
                    }
                    count = true;
                }


            }
        });

        home_satelite_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMap != null) {

                    if (count1) {
                        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        home_satelite_imageview.setImageResource(R.drawable.home_satellite1);
                        home_normal_map_imageview.setImageResource(R.drawable.home_location);
                        home_school1_imageview.setImageResource(R.drawable.home_school);
                        count1 = false;
                    } else {
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        home_satelite_imageview.setImageResource(R.drawable.home_satellite);
                        home_normal_map_imageview.setImageResource(R.drawable.home_location);
                        home_school1_imageview.setImageResource(R.drawable.home_school);
                        count1 = true;
                    }
                }
            }
        });

        home_normal_map_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMap != null) {

                    if (count2) {
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        home_normal_map_imageview.setImageResource(R.drawable.home_location1);
                        home_satelite_imageview.setImageResource(R.drawable.home_satellite);
                        home_school1_imageview.setImageResource(R.drawable.home_school);
                        LatLng latLng = new LatLng(P_latitude, P_longitude);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19));
                        count2 = false;
                    } else {
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        home_normal_map_imageview.setImageResource(R.drawable.home_location);
                        home_satelite_imageview.setImageResource(R.drawable.home_satellite);
                        home_school1_imageview.setImageResource(R.drawable.home_school);
                        LatLng latLng = new LatLng(P_latitude, P_longitude);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
                        count2 = true;
                    }
                }
            }
        });

        home_school1_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (count3) {
                    home_normal_map_imageview.setImageResource(R.drawable.home_location);
                    home_satelite_imageview.setImageResource(R.drawable.home_satellite);
                    home_school1_imageview.setImageResource(R.drawable.home_school2);
                    if (mMap != null) {
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    }
                    final Dialog dialog = new Dialog(getActivity());
                    dialog.setContentView(R.layout.home_school_layout);
                    Button btn = dialog.findViewById(R.id.home_school_btn);
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                    count3 = false;
                } else {
                    home_normal_map_imageview.setImageResource(R.drawable.home_location);
                    home_satelite_imageview.setImageResource(R.drawable.home_satellite);
                    home_school1_imageview.setImageResource(R.drawable.home_school1);

                    if (mMap != null) {
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    }
                    count3 = true;
                }

            }
        });

        home_direction_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMap != null) {
                    Is_MAP_Moveable = true;
                    mMap.getUiSettings().setScrollGesturesEnabled(false);
                    mMap.getUiSettings().setZoomGesturesEnabled(false);
                    cancel_drawing_btn.setVisibility(View.VISIBLE);

                }
            }
        });

        cancel_drawing_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMap != null) {
                    mMap.getUiSettings().setScrollGesturesEnabled(true);
                    mMap.getUiSettings().setZoomGesturesEnabled(true);
                    Is_MAP_Moveable = false;
                    cancel_drawing_btn.setVisibility(View.GONE);
                    mMap.clear();

                    if (polyline != null) {
                        polyline.remove();
                    }
                    if (polygon != null) {
                        polygon.remove();
                    }

                    if (latlong_newlist != null) {
                        latlong_newlist.clear();
                    }
                    PropertyList_call2();


                }
            }
        });

        current_location_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMap == null) {

                } else {
                    Location loc = mMap.getMyLocation();
                    if (loc != null) {
                        LatLng latLang = new LatLng(loc.getLatitude(), loc
                                .getLongitude());
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLang, 18);
                        mMap.animateCamera(cameraUpdate);

                    }

                }
            }
        });

        map_back_in_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMap != null) {
                    mMap.getUiSettings().setScrollGesturesEnabled(false);
                    mMap.getUiSettings().setZoomGesturesEnabled(false);
                    root_lay.setVisibility(View.VISIBLE);
                    map_back_in_image.setVisibility(View.GONE);
                    home_school_imageview.setVisibility(View.GONE);
                    home_direction_imageview.setVisibility(View.GONE);
                    Is_MAP_Moveable = false;
                    cancel_drawing_btn.setVisibility(View.GONE);

                    if (latlong_newlist != null && latlong_newlist.size() > 0) {
                        Log.e("call here", "polyline call");
                        Log.e("list ki size", "" + latlong_newlist.size());
                        PropertyListWithPolygon_call();
                    } else {

                        if (isInternetPresent) {
                            Log.e("call here", "Normal call");
                            PropertyList_call();
                        } else {
                            AlertConnection.showAlertDialog(getActivity(), "No Internet Connection",
                                    "You don't have internet connection.", false);
                        }
                    }
                    rr.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 560));

                }
            }
        });

        home_filter_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), FilterActivity.class);
                startActivity(intent);
            }
        });


        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

            if (HomeActivity.fkey != null && HomeActivity.fkey.equalsIgnoreCase("Filter")) {
                if (isInternetPresent) {
                    FilterPropertyList_call();
                } else {
                    AlertConnection.showAlertDialog(getActivity(), "No Internet Connection",
                            "You don't have internet connection.", false);
                }
            } else {
                if (isInternetPresent) {
                    PropertyList_call();
                } else {
                    AlertConnection.showAlertDialog(getActivity(), "No Internet Connection",
                            "You don't have internet connection.", false);
                }

            }
        } else {

            Log.e("cheekckk2", " kkkkkkkkkkkkkkkkkk2");
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                // buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mMap.getUiSettings().setScrollGesturesEnabled(false);
                mMap.getUiSettings().setZoomGesturesEnabled(false);
                if (marker_start != null) {
                    marker_start.remove();
                }
                LatLng latLng = new LatLng(P_latitude, P_longitude);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Current Position");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                marker_start = mMap.addMarker(markerOptions);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));


            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            //buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            if (marker_start != null) {
                marker_start.remove();
            }
            LatLng latLng = new LatLng(P_latitude, P_longitude);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Position");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            marker_start = mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));


            //latLngBounds = this.mMap.getProjection().getVisibleRegion().latLngBounds;
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                //imageView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
                mMap.getUiSettings().setScrollGesturesEnabled(true);
                mMap.getUiSettings().setZoomGesturesEnabled(true);
                //stop = false;
                root_lay.setVisibility(View.GONE);
                zoom_card_view.setVisibility(View.GONE);
                map_back_in_image.setVisibility(View.VISIBLE);
                home_school_imageview.setVisibility(View.VISIBLE);
                home_direction_imageview.setVisibility(View.VISIBLE);
                home_property_recycler_view.setVisibility(View.GONE);
                rr.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
                //current_location_image.setLayoutParams(new RelativeLayout.LayoutParams(50,50));
            }
        });


    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(getActivity())
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        mMap.setMyLocationEnabled(true);
                    }

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getActivity(), "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                Log.e("Place: ", "" + place.getName());
                home_search_edittext.setText(place.getAddress());

                LatLng latLng = place.getLatLng();
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 14);
                mMap.animateCamera(cameraUpdate);
                PropertyList_call();


            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                // TODO: Handle the error.
                Log.e("TAG ", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }


    public static void removePoly(MotionEvent event) {
        latlong_newlist = new ArrayList<>();
        if (polyline != null) {
            polyline.remove();
            polyline = null;
        }
        polyline = mMap.addPolyline(new PolylineOptions().color(Color.RED).width(5).addAll(latlong_newlist));
        int X1 = (int) event.getX();
        int Y1 = (int) event.getY();
        Point point = new Point();
        point.x = X1;
        point.y = Y1;
        firstGeoPoint = mMap.getProjection().fromScreenLocation(
                point);
    }

    public static void addPolyline(MotionEvent motionEvent) {

        if (Is_MAP_Moveable == true) {
            LatLng position = mMap.getProjection().fromScreenLocation(new Point((int) motionEvent.getX(), (int) motionEvent.getY()));
            latlong_newlist.add(position);
            polyline.setPoints(latlong_newlist);
            polyline = mMap.addPolyline(new PolylineOptions().color(Color.RED).width(5).addAll(latlong_newlist));
        }

    }

    public static void close_polyline() {
        if (Is_MAP_Moveable == true) {
            latlong_newlist.add(firstGeoPoint);
            rectOptions = new PolygonOptions();
            rectOptions.fillColor(Color.GRAY);
            rectOptions.strokeColor(Color.RED);
            rectOptions.strokeWidth(5);
            rectOptions.addAll(latlong_newlist);
            polygon = mMap.addPolygon(rectOptions);
            Is_MAP_Moveable = false;
        }
    }
    //=============================  get property list ==========================

    private void PropertyList_call() {
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
        Call<ResponseBody> resultCall = signupInterface.all_listing_list(logid, "ALL");
        resultCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    try {

                        zoom_card_view.setVisibility(View.VISIBLE);
                        //home_property_recycler_view.setVisibility(View.VISIBLE);

                        String responedata = response.body().string();
                        Log.e("all property list ", "" + responedata);
                        JSONObject object = new JSONObject(responedata);
                        String error = object.getString("status");

                        if (error.equals("1")) {
                            JSONArray jsnarry = object.getJSONArray("result");
                            int index = 0;
                            property_list = new ArrayList<>();
                            for (int i = 0; i < jsnarry.length(); i++) {

                                JSONObject jsonobject = jsnarry.getJSONObject(i);
                                String lat = jsonobject.getString("lat");
                                String lon = jsonobject.getString("lon");

                                if (lat != null && !lat.equalsIgnoreCase("")) {
                                    LatLng latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
                                    if (latLng != null) {
                                        if (mMap != null) {
                                            marker1 = mMap.addMarker(new MarkerOptions().position(latLng).flat(true).anchor(0.5f, 0.5f).icon(icon));

                                            latLngBounds = mMap.getProjection().getVisibleRegion().latLngBounds;

                                            if (polygon != null) {
                                                if (polygon.isVisible() && polygon.getPoints().size() > 0) {
                                                    for (int j = 0; j < polygon.getPoints().size(); j++) {

                                                    }

                                                }
                                            }


                                            if (latLngBounds.contains(latLng) == true) {
                                                zoom_card_view.setVisibility(View.GONE);
                                                home_property_recycler_view.setVisibility(View.VISIBLE);
                                                dataHolder = new DataHolder();
                                                propertyDetails = new PropertyDetails();
                                                agentDetails = new AgentDetails();
                                                String id = jsonobject.getString("id");
                                                property_price = jsonobject.getString("property_price");
                                                sale_type = jsonobject.getString("sale_type");
                                                String sq_feet = jsonobject.getString("sq_feet");
                                                String beds = jsonobject.getString("beds");
                                                String baths = jsonobject.getString("baths");
                                                String address = jsonobject.getString("address");
                                                String acre_area = jsonobject.getString("acre_area");
                                                String status = jsonobject.getString("status");


                                                JSONArray jsonArray = jsonobject.getJSONArray("property_images");
                                                property_image_list = new ArrayList<>();
                                                for (int j = 0; j < jsonArray.length(); j++) {

                                                    PropertyImage propertyImage = new PropertyImage();
                                                    property_image = jsonArray.getJSONObject(j).getString("property_image");
                                                    propertyImage.setPropertyImage(property_image);
                                                    property_image_list.add(propertyImage);
                                                }

                                                dataHolder.setProperty_price(property_price);
                                                dataHolder.setProperty_sale_type(status);
                                                dataHolder.setPropertyImageList(property_image_list);
                                                propertyDetails.setId(id);
                                                propertyDetails.setAcreArea(acre_area);
                                                propertyDetails.setAddress(address);
                                                propertyDetails.setBaths(baths);
                                                propertyDetails.setBeds(beds);
                                                propertyDetails.setPropertyPrice(property_price);
                                                propertyDetails.setSaleType(sale_type);
                                                propertyDetails.setSqFeet(sq_feet);
                                                dataHolder.setPropertyDetails(propertyDetails);

                                                JSONObject jsonObject1 = jsonobject.getJSONObject("agent_details");
                                                String agent_id = jsonObject1.getString("id");
                                                String fname = jsonObject1.getString("first_name");
                                                String lname = jsonObject1.getString("last_name");
                                                String email = jsonObject1.getString("email");
                                                String image = jsonObject1.getString("image");

                                                agentDetails.setId(id);
                                                agentDetails.setFirstName(fname);
                                                agentDetails.setLastName(lname);
                                                agentDetails.setEmail(email);
                                                agentDetails.setImage(image);
                                                dataHolder.setAgentDetails(agentDetails);
                                                property_list.add(dataHolder);

                                                if (getActivity() != null) {
                                                    Gson gson = new Gson();
                                                    HomePropertyAdapter adaper = new HomePropertyAdapter(getActivity(), property_list);
                                                    zoom_card_view.setVisibility(View.GONE);
                                                    home_property_recycler_view.setVisibility(View.VISIBLE);
                                                    home_property_recycler_view.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                                                    home_property_recycler_view.setAdapter(adaper);
                                                    adaper.notifyDataSetChanged();

                                                }


                                            } else {
//                                                 home_property_recycler_view.setVisibility(View.GONE);
//                                                 zoom_card_view.setVisibility(View.VISIBLE);
                                            }

                                            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
                                        }
                                    }
                                }

                            }

                        } else {
                            String result = object.getString("result");
                            if (getActivity() != null) {
                                Toast.makeText(getActivity(), "" + result, Toast.LENGTH_SHORT).show();
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
                if (mMap == null) {

                } else {
                    mMap.clear();
                }
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), "Server Problem Please try Next time...!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //=============================  get property list by polygon ==========================

    private void PropertyListWithPolygon_call() {
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
        Call<ResponseBody> resultCall = signupInterface.all_listing_list(logid, "ALL");
        resultCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    try {

                        zoom_card_view.setVisibility(View.VISIBLE);
                        // home_property_recycler_view.setVisibility(View.VISIBLE);
                        String responedata = response.body().string();
                        Log.e("all property list ", "" + responedata);
                        JSONObject object = new JSONObject(responedata);
                        String error = object.getString("status");

                        if (error.equals("1")) {
                            JSONArray jsnarry = object.getJSONArray("result");
                            int index = 0;
                            property_list = new ArrayList<>();
                            for (int i = 0; i < jsnarry.length(); i++) {

                                JSONObject jsonobject = jsnarry.getJSONObject(i);
                                String lat = jsonobject.getString("lat");
                                String lon = jsonobject.getString("lon");

                                if (lat != null && !lat.equalsIgnoreCase("")) {
                                    LatLng latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
                                    if (latLng != null) {
                                        if (mMap != null) {
                                            marker1 = mMap.addMarker(new MarkerOptions().position(latLng).flat(true).anchor(0.5f, 0.5f).icon(icon));

                                            boolean ispoly = isPointInPolygon(latLng, latlong_newlist);

                                            if (ispoly == true) {
                                                zoom_card_view.setVisibility(View.GONE);
                                                home_property_recycler_view.setVisibility(View.VISIBLE);
                                                dataHolder = new DataHolder();
                                                propertyDetails = new PropertyDetails();
                                                agentDetails = new AgentDetails();
                                                String id = jsonobject.getString("id");
                                                property_price = jsonobject.getString("property_price");
                                                sale_type = jsonobject.getString("sale_type");
                                                String sq_feet = jsonobject.getString("sq_feet");
                                                String beds = jsonobject.getString("beds");
                                                String baths = jsonobject.getString("baths");
                                                String address = jsonobject.getString("address");
                                                String acre_area = jsonobject.getString("acre_area");
                                                String status = jsonobject.getString("status");


                                                JSONArray jsonArray = jsonobject.getJSONArray("property_images");
                                                property_image_list = new ArrayList<>();
                                                for (int k = 0; k < jsonArray.length(); k++) {

                                                    PropertyImage propertyImage = new PropertyImage();
                                                    property_image = jsonArray.getJSONObject(k).getString("property_image");
                                                    propertyImage.setPropertyImage(property_image);
                                                    property_image_list.add(propertyImage);
                                                }

                                                dataHolder.setProperty_price(property_price);
                                                dataHolder.setProperty_sale_type(status);
                                                dataHolder.setPropertyImageList(property_image_list);
                                                propertyDetails.setId(id);
                                                propertyDetails.setAcreArea(acre_area);
                                                propertyDetails.setAddress(address);
                                                propertyDetails.setBaths(baths);
                                                propertyDetails.setBeds(beds);
                                                propertyDetails.setPropertyPrice(property_price);
                                                propertyDetails.setSaleType(sale_type);
                                                propertyDetails.setSqFeet(sq_feet);
                                                dataHolder.setPropertyDetails(propertyDetails);

                                                JSONObject jsonObject1 = jsonobject.getJSONObject("agent_details");
                                                String agent_id = jsonObject1.getString("id");
                                                String fname = jsonObject1.getString("first_name");
                                                String lname = jsonObject1.getString("last_name");
                                                String email = jsonObject1.getString("email");
                                                String image = jsonObject1.getString("image");

                                                agentDetails.setId(agent_id);
                                                agentDetails.setFirstName(fname);
                                                agentDetails.setLastName(lname);
                                                agentDetails.setEmail(email);
                                                agentDetails.setImage(image);
                                                dataHolder.setAgentDetails(agentDetails);
                                                property_list.add(dataHolder);

                                                if (getActivity() != null) {
                                                    Gson gson = new Gson();
                                                    HomePropertyAdapter adaper = new HomePropertyAdapter(getActivity(), property_list);
                                                    zoom_card_view.setVisibility(View.GONE);
                                                    home_property_recycler_view.setVisibility(View.VISIBLE);
                                                    home_property_recycler_view.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                                                    home_property_recycler_view.setAdapter(adaper);
                                                    adaper.notifyDataSetChanged();

                                                }
                                            }


                                        }


                                    } else {
//                                                 home_property_recycler_view.setVisibility(View.GONE);
//                                                 zoom_card_view.setVisibility(View.VISIBLE);


                                        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));

                                    }
                                }

                            }

                        } else {
                            String result = object.getString("result");
                            if (getActivity() != null) {
                                Toast.makeText(getActivity(), "" + result, Toast.LENGTH_SHORT).show();
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
                if (mMap == null) {

                } else {
                    mMap.clear();
                }
                Toast.makeText(getActivity(), "Server Problem Please try Next time...!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private boolean isPointInPolygon(LatLng tap, ArrayList<LatLng> vertices) {
        int intersectCount = 0;
        for (int j = 0; j < vertices.size() - 1; j++) {
            if (rayCastIntersect(tap, vertices.get(j), vertices.get(j + 1))) {
                intersectCount++;
            }
        }

        return ((intersectCount % 2) == 1); // odd = inside, even = outside;
    }

    private boolean rayCastIntersect(LatLng tap, LatLng vertA, LatLng vertB) {

        double aY = vertA.latitude;
        double bY = vertB.latitude;
        double aX = vertA.longitude;
        double bX = vertB.longitude;
        double pY = tap.latitude;
        double pX = tap.longitude;

        if ((aY > pY && bY > pY) || (aY < pY && bY < pY)
                || (aX < pX && bX < pX)) {
            return false; // a and b can't both be above or below pt.y, and a or
            // b must be east of pt.x
        }

        double m = (aY - bY) / (aX - bX); // Rise over run
        double bee = (-aX) * m + aY; // y = mx + b
        double x = (pY - bee) / m; // algebra is neat!

        return x > pX;
    }

    private void PropertyList_call2() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.Base_Url).client(client)
                .addConverterFactory(GsonConverterFactory.create(new Gson())).build();
        UserInterface signupInterface = retrofit.create(UserInterface.class);
        Call<ResponseBody> resultCall = signupInterface.all_listing_list(logid, "ALL");
        resultCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {

                        String responedata = response.body().string();
                        Log.e("all property list ", "" + responedata);
                        JSONObject object = new JSONObject(responedata);
                        String error = object.getString("status");

                        if (error.equals("1")) {
                            JSONArray jsnarry = object.getJSONArray("result");
                            int index = 0;
                            property_list = new ArrayList<>();
                            for (int i = 0; i < jsnarry.length(); i++) {

                                JSONObject jsonobject = jsnarry.getJSONObject(i);
                                String lat = jsonobject.getString("lat");
                                String lon = jsonobject.getString("lon");

                                if (lat != null && !lat.equalsIgnoreCase("")) {
                                    LatLng latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
                                    if (latLng != null) {
                                        if (mMap != null) {
                                            marker1 = mMap.addMarker(new MarkerOptions().position(latLng).flat(true).anchor(0.5f, 0.5f).icon(icon));

                                        }
                                    }
                                }

                            }

                        } else {
                            String result = object.getString("result");
                            if (getActivity() != null) {
                                Toast.makeText(getActivity(), "" + result, Toast.LENGTH_SHORT).show();
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
                if (mMap == null) {

                } else {
                    mMap.clear();
                }
                Toast.makeText(getActivity(), "Server Problem Please try Next time...!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    //=============================  get filter  property list ==========================

    private void FilterPropertyList_call() {
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
        Call<ResponseBody> resultCall = signupInterface.all_listing_list2(logid, "FILTER", HomeActivity.ptype, HomeActivity.sale_type, HomeActivity.from_price, HomeActivity.to_price, HomeActivity.from_beds, HomeActivity.to_beds, HomeActivity.from_baths, HomeActivity.to_baths, HomeActivity.fsort, HomeActivity.fdate, HomeActivity.fstatus, HomeActivity.p_amenities);
        resultCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    try {

                        String responedata = response.body().string();
                        Log.e("filter property list ", "" + responedata);
                        JSONObject object = new JSONObject(responedata);
                        String error = object.getString("status");

                        if (error.equals("1")) {
                            JSONArray jsnarry = object.getJSONArray("result");
                            int index = 0;
                            property_list = new ArrayList<>();
                            for (int i = 0; i < jsnarry.length(); i++) {

                                JSONObject jsonobject = jsnarry.getJSONObject(i);
                                String lat = jsonobject.getString("lat");
                                String lon = jsonobject.getString("lon");

                                if (lat != null && !lat.equalsIgnoreCase("")) {
                                    LatLng latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
                                    if (latLng != null) {
                                        if (mMap != null) {
                                            marker1 = mMap.addMarker(new MarkerOptions().position(latLng).flat(true).anchor(0.5f, 0.5f).icon(icon));

                                            dataHolder = new DataHolder();
                                            propertyDetails = new PropertyDetails();
                                            agentDetails = new AgentDetails();
                                            property_price = jsonobject.getString("property_price");
                                            sale_type = jsonobject.getString("sale_type");
                                            String id = jsonobject.getString("id");
                                            String sq_feet = jsonobject.getString("sq_feet");
                                            String beds = jsonobject.getString("beds");
                                            String baths = jsonobject.getString("baths");
                                            String address = jsonobject.getString("address");
                                            String acre_area = jsonobject.getString("acre_area");
                                            String status = jsonobject.getString("status");


                                            JSONArray jsonArray = jsonobject.getJSONArray("property_images");
                                            property_image_list = new ArrayList<>();
                                            for (int j = 0; j < jsonArray.length(); j++) {

                                                PropertyImage propertyImage = new PropertyImage();
                                                property_image = jsonArray.getJSONObject(j).getString("property_image");
                                                propertyImage.setPropertyImage(property_image);
                                                property_image_list.add(propertyImage);
                                            }

                                            dataHolder.setProperty_price(property_price);
                                            dataHolder.setProperty_sale_type(status);
                                            dataHolder.setPropertyImageList(property_image_list);
                                            propertyDetails.setId(id);
                                            propertyDetails.setAcreArea(acre_area);
                                            propertyDetails.setAddress(address);
                                            propertyDetails.setBaths(baths);
                                            propertyDetails.setBeds(beds);
                                            propertyDetails.setPropertyPrice(property_price);
                                            propertyDetails.setSaleType(sale_type);
                                            propertyDetails.setSqFeet(sq_feet);
                                            dataHolder.setPropertyDetails(propertyDetails);

                                            JSONObject jsonObject1 = jsonobject.getJSONObject("agent_details");
                                            String agent_id = jsonObject1.getString("id");
                                            String fname = jsonObject1.getString("first_name");
                                            String lname = jsonObject1.getString("last_name");
                                            String image = jsonObject1.getString("image");
                                            String email = jsonObject1.getString("email");

                                            agentDetails.setId(id);
                                            agentDetails.setFirstName(fname);
                                            agentDetails.setLastName(lname);
                                            agentDetails.setEmail(email);
                                            agentDetails.setImage(image);
                                            dataHolder.setAgentDetails(agentDetails);

                                            property_list.add(dataHolder);

                                            Log.e("stop ", " " + stop);

                                            if (getActivity() != null) {
                                                HomeActivity.ptype = "";
                                                HomeActivity.to_beds = "";
                                                HomeActivity.from_beds = "";
                                                HomeActivity.to_baths = "";
                                                HomeActivity.from_baths = "";
                                                HomeActivity.to_price = "";
                                                HomeActivity.from_price = "";
                                                HomeActivity.fstatus = "";
                                                HomeActivity.fdate = "";
                                                HomeActivity.fsort = "";
                                                HomeActivity.sale_type = "";
                                                HomeActivity.fkey = "";
                                                HomeActivity.p_amenities = "";

                                                Gson gson = new Gson();
                                                HomePropertyAdapter adaper = new HomePropertyAdapter(getActivity(), property_list);
                                                zoom_card_view.setVisibility(View.GONE);
                                                home_property_recycler_view.setVisibility(View.VISIBLE);
                                                home_property_recycler_view.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                                                home_property_recycler_view.setAdapter(adaper);
                                                adaper.notifyDataSetChanged();

                                            } else {

                                                HomeActivity.ptype = "";
                                                HomeActivity.to_beds = "";
                                                HomeActivity.from_beds = "";
                                                HomeActivity.to_baths = "";
                                                HomeActivity.from_baths = "";
                                                HomeActivity.to_price = "";
                                                HomeActivity.from_price = "";
                                                HomeActivity.fstatus = "";
                                                HomeActivity.fdate = "";
                                                HomeActivity.fsort = "";
                                                HomeActivity.sale_type = "";
                                                HomeActivity.fkey = "";
                                                HomeActivity.p_amenities = "";
                                                home_property_recycler_view.setVisibility(View.GONE);
                                                zoom_card_view.setVisibility(View.VISIBLE);
                                            }

                                            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
                                        }
                                    }
                                }

                            }

                        } else {
                            String result = object.getString("result");

                            if (getActivity() != null) {
                                HomeActivity.ptype = "";
                                HomeActivity.to_beds = "";
                                HomeActivity.from_beds = "";
                                HomeActivity.to_baths = "";
                                HomeActivity.from_baths = "";
                                HomeActivity.to_price = "";
                                HomeActivity.from_price = "";
                                HomeActivity.fstatus = "";
                                HomeActivity.fdate = "";
                                HomeActivity.fsort = "";
                                HomeActivity.sale_type = "";
                                HomeActivity.fkey = "";

                                Toast.makeText(getActivity(), "" + result, Toast.LENGTH_SHORT).show();
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
                if (mMap == null) {

                } else {
                    mMap.clear();
                }
                Toast.makeText(getActivity(), "Server Problem Please try Next time...!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    //-------------------------------- home property adapter ---------------------------

    public class HomePropertyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private Context context;
        private LayoutInflater inflater;
        public List<DataHolder> dataHolderList;
        RecyclerView recyclerView;
        View view;

        public HomePropertyAdapter(Context context, List<DataHolder> dataHolderList) {
            this.context = context;
            inflater = LayoutInflater.from(context);
            this.dataHolderList = dataHolderList;
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            view = inflater.inflate(R.layout.home_prop_pojo_lay, parent, false);
            MyHolder holder = new MyHolder(view);
            return holder;
        }

        @SuppressLint("ResourceAsColor")
        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

            System.out.println("Dattasize***" + dataHolderList.size());
            final MyHolder myHolder = (MyHolder) holder;
            if (dataHolderList.size() > 0) {

                myHolder.home_property_price_text.setText(dataHolderList.get(position).getProperty_price() + " XAF");
                myHolder.home_property_sale_type_text.setText(dataHolderList.get(position).getProperty_sale_type());

                if (dataHolderList.get(position).getPropertyImageList() != null && !dataHolderList.get(position).getPropertyImageList().isEmpty()) {
                    SectionListDataAdapter itemListDataAdapter = new SectionListDataAdapter(context, dataHolderList.get(position).getPropertyImageList(), dataHolderList.get(position).getPropertyDetails());
                    myHolder.home_secand_recycler_view.setHasFixedSize(true);
                    myHolder.home_secand_recycler_view.setItemViewCacheSize(20);
                    myHolder.home_secand_recycler_view.setDrawingCacheEnabled(true);
                    myHolder.home_secand_recycler_view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                    myHolder.home_secand_recycler_view.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                    myHolder.home_secand_recycler_view.setAdapter(itemListDataAdapter);
                    itemListDataAdapter.notifyDataSetChanged();

                    // Log.e("testing list size ",""+dataHolderList.get(position).getPropertyDetails().getPropertyImages().size());

//                    TestAdapter itemListDataAdapter = new TestAdapter(context, dataHolderList.get(position).getPropertyImageList(), dataHolderList.get(position).getPropertyDetails());
//                    myHolder.home_secand_recycler_view.setHasFixedSize(true);
//                    myHolder.home_secand_recycler_view.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
//                    myHolder.home_secand_recycler_view.setAdapter(itemListDataAdapter);
//                    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.RIGHT,this);
//                    new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
//                    itemListDataAdapter.notifyDataSetChanged();

                }

                myHolder.home_share_pojo_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        shareTextUrl();
                    }
                });

                myHolder.home_add_to_fav_image.setOnClickListener(new View.OnClickListener() {
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

                            property_id = dataHolderList.get(position).getPropertyDetails().getId();
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
                    }
                });

                myHolder.home_send_in_message_image.setOnClickListener(new View.OnClickListener() {
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

                            mFirebaseDatabase.orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.exists()) {

                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                            String key = ds.getKey();
                                            User user = ds.child("credentials").getValue(User.class);
                                            String email = user.email;



                                                if (email != null && dataHolderList.get(position).getAgentDetails().getEmail() != null && dataHolderList.get(position).getAgentDetails().getEmail().equalsIgnoreCase(email)) {
                                                    String agent_id = key;
                                                    if (key != null && mAuth.getCurrentUser().getUid() != null && key.equalsIgnoreCase(mAuth.getCurrentUser().getUid())) {

                                                        Toast.makeText(context, getString(R.string.youcantnotchatown), Toast.LENGTH_SHORT).show();

                                                    } else {
                                                    Intent intent = new Intent(context, ChatActivity.class);
                                                    intent.putExtra("receiver_id", agent_id);
                                                    intent.putExtra("username", dataHolderList.get(position).getAgentDetails().getFirstName() + " " + dataHolderList.get(position).getAgentDetails().getLastName());
                                                    intent.putExtra("userprofile", dataHolderList.get(position).getAgentDetails().getImage());
                                                    intent.putExtra("property_id", dataHolderList.get(position).getPropertyDetails().getId());
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

            } else zoom_card_view.setVisibility(View.VISIBLE);
        }

        @Override
        public int getItemCount() {
            return dataHolderList.size();
        }


        class MyHolder extends RecyclerView.ViewHolder {

            TextView home_property_price_text, home_property_sale_type_text;
            RecyclerView home_secand_recycler_view;
            CardView card_view_in_home;
            ImageView home_share_pojo_image, home_add_to_fav_image, home_send_in_message_image;

            // create constructor to get widget reference
            public MyHolder(View itemView) {
                super(itemView);

                home_property_price_text = itemView.findViewById(R.id.home_property_price_text);
                home_property_sale_type_text = itemView.findViewById(R.id.home_property_sale_type_text);
                home_secand_recycler_view = itemView.findViewById(R.id.home_secand_recycler_view);
                card_view_in_home = itemView.findViewById(R.id.card_view_in_home);
                home_share_pojo_image = itemView.findViewById(R.id.home_share_pojo_image);
                home_add_to_fav_image = itemView.findViewById(R.id.home_add_to_fav_image);
                home_send_in_message_image = itemView.findViewById(R.id.home_send_in_message_image);
            }

        }
    }

    public class SectionListDataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private Context context;
        private LayoutInflater inflater;
        public List<PropertyImage> dataHolderList;
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
                    Picasso.with(context).load(dataHolderList.get(position).getPropertyImage()).placeholder(R.drawable.dummy_img).into(myHolder.home_property_imageview);

                }

                myHolder.home_property_imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), PropertyDetailActivity.class);
                        intent.putExtra("prop_id", propertyDetails.getId());
                        startActivity(intent);
                    }
                });

            }
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
        Intent share = new Intent(Intent.ACTION_SEND);
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
                            Toast.makeText(getActivity(), "" + message, Toast.LENGTH_SHORT).show();
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
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), "Server Problem Please try Next time...!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
