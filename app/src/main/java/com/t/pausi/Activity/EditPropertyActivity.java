package com.t.pausi.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.fxn.pix.Pix;
import com.fxn.utility.PermUtil;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.t.pausi.Bean.AlertConnection;
import com.t.pausi.Bean.ConnectionDetector;
import com.t.pausi.Bean.MyLanguageSession;
import com.t.pausi.Bean.MySharedPref;
import com.t.pausi.Interface.Config;
import com.t.pausi.Interface.UserInterface;
import com.t.pausi.Pojo.PropertyDetailResponse;
import com.t.pausi.Pojo.PropertyImage;
import com.t.pausi.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
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

public class EditPropertyActivity extends AppCompatActivity {
    ImageView edit_property_back;
    ImageView add_property_back, add_property_imageview;
    LinearLayout apart_check_lay;
    Spinner add_prop_type_spn, add_prp_for_spn, add_prp_available_sale_spn, add_prp_available_rent_spn;
    String prop_type, prop_for = "", tittle = "", available = "", address = "", address2 = "", beds = "", bath = "", size = "", built_in = "", total_cost = "", description = "",
            image_count = "", property_amenities = "";
    EditText add_property_title_edittext, add_prop_address_editext, add_prop_total_beds_text,
            add_prop_total_bath_edittext, add_prop_size_edittext, add_prop_built_year_edittext,
            add_prop_total_cost_editext, add_prop_decerp_edittext;
    TextView add_prop_address2_text;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    int IMAGE_REQUEST_CODE = 100;
    double lat, lon;
    RecyclerView add_property_recylcer_view;
    List<String> image_list;
    List<PropertyImage> final_image_list;
    List<String> property_amenities_list;
    List<PropertyImage> property_image_local_list;
    Button add_property_btn;
    CheckBox kitchen_checkbox, dining_checkbox, parking_checkbox, external_garage_checkbox, study_checkbox;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    ProgressDialog progressDialog;
    MySharedPref sp;
    String ldata, logid, property_image_id = "";
    List<MultipartBody.Part> parts_image;
    String property_id = "";
    List<String> pfor;
    List<String> ptype;
    List<String> avi_list;
    List<String> avi_list2;
    boolean count = true;
    MyLanguageSession myLanguageSession;
    private String language = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLanguageSession = new MyLanguageSession(this);
        language = myLanguageSession.getLanguage();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());

        setContentView(R.layout.activity_edit_property);

        //--------------------------- connection detector -----------------------------------

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

        //------------------------------- find view ---------------------------------------------

        apart_check_lay = findViewById(R.id.apart_check_lay2);
        add_prop_type_spn = findViewById(R.id.add_prop_type_spn2);
        add_prp_for_spn = findViewById(R.id.add_prp_for_spn2);
        add_property_title_edittext = findViewById(R.id.add_property_title_edittext2);
        add_prop_address_editext = findViewById(R.id.add_prop_address_editext2);
        add_prop_total_beds_text = findViewById(R.id.add_prop_total_beds_text2);
        add_prop_address2_text = findViewById(R.id.add_prop_address2_text2);
        add_prop_total_bath_edittext = findViewById(R.id.add_prop_total_bath_edittext2);
        add_prop_size_edittext = findViewById(R.id.add_prop_size_edittext2);
        add_prop_built_year_edittext = findViewById(R.id.add_prop_built_year_edittext2);
        add_prop_total_cost_editext = findViewById(R.id.add_prop_total_cost_editext2);
        add_prop_decerp_edittext = findViewById(R.id.add_prop_decerp_edittext2);
        add_property_recylcer_view = findViewById(R.id.add_property_recylcer_view2);
        add_property_imageview = findViewById(R.id.add_property_imageview2);
        add_property_btn = findViewById(R.id.add_property_btn2);
        add_prp_available_sale_spn = findViewById(R.id.add_prp_available_sale_spn2);
        add_prp_available_rent_spn = findViewById(R.id.add_prp_available_rent_spn2);
        kitchen_checkbox = findViewById(R.id.kitchen_checkbox2);
        dining_checkbox = findViewById(R.id.dining_checkbox2);
        parking_checkbox = findViewById(R.id.parking_checkbox2);
        external_garage_checkbox = findViewById(R.id.external_garage_checkbox2);
        study_checkbox = findViewById(R.id.study_checkbox2);
        edit_property_back = findViewById(R.id.edit_property_back);

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
            } else {
                Toast.makeText(this, R.string.property_id_not_found, Toast.LENGTH_SHORT).show();
            }

        } else {
            AlertConnection.showAlertDialog(getApplicationContext(), getString(R.string.no_internal_connection),
                    getString(R.string.donothaveinternet), false);
        }

        //----------------------- init list ------------------------------------------------------

        image_list = new ArrayList<>();
        final_image_list = new ArrayList<>();
        property_image_local_list = new ArrayList<>();
        property_amenities_list = new ArrayList<>();


        pfor = new ArrayList<>();
        pfor.add("Sale");
        pfor.add("Rent");

        ptype = new ArrayList<>();
        ptype.add("Family Homes");
        ptype.add("Apartment");
        ptype.add("Plots and Land");
        ptype.add("Guest House");
        ptype.add("Commercial");
        ptype.add("Others");

        avi_list = new ArrayList<>();
        avi_list.add("FOR SALE");
        avi_list.add("OPEN HOUSE");
        avi_list.add("CONTRACT");
        avi_list.add("SOLD");

        avi_list2 = new ArrayList<>();
        avi_list2.add("FOR RENT");
        avi_list2.add("OPEN HOUSE");
        avi_list2.add("CONTRACT");
        avi_list2.add("NOT AVAILABLE");

        //---------------------- spn code -----------------------------------------------------------

        add_prop_type_spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                prop_type = add_prop_type_spn.getSelectedItem().toString();
                if (prop_type.equalsIgnoreCase("Apartment")) {
                    apart_check_lay.setVisibility(View.VISIBLE);
                } else apart_check_lay.setVisibility(View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        add_prp_for_spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                prop_for = add_prp_for_spn.getSelectedItem().toString();
                if (prop_for.equalsIgnoreCase("Rent")) {
                    add_prp_available_rent_spn.setVisibility(View.VISIBLE);
                    add_prp_available_sale_spn.setVisibility(View.GONE);
                } else {
                    add_prp_available_rent_spn.setVisibility(View.GONE);
                    add_prp_available_sale_spn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        add_prp_available_sale_spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                available = add_prp_available_sale_spn.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        add_prp_available_rent_spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                available = add_prp_available_rent_spn.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //---------------------------------------- checkbox code -------------------------------

        kitchen_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    property_amenities_list.add(getString(R.string.kitchen));
                } else property_amenities_list.remove(getString(R.string.kitchen));
            }
        });

        dining_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    property_amenities_list.add(getString(R.string.dining));
                } else property_amenities_list.remove(getString(R.string.dining));
            }
        });

        parking_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    property_amenities_list.add(getString(R.string.parking));
                } else property_amenities_list.remove(getString(R.string.parking));
            }
        });

        study_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    property_amenities_list.add(getString(R.string.study));
                } else property_amenities_list.remove(getString(R.string.study));
            }
        });

        external_garage_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    property_amenities_list.add(getString(R.string.external_garage));
                } else property_amenities_list.remove(getString(R.string.external_garage));
            }
        });

        //--------------------------------- on click -------------------------------------------------

        add_property_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Pix.start(EditPropertyActivity.this,                    //Activity or Fragment Instance
                        IMAGE_REQUEST_CODE,                //Request code for activity results
                        12);
            }
        });


        add_prop_address2_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .build(EditPropertyActivity.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }

            }
        });


        edit_property_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        add_property_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (property_image_local_list != null && property_image_local_list.isEmpty()) {

                    Toast.makeText(EditPropertyActivity.this, "Please select Image", Toast.LENGTH_SHORT).show();
                } else {
                    parts_image = new ArrayList<>();
                    for (int i = 0; i < property_image_local_list.size(); i++) {

                        if (property_image_local_list.get(i).getId().equalsIgnoreCase(getString(R.string.local))) {

                            File file = new File(property_image_local_list.get(i).getPropertyImage());
                            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                            MultipartBody.Part body = MultipartBody.Part.createFormData("property_image[]", file.getName(), requestFile);
                            parts_image.add(body);
                        }

                        if (parts_image != null && parts_image.size() < 1) {
                            parts_image = new ArrayList<>();
                            MultipartBody.Part body = MultipartBody.Part.createFormData("property_image[]", "");
                            parts_image.add(body);
                            image_count = String.valueOf(0);
                        } else {
                            image_count = String.valueOf(parts_image.size());
                        }

                    }
                }

                Log.e("image count ", image_count);

                String idList2 = property_amenities_list.toString();
                property_amenities = idList2.substring(1, idList2.length() - 1).replace(", ", ",");
                tittle = add_property_title_edittext.getText().toString();
                address = add_prop_address_editext.getText().toString();
                address2 = add_prop_address2_text.getText().toString();
                beds = add_prop_total_beds_text.getText().toString();
                bath = add_prop_total_bath_edittext.getText().toString();
                size = add_prop_size_edittext.getText().toString();
                built_in = add_prop_built_year_edittext.getText().toString();
                total_cost = add_prop_total_cost_editext.getText().toString();
                description = add_prop_decerp_edittext.getText().toString();
                Log.e("property_amenities ", "" + property_amenities);

                if (isInternetPresent)

                {
                    UpdateProperty_call();
                } else

                {
                    AlertConnection.showAlertDialog(getApplicationContext(), getString(R.string.no_internal_connection),
                            getString(R.string.donothaveinternet), false);
                }


            }


        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Pix.start(EditPropertyActivity.this, IMAGE_REQUEST_CODE, 12);
                } else {
                    Toast.makeText(EditPropertyActivity.this, getString(R.string.piximagepicker), Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_REQUEST_CODE) {
            image_list = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            for (int i = 0; i < image_list.size(); i++) {
                PropertyImage propertyImage = new PropertyImage();
                propertyImage.setId(getString(R.string.local));
                propertyImage.setPropertyImage(image_list.get(i));
                property_image_local_list.add(propertyImage);
            }

            add_property_recylcer_view.setVisibility(View.VISIBLE);
            ImageAdapter adaper = new ImageAdapter(EditPropertyActivity.this, property_image_local_list);
            add_property_recylcer_view.setLayoutManager(new LinearLayoutManager(EditPropertyActivity.this, LinearLayoutManager.HORIZONTAL, false));
            add_property_recylcer_view.setAdapter(adaper);
            adaper.notifyDataSetChanged();

        }

        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(EditPropertyActivity.this, data);
                Log.e("Place: ", "" + place.getName());
                add_prop_address2_text.setText(place.getAddress());

                lat = place.getLatLng().latitude;
                lon = place.getLatLng().longitude;

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(EditPropertyActivity.this, data);
                // TODO: Handle the error.
                Log.e("TAG ", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }

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

//-------------------------------- news adapter ---------------------------

    public class ImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private Context context;
        private LayoutInflater inflater;
        public List<PropertyImage> newsLists;
        RecyclerView recyclerView;
        View view;


        public ImageAdapter(Context context, List<PropertyImage> newsLists) {
            this.context = context;
            inflater = LayoutInflater.from(context);
            this.newsLists = newsLists;
            notifyDataSetChanged();

        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            view = inflater.inflate(R.layout.add_prop_image_pojo_layout, parent, false);
            MyHolder holder = new MyHolder(view);
            return holder;
        }

        @SuppressLint("ResourceAsColor")
        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

            System.out.println("Dattasize***" + newsLists.size());
            final MyHolder myHolder = (MyHolder) holder;


            if (newsLists.get(position).getId().equalsIgnoreCase(getString(R.string.local))) {
                myHolder.add_prop_pojo_imageview.setImageURI(Uri.parse(newsLists.get(position).getPropertyImage()));
            } else {
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
                Glide.with(context)
                        .load(newsLists.get(position).getPropertyImage()).apply(requestOptions)
                        .into(myHolder.add_prop_pojo_imageview);
                // Picasso.with(context).load(newsLists.get(position).getPropertyImage()).into(myHolder.add_prop_pojo_imageview);
            }


//            for (int i = 0; i < newsLists.size(); i++) {
//                if (newsLists.get(position).getId().equalsIgnoreCase("local")) {
//                    final_image_list.add(newsLists.get(i).getPropertyImage());
//                }
//            }


            myHolder.add_prop_cancel_imageview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // image_list.remove(newsLists.get(position));
                    if (!newsLists.get(position).getId().equalsIgnoreCase(getString(R.string.local))) {
                        property_image_id = newsLists.get(position).getId();
                        DeleteImage_call();
                    }

                    newsLists.remove(newsLists.get(position));
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return newsLists.size();
        }

        class MyHolder extends RecyclerView.ViewHolder {
            ImageView add_prop_pojo_imageview, add_prop_cancel_imageview;

            // create constructor to get widget reference
            public MyHolder(View itemView) {
                super(itemView);
                add_prop_pojo_imageview = itemView.findViewById(R.id.add_prop_pojo_imageview);
                add_prop_cancel_imageview = itemView.findViewById(R.id.add_prop_cancel_imageview);

            }
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

                            if (EditPropertyActivity.this != null) {
                                Gson gson = new Gson();
                                PropertyDetailResponse propertyDetailResponse = gson.fromJson(responedata, PropertyDetailResponse.class);

                                add_property_title_edittext.setText(propertyDetailResponse.getResult().get(0).getPropertyName());
                                add_prop_address_editext.setText(propertyDetailResponse.getResult().get(0).getAddress());
                                add_prop_address2_text.setText(propertyDetailResponse.getResult().get(0).getAddress2());
                                add_prop_total_beds_text.setText(propertyDetailResponse.getResult().get(0).getBeds());
                                add_prop_total_bath_edittext.setText(propertyDetailResponse.getResult().get(0).getBaths());
                                add_prop_size_edittext.setText(propertyDetailResponse.getResult().get(0).getSqFeet());
                                add_prop_built_year_edittext.setText(propertyDetailResponse.getResult().get(0).getBuildYear());
                                add_prop_decerp_edittext.setText(propertyDetailResponse.getResult().get(0).getPropertyDescription());
                                add_prop_total_cost_editext.setText(propertyDetailResponse.getResult().get(0).getPropertyPrice());
                                prop_for = propertyDetailResponse.getResult().get(0).getSaleType();
                                prop_type = propertyDetailResponse.getResult().get(0).getPropertyType();
                                available = propertyDetailResponse.getResult().get(0).getStatus();
                                lat = Double.parseDouble(propertyDetailResponse.getResult().get(0).getLat());
                                lon = Double.parseDouble(propertyDetailResponse.getResult().get(0).getLon());


                                for (int i = 0; i < pfor.size(); i++) {
                                    String data = pfor.get(i);
                                    if (data.equalsIgnoreCase(prop_for)) {
                                        add_prp_for_spn.setSelection(i);
                                        break;
                                    }
                                }

                                if (prop_for.equalsIgnoreCase(getString(R.string.rent))) {
                                    add_prp_available_rent_spn.setVisibility(View.VISIBLE);
                                    add_prp_available_sale_spn.setVisibility(View.GONE);
                                    for (int i = 0; i < avi_list2.size(); i++) {
                                        String data = avi_list2.get(i);
                                        if (data.equalsIgnoreCase(available)) {
                                            add_prp_available_rent_spn.setSelection(i);
                                            break;
                                        }
                                    }
                                } else {
                                    add_prp_available_rent_spn.setVisibility(View.GONE);
                                    add_prp_available_sale_spn.setVisibility(View.VISIBLE);
                                    for (int i = 0; i < avi_list.size(); i++) {
                                        String data = avi_list.get(i);
                                        if (data.equalsIgnoreCase(available)) {
                                            add_prp_available_sale_spn.setSelection(i);
                                            break;
                                        }
                                    }
                                }

                                for (int i = 0; i < ptype.size(); i++) {
                                    String data = ptype.get(i);
                                    if (data.equalsIgnoreCase(prop_type)) {
                                        add_prop_type_spn.setSelection(i);
                                        break;
                                    }
                                }


                                if (prop_type.equalsIgnoreCase(getString(R.string.apartment)) && propertyDetailResponse.getResult().get(0).getPropertyAmenities().size() > 0) {
                                    apart_check_lay.setVisibility(View.VISIBLE);
                                    for (int i = 0; i < propertyDetailResponse.getResult().get(0).getPropertyAmenities().size(); i++) {
                                        String data = propertyDetailResponse.getResult().get(0).getPropertyAmenities().get(i).getAmenities();
                                        property_amenities_list.add(data);
                                        if (data.equalsIgnoreCase(getString(R.string.kitchen))) {
                                            kitchen_checkbox.setChecked(true);
                                        }
                                        if (data.equalsIgnoreCase(getString(R.string.dining))) {
                                            dining_checkbox.setChecked(true);
                                        }
                                        if (data.equalsIgnoreCase(getString(R.string.parking))) {
                                            parking_checkbox.setChecked(true);
                                        }
                                        if (data.equalsIgnoreCase(getString(R.string.study))) {
                                            study_checkbox.setChecked(true);
                                        }
                                        if (data.equalsIgnoreCase(getString(R.string.external_garage))) {
                                            external_garage_checkbox.setChecked(true);
                                        }

                                    }
                                }

                                add_property_recylcer_view.setVisibility(View.VISIBLE);
                                final_image_list = propertyDetailResponse.getResult().get(0).getPropertyImages();

                                for (int i = 0; i < final_image_list.size(); i++) {
                                    PropertyImage propertyImage = new PropertyImage();
                                    propertyImage.setId(final_image_list.get(i).getId());
                                    propertyImage.setPropertyImage(final_image_list.get(i).getPropertyImage());
                                    property_image_local_list.add(propertyImage);
                                }

                                ImageAdapter adaper = new ImageAdapter(EditPropertyActivity.this, property_image_local_list);
                                add_property_recylcer_view.setLayoutManager(new LinearLayoutManager(EditPropertyActivity.this, LinearLayoutManager.HORIZONTAL, false));
                                add_property_recylcer_view.setAdapter(adaper);
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

    //--------------------------- delete image _call  -----------------------------------

    private void DeleteImage_call() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.Base_Url).client(client)
                .addConverterFactory(GsonConverterFactory.create(new Gson())).build();
        UserInterface signupInterface = retrofit.create(UserInterface.class);
        Call<ResponseBody> resultCall = signupInterface.delete_property_image(property_image_id);
        resultCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {

                        String responedata = response.body().string();
                        Log.e("delete image", " " + responedata);
                        JSONObject object = new JSONObject(responedata);
                        String error = object.getString("result");
                        if (error.equals("successfull")) {
                            if (EditPropertyActivity.this != null) {
                                GetPropertyDetail_call();
                            }

                        } else {
                            String message = object.getString("message");
                            //Toast.makeText(getApplicationContext(), "" + message, Toast.LENGTH_SHORT).show();
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

    //------------------------------------ update property call -----------------------------------

    private void UpdateProperty_call() {
        progressDialog = new ProgressDialog(EditPropertyActivity.this);
        progressDialog.setMessage("Please wait..");
        progressDialog.show();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.Base_Url).client(client)
                .addConverterFactory(GsonConverterFactory.create(new Gson())).build();
        UserInterface signupInterface = retrofit.create(UserInterface.class);
        Call<ResponseBody> resultCall = signupInterface.update_property(logid, property_id, tittle, total_cost, total_cost, prop_type, available, size, beds, bath, built_in, size, "", "", "", address, String.valueOf(lat), String.valueOf(lon), "", prop_for, description, address2, image_count, property_amenities, language, parts_image);
        resultCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    try {

                        String responedata = response.body().string();
                        Log.e("update prop response ", " " + responedata);
                        JSONObject object = new JSONObject(responedata);
                        String error = object.getString("status");

                        if (error.equals("1")) {

                            Toast.makeText(EditPropertyActivity.this, R.string.property_update, Toast.LENGTH_SHORT).show();


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
