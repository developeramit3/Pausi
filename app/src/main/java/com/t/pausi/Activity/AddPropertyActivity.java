package com.t.pausi.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fxn.pix.Pix;
import com.fxn.utility.PermUtil;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.vision.text.Text;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.t.pausi.Bean.AlertConnection;
import com.t.pausi.Bean.ConnectionDetector;
import com.t.pausi.Bean.MyLanguageSession;
import com.t.pausi.Bean.MySharedPref;
import com.t.pausi.Fragment.NewsFragment;
import com.t.pausi.Interface.Config;
import com.t.pausi.Interface.UserInterface;
import com.t.pausi.Pojo.NewsList;
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
import java.util.ArrayList;
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

public class AddPropertyActivity extends AppCompatActivity {
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
    List<String> final_image_list;
    List<String> property_amenities_list;
    Button add_property_btn;
    CheckBox kitchen_checkbox, dining_checkbox, parking_checkbox, external_garage_checkbox, study_checkbox;
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    ProgressDialog progressDialog;
    MySharedPref sp;
    String ldata, logid;
    List<MultipartBody.Part> parts_image = new ArrayList<>();
    MyLanguageSession myLanguageSession;
    private String language = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLanguageSession = new MyLanguageSession(this);
        language = myLanguageSession.getLanguage();
        myLanguageSession.setLangRecreate(myLanguageSession.getLanguage());
        setContentView(R.layout.activity_add_property);


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

        add_property_back = findViewById(R.id.add_property_back);
        apart_check_lay = findViewById(R.id.apart_check_lay);
        add_prop_type_spn = findViewById(R.id.add_prop_type_spn);
        add_prp_for_spn = findViewById(R.id.add_prp_for_spn);
        add_property_title_edittext = findViewById(R.id.add_property_title_edittext);
        add_prop_address_editext = findViewById(R.id.add_prop_address_editext);
        add_prop_total_beds_text = findViewById(R.id.add_prop_total_beds_text);
        add_prop_address2_text = findViewById(R.id.add_prop_address2_text);
        add_prop_total_bath_edittext = findViewById(R.id.add_prop_total_bath_edittext);
        add_prop_size_edittext = findViewById(R.id.add_prop_size_edittext);
        add_prop_built_year_edittext = findViewById(R.id.add_prop_built_year_edittext);
        add_prop_total_cost_editext = findViewById(R.id.add_prop_total_cost_editext);
        add_prop_decerp_edittext = findViewById(R.id.add_prop_decerp_edittext);
        add_property_recylcer_view = findViewById(R.id.add_property_recylcer_view);
        add_property_imageview = findViewById(R.id.add_property_imageview);
        add_property_btn = findViewById(R.id.add_property_btn);
        add_prp_available_sale_spn = findViewById(R.id.add_prp_available_sale_spn);
        add_prp_available_rent_spn = findViewById(R.id.add_prp_available_rent_spn);
        kitchen_checkbox = findViewById(R.id.kitchen_checkbox);
        dining_checkbox = findViewById(R.id.dining_checkbox);
        parking_checkbox = findViewById(R.id.parking_checkbox);
        external_garage_checkbox = findViewById(R.id.external_garage_checkbox);
        study_checkbox = findViewById(R.id.study_checkbox);

        //----------------------- init list ------------------------------------------------------

        image_list = new ArrayList<>();
        property_amenities_list = new ArrayList<>();
        final_image_list = new ArrayList<>();

        //---------------------- spn code -----------------------------------------------------------

        add_prop_type_spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                prop_type = add_prop_type_spn.getSelectedItem().toString();
                if (prop_type.equalsIgnoreCase(getString(R.string.apartment))) {
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
                if (prop_for.equalsIgnoreCase(getString(R.string.rent))) {
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


        add_prop_decerp_edittext.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on Enter key press
//                    txtSearch.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        add_property_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Pix.start(AddPropertyActivity.this,                    //Activity or Fragment Instance
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
                                    .build(AddPropertyActivity.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }

            }
        });

        add_property_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        add_property_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (add_property_title_edittext.getText().toString().equalsIgnoreCase("")) {
                    add_property_title_edittext.setError(getString(R.string.can_not_be_empty));
                    add_property_title_edittext.requestFocus();
                } else if (add_prop_address_editext.getText().toString().equalsIgnoreCase("")) {
                    add_prop_address_editext.setError(getString(R.string.can_not_be_empty));
                    add_prop_address_editext.requestFocus();
                } else if (add_prop_address2_text.getText().toString().equalsIgnoreCase("")) {
                    add_prop_address2_text.setError(getString(R.string.can_not_be_empty));
                    add_prop_address2_text.requestFocus();
                } else if (add_prop_total_beds_text.getText().toString().equalsIgnoreCase("")) {
                    add_prop_total_beds_text.setError(getString(R.string.can_not_be_empty));
                    add_prop_total_beds_text.requestFocus();
                } else if (add_prop_total_bath_edittext.getText().toString().equalsIgnoreCase("")) {
                    add_prop_total_bath_edittext.setError(getString(R.string.can_not_be_empty));
                    add_prop_total_bath_edittext.requestFocus();
                } else if (add_prop_size_edittext.getText().toString().equalsIgnoreCase("")) {
                    add_prop_size_edittext.setError(getString(R.string.can_not_be_empty));
                    add_prop_size_edittext.requestFocus();
                } else if (add_prop_built_year_edittext.getText().toString().equalsIgnoreCase("")) {
                    add_prop_built_year_edittext.setError(getString(R.string.can_not_be_empty));
                    add_prop_built_year_edittext.requestFocus();
                } else if (add_prop_total_cost_editext.getText().toString().equalsIgnoreCase("")) {
                    add_prop_total_cost_editext.setError(getString(R.string.can_not_be_empty));
                    add_prop_total_cost_editext.requestFocus();
                } else if (add_prop_decerp_edittext.getText().toString().equalsIgnoreCase("")) {
                    add_prop_decerp_edittext.setError(getString(R.string.can_not_be_empty));
                    add_prop_decerp_edittext.requestFocus();
                } else if (image_list == null || image_list.size() == 0) {
                    Toast.makeText(AddPropertyActivity.this, R.string.please_select_img, Toast.LENGTH_SHORT).show();
                } else {

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
                    image_count = String.valueOf(image_list.size());

                    Log.e("images ", "" + final_image_list.size());
                    Log.e("images2 ", "" + image_list.size());
                    Log.e("property_amenities ", "" + property_amenities);

                    for (int i = 0; i < image_list.size(); i++) {
                        File file = new File(image_list.get(i));
                        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                        MultipartBody.Part body = MultipartBody.Part.createFormData("property_image[]", file.getName(), requestFile);
                        parts_image.add(body);
                    }

                    if (isInternetPresent) {
                        AddProperty_call();
                    } else {
                        AlertConnection.showAlertDialog(getApplicationContext(), getString(R.string.no_internal_connection),
                                getString(R.string.donothaveinternet), false);
                    }

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
                    Pix.start(AddPropertyActivity.this, IMAGE_REQUEST_CODE, 12);
                } else {
                    Toast.makeText(AddPropertyActivity.this, R.string.piximagepicker, Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_REQUEST_CODE) {
            List<String> image_list1 = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            for (int i = 0; i < image_list1.size(); i++) {
                String image = image_list1.get(i);
                image_list.add(image);
            }
            add_property_recylcer_view.setVisibility(View.VISIBLE);
            ImageAdapter adaper = new ImageAdapter(AddPropertyActivity.this, image_list);
            add_property_recylcer_view.setLayoutManager(new LinearLayoutManager(AddPropertyActivity.this, LinearLayoutManager.HORIZONTAL, false));
            add_property_recylcer_view.setAdapter(adaper);
            adaper.notifyDataSetChanged();

        }

        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(AddPropertyActivity.this, data);
                Log.e("Place: ", "" + place.getName());
                add_prop_address2_text.setText(place.getAddress());

                lat = place.getLatLng().latitude;
                lon = place.getLatLng().longitude;

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(AddPropertyActivity.this, data);
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
        public List<String> newsLists;
        RecyclerView recyclerView;
        View view;

        public ImageAdapter(Context context, List<String> newsLists) {
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

            Log.e("imaghes ", newsLists.get(position));

            myHolder.add_prop_pojo_imageview.setImageURI(Uri.parse(newsLists.get(position)));
//            for (int i = 0; i < newsLists.size(); i++) {
//                final_image_list.add(newsLists.get(i));
//            }

            myHolder.add_prop_cancel_imageview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // image_list.remove(newsLists.get(position));
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

    //------------------------------------ add property call -----------------------------------

    private void AddProperty_call() {
        progressDialog = new ProgressDialog(AddPropertyActivity.this);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.show();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.Base_Url).client(client)
                .addConverterFactory(GsonConverterFactory.create(new Gson())).build();
        UserInterface signupInterface = retrofit.create(UserInterface.class);
        Call<ResponseBody> resultCall = signupInterface.add_new_property(logid, tittle, total_cost, total_cost, prop_type, available, size, beds, bath, built_in, size, "", "", "", address, String.valueOf(lat), String.valueOf(lon), "", prop_for, description, address2, image_count, property_amenities, language, parts_image);
        resultCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    try {

                        String responedata = response.body().string();
                        Log.e("add property response ", " " + responedata);
                        JSONObject object = new JSONObject(responedata);
                        String error = object.getString("status");

                        if (error.equals("1")) {

                            Toast.makeText(AddPropertyActivity.this, R.string.property_added, Toast.LENGTH_SHORT).show();
                            finish();
                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                            startActivity(intent);


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
                Toast.makeText(getApplicationContext(), R.string.server_problem, Toast.LENGTH_SHORT).show();
            }
        });
    }
}



