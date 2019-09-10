package com.alice.mhp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alice.mhp.alicecleaningmanagement.R;


public class CustomerRequestRecyclerViewHolder extends RecyclerView.ViewHolder {

    public TextView text_clean_date;
    public TextView text_clean_time;
    public TextView text_clean_type;

    public CustomerRequestRecyclerViewHolder(View itemView) {
        super(itemView);
        text_clean_date = itemView.findViewById(R.id.text_clean_date);
        text_clean_time = itemView.findViewById(R.id.text_clean_time);
        text_clean_type = itemView.findViewById(R.id.text_clean_type);
    }
}
