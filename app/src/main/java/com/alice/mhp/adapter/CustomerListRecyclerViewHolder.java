package com.alice.mhp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.alice.mhp.alicecleaningmanagement.R;


public class CustomerListRecyclerViewHolder extends RecyclerView.ViewHolder {
    public TextView text_customer_name;
    public TextView text_customer_address;


    public CustomerListRecyclerViewHolder(View itemView) {
        super(itemView);
        text_customer_name = itemView.findViewById(R.id.text_customer_name);
        text_customer_address = itemView.findViewById(R.id.text_customer_address);
    }
}
