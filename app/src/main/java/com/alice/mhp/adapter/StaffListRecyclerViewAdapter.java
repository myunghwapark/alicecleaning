package com.alice.mhp.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alice.mhp.alicecleaningmanagement.common.GetStaffActivity;
import com.alice.mhp.alicecleaningmanagement.R;
import com.alice.mhp.alicecleaningmanagement.staff.StaffDetailActivity;
import com.alice.mhp.dao.Staff;

import java.util.ArrayList;


import static android.app.Activity.RESULT_OK;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

public class StaffListRecyclerViewAdapter extends RecyclerView.Adapter<StaffListRecyclerViewHolder> {
    private ArrayList<Staff> staffList = null;
    Context mContext;
    boolean popupYn = false;

    public StaffListRecyclerViewAdapter(ArrayList<Staff> itemList, boolean popup) {
        staffList = itemList;
        popupYn = popup;
    }

    // Create View
    @Override
    public StaffListRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_staff_list, parent, false);
        mContext = parent.getContext();

        StaffListRecyclerViewHolder holder = new StaffListRecyclerViewHolder(v);
        return holder;
    }

    // Call Recycler View, Adapter combines the data of corresponding position
    @Override
    public void onBindViewHolder(StaffListRecyclerViewHolder holder, final int position) {

        try {
            holder.text_staff_name.setText(staffList.get(position).getStaffFirstName()+", "+staffList.get(position).getStaffLastName());
            holder.text_staff_name.setTag(staffList.get(position).getStaffSeqNo());
            holder.text_day_of_birth.setText(staffList.get(position).getStaffDayOfBirth());
            holder.text_mobile_number.setText(staffList.get(position).getStaffMobile());

            holder.text_staff_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(popupYn) {
                        Intent pickStaffIntent = new Intent();
                        pickStaffIntent.putExtra("staffSeqNo", staffList.get(position).getStaffSeqNo());
                        pickStaffIntent.putExtra("staffName", staffList.get(position).getStaffFirstName());
                        pickStaffIntent.putExtra("wages", staffList.get(position).getWages());
                        ((GetStaffActivity)mContext).setResult(RESULT_OK, pickStaffIntent);
                        ((GetStaffActivity)mContext).finish();
                        //((GetStaffActivity)mContext).startActivityForResult(pickStaffIntent, 1);
                    }
                    else {
                        Intent intent = new Intent(mContext, StaffDetailActivity.class);
                        intent.addFlags(FLAG_ACTIVITY_SINGLE_TOP);
                        intent.putExtra("staffSeqNo", ""+v.getTag());
                        mContext.startActivity(intent);
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
        return staffList.size();
    }
}
