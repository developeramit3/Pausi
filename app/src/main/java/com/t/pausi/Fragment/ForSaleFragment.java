package com.t.pausi.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.t.pausi.Activity.FilterActivity;
import com.t.pausi.Activity.HomeActivity;
import com.t.pausi.R;

import java.util.ArrayList;
import java.util.List;

public class ForSaleFragment extends Fragment {

    Spinner all_property_type_spn, beds_to_spn, beds_from_spn, baths_to_spn, baths_from_spn,
            price_to_spn, price_from_spn, status_spn, date_spn, sort_spn;
    Button sale_apply_filter_btn, sale_type_reset_filter_btn;
    String p_type, beds_to, beds_from, baths_to, baths_from, price_to, price_from, status, date, sort;
    LinearLayout apart_check_lay;
    CheckBox kitchen_checkbox, dining_checkbox, parking_checkbox, external_garage_checkbox, study_checkbox;
    List<String> property_amenities_list;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.for_sale_fragment, container, false);

        all_property_type_spn = view.findViewById(R.id.all_property_type_spn);
        beds_to_spn = view.findViewById(R.id.beds_to_spn);
        beds_from_spn = view.findViewById(R.id.beds_from_spn);
        baths_to_spn = view.findViewById(R.id.baths_to_spn);
        baths_from_spn = view.findViewById(R.id.baths_from_spn);
        price_to_spn = view.findViewById(R.id.price_to_spn);
        price_from_spn = view.findViewById(R.id.price_from_spn);
        status_spn = view.findViewById(R.id.status_spn);
        date_spn = view.findViewById(R.id.date_spn);
        sort_spn = view.findViewById(R.id.sort_spn);
        sale_apply_filter_btn = view.findViewById(R.id.sale_apply_filter_btn);
        kitchen_checkbox = view.findViewById(R.id.kitchen_checkbox);
        dining_checkbox = view.findViewById(R.id.dining_checkbox);
        parking_checkbox = view.findViewById(R.id.parking_checkbox);
        external_garage_checkbox = view.findViewById(R.id.external_garage_checkbox);
        study_checkbox = view.findViewById(R.id.study_checkbox);
        apart_check_lay = view.findViewById(R.id.apart_check_lay);
        sale_type_reset_filter_btn = view.findViewById(R.id.sale_type_reset_filter_btn);

        property_amenities_list = new ArrayList<>();


        //------------------------------ spinner code -------------------------------------


        all_property_type_spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                p_type = all_property_type_spn.getSelectedItem().toString();
                if (p_type.equalsIgnoreCase("Apartment")) {
                    apart_check_lay.setVisibility(View.VISIBLE);
                } else apart_check_lay.setVisibility(View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        beds_to_spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                beds_to = beds_to_spn.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        beds_from_spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                beds_from = beds_from_spn.getSelectedItem().toString();
                if (beds_from.equalsIgnoreCase("6+")) {
                    beds_from = "999";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        baths_to_spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                baths_to = baths_to_spn.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        baths_from_spn.setSelection(1);

        baths_from_spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                baths_from = baths_from_spn.getSelectedItem().toString();
                if (baths_from.equalsIgnoreCase("5+")) {
                    baths_from = "999";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        price_to_spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                price_to = price_to_spn.getSelectedItem().toString();

                if (price_to.equalsIgnoreCase("0M")) {
                    price_to = "0";
                }
                if (price_to.equalsIgnoreCase("5M")) {
                    price_to = "5000000";
                }
                if (price_to.equalsIgnoreCase("10M")) {
                    price_to = "10000000";
                }
                if (price_to.equalsIgnoreCase("25M")) {
                    price_to = "25000000";
                }
                if (price_to.equalsIgnoreCase("100M")) {
                    price_to = "100000000";
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        price_from_spn.setSelection(5);

        price_from_spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                price_from = price_from_spn.getSelectedItem().toString();
                if (price_from.equalsIgnoreCase("5M")) {
                    price_from = "5000000";
                }
                if (price_from.equalsIgnoreCase("10M")) {
                    price_from = "10000000";
                }
                if (price_from.equalsIgnoreCase("25M")) {
                    price_from = "25000000";
                }
                if (price_from.equalsIgnoreCase("100M")) {
                    price_from = "100000000";
                }
                if (price_from.equalsIgnoreCase("120M")) {
                    price_from = "120000000";
                }
                if (price_from.equalsIgnoreCase("120M+")) {
                    price_from = "999999999";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        status_spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                status = status_spn.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        date_spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                date = date_spn.getSelectedItem().toString();
                if (date.equalsIgnoreCase("In the past week")) {
                    date = "Week";
                }
                if (date.equalsIgnoreCase("In the past month")) {
                    date = "CurrentMonth";
                }
                if (date.equalsIgnoreCase("In the past 3 months")) {
                    date = "CurrentThreeMonth";
                }
                if (date.equalsIgnoreCase("In the past 6 months")) {
                    date = "CurrentSixMonth";
                }
                if (date.equalsIgnoreCase("Over 1 month")) {
                    date = "OverMonth";
                }
                if (date.equalsIgnoreCase("Over 3 months")) {
                    date = "OverThreeMonth";
                }
                if (date.equalsIgnoreCase("Over 6 months")) {
                    date = "OverSixMonth";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        sort_spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sort = sort_spn.getSelectedItem().toString();
                if (sort.equalsIgnoreCase("Price (Low to High")) {
                    sort = "LowToHigh";
                }
                if (sort.equalsIgnoreCase("Price (High to Low)")) {
                    sort = "HighToLow";
                }
                if (sort.equalsIgnoreCase("A to Z")) {
                    sort = "ASC";
                }
                if (sort.equalsIgnoreCase("Z to A")) {
                    sort = "DESC";
                }
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
                    property_amenities_list.add("Kitchen");
                } else property_amenities_list.remove("Kitchen");
            }
        });

        dining_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    property_amenities_list.add("Dining");
                } else property_amenities_list.remove("Dining");
            }
        });

        parking_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    property_amenities_list.add("Parking");
                } else property_amenities_list.remove("Parking");
            }
        });

        study_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    property_amenities_list.add("Study");
                } else property_amenities_list.remove("Study");
            }
        });

        external_garage_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    property_amenities_list.add("External Garage");
                } else property_amenities_list.remove("External Garage");
            }
        });

        sale_apply_filter_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (getActivity() != null) {

                    String idList2 = property_amenities_list.toString();
                    HomeActivity.p_amenities = idList2.substring(1, idList2.length() - 1).replace(", ", ",");
                    HomeActivity.ptype = p_type;
                    HomeActivity.to_beds = beds_to;
                    HomeActivity.from_beds = beds_from;
                    HomeActivity.to_baths = baths_to;
                    HomeActivity.from_baths = baths_from;
                    HomeActivity.to_price = price_to;
                    HomeActivity.from_price = price_from;
                    HomeActivity.fstatus = status;
                    HomeActivity.fdate = date;
                    HomeActivity.fsort = sort;
                    HomeActivity.sale_type = "Sale";
                    HomeActivity.fkey = "Filter";

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

                    getActivity().startActivity(new Intent(getActivity(), HomeActivity.class));
                }


            }
        });

        sale_type_reset_filter_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FilterActivity.addFragment(new ForSaleFragment(), false);
            }
        });

        return view;
    }


}
