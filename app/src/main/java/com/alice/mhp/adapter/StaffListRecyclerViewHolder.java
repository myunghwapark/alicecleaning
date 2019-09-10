package com.alice.mhp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.alice.mhp.alicecleaningmanagement.R;


public class StaffListRecyclerViewHolder extends RecyclerView.ViewHolder {

    public TextView text_staff_name;
    public TextView text_day_of_birth;
    public TextView text_mobile_number;

    public StaffListRecyclerViewHolder(View itemView) {
        super(itemView);
        text_staff_name = itemView.findViewById(R.id.text_staff_name);
        text_day_of_birth = itemView.findViewById(R.id.text_day_of_birth);
        text_mobile_number = itemView.findViewById(R.id.text_mobile_number);
    }
}
