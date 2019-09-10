package com.alice.mhp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.alice.mhp.alicecleaningmanagement.R;
import com.alice.mhp.dao.Customer;
import com.alice.mhp.dao.StaffWorkHistory;
import com.alice.mhp.dao.Task;

import java.util.ArrayList;

public class CustomerRequestRecyclerViewAdapter extends RecyclerView.Adapter<CustomerRequestRecyclerViewHolder> {
    private ArrayList<Task> customerRequestList = null;
    Context mContext;

    public CustomerRequestRecyclerViewAdapter(ArrayList<Task> itemList) {
        customerRequestList = itemList;
    }

    // Create View
    @Override
    public CustomerRequestRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_customer_request, parent, false);
        mContext = parent.getContext();

        CustomerRequestRecyclerViewHolder holder = new CustomerRequestRecyclerViewHolder(v);
        return holder;
    }

    // Call Recycler View, Adapter combines the data of corresponding position
    @Override
    public void onBindViewHolder(CustomerRequestRecyclerViewHolder holder, final int position) {

        try {
            holder.text_clean_date.setText(customerRequestList.get(position).getCleanDate());
            holder.text_clean_time.setText(customerRequestList.get(position).getCleanStartTime().substring(0,5)+" - "+customerRequestList.get(position).getCleanEndTime().substring(0,5));
            holder.text_clean_type.setText(customerRequestList.get(position).getCleaningType());

        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    // Return data count
    @Override
    public int getItemCount() {
        return customerRequestList.size();
    }
}
