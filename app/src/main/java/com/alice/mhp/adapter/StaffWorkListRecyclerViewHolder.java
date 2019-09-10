package com.alice.mhp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alice.mhp.alicecleaningmanagement.R;


public class StaffWorkListRecyclerViewHolder extends RecyclerView.ViewHolder {

    public TextView text_work_date;
    public TextView text_staff_working_time;
    public TextView text_staff_wages;
    public Button btn_pay_complete_yn;

    public StaffWorkListRecyclerViewHolder(View itemView) {
        super(itemView);
        text_work_date = itemView.findViewById(R.id.text_work_date);
        text_staff_working_time = itemView.findViewById(R.id.text_staff_working_time);
        text_staff_wages = itemView.findViewById(R.id.text_staff_wages);
        btn_pay_complete_yn = itemView.findViewById(R.id.btn_pay_complete_yn);
    }
}
