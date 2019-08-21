package com.t.pausi.Bean;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.t.pausi.Activity.PropertyDetailActivity;
import com.t.pausi.Pojo.PropertyDetails;
import com.t.pausi.Pojo.PropertyImage;
import com.t.pausi.R;

import java.util.List;

public class TestAdapter extends RecyclerView.Adapter<TestAdapter.MyHolder> {
    private Context context;
    private LayoutInflater inflater;
    public List<PropertyImage> dataHolderList;
    public List<DataHolder> ssdataHolderList;
    RecyclerView recyclerView;
    PropertyDetails propertyDetails;
    View view;


    public TestAdapter(Context context, List<PropertyImage> dataHolderList, PropertyDetails propertyDetails) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.dataHolderList = dataHolderList;
        this.propertyDetails = propertyDetails;
        notifyDataSetChanged();
    }


    @Override
    public TestAdapter.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = inflater.inflate(R.layout.home_prop_pojo_lay2, parent, false);
        MyHolder holder = new MyHolder(view);
        return holder;
    }


    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(final TestAdapter.MyHolder holder, final int position) {
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
                        Intent intent = new Intent(context, PropertyDetailActivity.class);
                        intent.putExtra("prop_id", propertyDetails.getId());
                        context.startActivity(intent);
                    }
                });

            } else
                Picasso.with(context).load(dataHolderList.get(position).getPropertyImage()).into(myHolder.home_property_imageview);


        }

        myHolder.home_property_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PropertyDetailActivity.class);
                intent.putExtra("prop_id", dataHolderList.get(position).getPropertyId());
                context.startActivity(intent);
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
        RelativeLayout viewForeground;

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

