package com.alice.mhp.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alice.mhp.alicecleaningmanagement.customer.CustomerDetailActivity;
import com.alice.mhp.alicecleaningmanagement.common.GetCustomerActivity;
import com.alice.mhp.alicecleaningmanagement.R;
import com.alice.mhp.dao.Customer;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

public class CustomerListRecyclerViewAdapter extends RecyclerView.Adapter<CustomerListRecyclerViewHolder> {
    private ArrayList<Customer> customerList = null;
    Context mContext;
    boolean popupYn = false;

    public CustomerListRecyclerViewAdapter(ArrayList<Customer> itemList, boolean popup) {
        customerList = itemList;
        popupYn = popup;
    }

    // Create View
    @Override
    public CustomerListRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_customer_list, parent, false);
        mContext = parent.getContext();

        CustomerListRecyclerViewHolder holder = new CustomerListRecyclerViewHolder(v);
        return holder;
    }

    // Call Recycler View, Adapter combines the data of corresponding position
    @Override
    public void onBindViewHolder(CustomerListRecyclerViewHolder holder, final int position) {

        try {
            holder.text_customer_name.setText(customerList.get(position).getCustomerFirstName()+", "+customerList.get(position).getCustomerLastName());
            holder.text_customer_name.setTag(customerList.get(position).getCustomerSeqNo());
            holder.text_customer_address.setText(customerList.get(position).getCustomerAddress());

            holder.text_customer_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(popupYn) {
                        Intent pickCustomerIntent = new Intent();
                        pickCustomerIntent.putExtra("customerSeqNo", customerList.get(position).getCustomerSeqNo());
                        pickCustomerIntent.putExtra("customerName", customerList.get(position).getCustomerFirstName()+", "+customerList.get(position).getCustomerLastName());
                        pickCustomerIntent.putExtra("customerAddress", customerList.get(position).getCustomerAddress());
                        ((GetCustomerActivity)mContext).setResult(RESULT_OK, pickCustomerIntent);
                        ((GetCustomerActivity)mContext).finish();
                        //((GetCustomerActivity)mContext).startActivityForResult(pickCustomerIntent, 2);
                    }
                    else {
                        Intent intent = new Intent(mContext, CustomerDetailActivity.class);
                        intent.addFlags(FLAG_ACTIVITY_SINGLE_TOP);
                        intent.putExtra("customerSeqNo", "" + view.getTag());
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
        return customerList.size();
    }
}
