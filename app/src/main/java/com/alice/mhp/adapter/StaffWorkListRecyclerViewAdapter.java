package com.alice.mhp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.alice.mhp.alicecleaningmanagement.R;
import com.alice.mhp.alicecleaningmanagement.staff.PayComplete;
import com.alice.mhp.dao.StaffWorkHistory;

import java.util.ArrayList;

public class StaffWorkListRecyclerViewAdapter extends RecyclerView.Adapter<StaffWorkListRecyclerViewHolder> {
    private ArrayList<StaffWorkHistory> staffWorkList = null;
    Context mContext;

    public StaffWorkListRecyclerViewAdapter(ArrayList<StaffWorkHistory> itemList) {
        staffWorkList = itemList;
    }

    // Create View
    @Override
    public StaffWorkListRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_staff_task_detail, parent, false);
        mContext = parent.getContext();

        StaffWorkListRecyclerViewHolder holder = new StaffWorkListRecyclerViewHolder(v);
        return holder;
    }

    // Call Recycler View, Adapter combines the data of corresponding position
    @Override
    public void onBindViewHolder(StaffWorkListRecyclerViewHolder holder, final int position) {

        try {
            holder.text_work_date.setText(staffWorkList.get(position).getWorkDate());
            holder.text_staff_working_time.setText(staffWorkList.get(position).getWorkStartTime().substring(0,5)+" - "+staffWorkList.get(position).getWorkEndTime().substring(0,5));
            holder.text_staff_wages.setText(staffWorkList.get(position).getPayAmount());

            String payYn = staffWorkList.get(position).getPayComplete();
            String payText = "";
            if(payYn.equals("Y")) {
                payText = mContext.getResources().getString(R.string.text_paid);
                holder.btn_pay_complete_yn.setBackground(mContext.getResources().getDrawable(R.drawable.btn_round_red_orange));
            }
            else {
                payText = mContext.getResources().getString(R.string.text_unpaid);
                holder.btn_pay_complete_yn.setBackground(mContext.getResources().getDrawable(R.drawable.btn_round_gray));
            }
            holder.btn_pay_complete_yn.setText(payText);
            holder.btn_pay_complete_yn.setTag(payYn+"_"+staffWorkList.get(position).getWorkHistorySeqNo());

            holder.btn_pay_complete_yn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String tags[] = (view.getTag()+"").split("_");
                    if(tags[0].equals("Y")) {
                        ((Button)view).setText(mContext.getResources().getString(R.string.text_unpaid));
                        view.setBackground(mContext.getResources().getDrawable(R.drawable.btn_round_gray));
                        view.setTag("N_"+tags[1]);
                        new PayComplete(mContext, tags[1]+"", "N").execute();
                    }
                    else {
                        ((Button)view).setText(mContext.getResources().getString(R.string.text_paid));
                        view.setBackground(mContext.getResources().getDrawable(R.drawable.btn_round_red_orange));
                        view.setTag("Y_"+tags[1]);
                        new PayComplete(mContext, tags[1]+"", "Y").execute();
                    }
                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    // Return data count
    @Override
    public int getItemCount() {
        return staffWorkList.size();
    }
}
