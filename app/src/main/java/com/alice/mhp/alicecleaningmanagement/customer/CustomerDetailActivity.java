package com.alice.mhp.alicecleaningmanagement.customer;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alice.mhp.adapter.CustomerRequestRecyclerViewAdapter;
import com.alice.mhp.adapter.StaffWorkListRecyclerViewAdapter;
import com.alice.mhp.alicecleaningmanagement.common.CommonActivity;
import com.alice.mhp.alicecleaningmanagement.R;
import com.alice.mhp.alicecleaningmanagement.staff.StaffDetailActivity;
import com.alice.mhp.alicecleaningmanagement.staff.StaffModifyActivity;
import com.alice.mhp.alicecleaningmanagement.task.TaskListActivity;
import com.alice.mhp.common.AppSettings;
import com.alice.mhp.common.Util;
import com.alice.mhp.dao.Customer;
import com.alice.mhp.dao.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

public class CustomerDetailActivity extends CommonActivity {

    TextView text_customer_name, text_customer_address, text_number_of_room, text_number_of_bathroom, text_customer_email
            , text_customer_mobile, text_customer_note;
    ImageButton btn_call, btn_sms;

    Util util;
    ArrayList<Task> requestHistoryArrayList;
    JSONArray customerDetail;
    String customerSeqNo;
    int startPageNum = 0;
    int rowCount = 10;
    CustomerRequestRecyclerViewAdapter sAdapter;
    Button btn_delete, btn_modify, btn_request_history;
    private PopupWindow mPopupWindow ;
    boolean lockListView = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_detail);

        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle(getResources().getString(R.string.text_customer));
            actionBar.setDisplayHomeAsUpEnabled(true);

            util = new Util(this);

            customerSeqNo = getIntent().getExtras().getString("customerSeqNo");
            Log.d("customerSeqNo==",customerSeqNo);

            text_customer_name = findViewById(R.id.text_customer_name);
            text_customer_address = findViewById(R.id.text_customer_address);
            text_number_of_room = findViewById(R.id.text_number_of_room);
            text_number_of_bathroom = findViewById(R.id.text_number_of_bathroom);
            text_customer_email = findViewById(R.id.text_customer_email);
            text_customer_mobile = findViewById(R.id.text_customer_mobile);
            text_customer_note = findViewById(R.id.text_customer_note);

            btn_call = findViewById(R.id.btn_call);
            btn_sms = findViewById(R.id.btn_sms);

            requestHistoryArrayList = new ArrayList<>();

            btn_delete = findViewById(R.id.btn_delete);
            btn_delete.setOnClickListener(goPage);

            btn_modify = findViewById(R.id.btn_modify);
            btn_modify.setOnClickListener(goPage);

            btn_request_history = findViewById(R.id.btn_request_history);
            btn_request_history.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showRequestHistory();
                }
            });

            new LoadCustomerDetail().execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showRequestHistory() {
        try {
            View popupView = getLayoutInflater().inflate(R.layout.layout_staff_working_history, null);
            mPopupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

            // when click dialog outside, popup window finish
            mPopupWindow.setFocusable(true);
            mPopupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

            TextView text_title = popupView.findViewById(R.id.text_title);
            text_title.setText(getResources().getString(R.string.text_request_history));

            TextView text_no_list = popupView.findViewById(R.id.text_no_list);

            Button btnClose = popupView.findViewById(R.id.btn_close);
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPopupWindow.dismiss();
                }
            });

            RecyclerView recycler_view = popupView.findViewById(R.id.recycler_view);
            recycler_view.setLayoutManager(new LinearLayoutManager(this));
            recycler_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    try {

                        // End of List
                        if (!recyclerView.canScrollVertically(1)) {
                            if(!lockListView) {
                                lockListView = true;
                                startPageNum++;
                                new LoadCustomerDetail().execute();
                            }

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            sAdapter = new CustomerRequestRecyclerViewAdapter(requestHistoryArrayList);
            recycler_view.setAdapter(sAdapter);

            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getApplicationContext(),new LinearLayoutManager(this).getOrientation());
            recycler_view.addItemDecoration(dividerItemDecoration);

            lockListView = false;

            if(requestHistoryArrayList.size() == 0) {
                text_no_list.setVisibility(View.VISIBLE);
                lockListView = true;
            }

        }
        catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public void displayDetail() {

        try {
            JSONObject object = customerDetail.getJSONObject(0);

            Customer customer = new Customer();
            customer.setCustomerFirstName(object.getString("customerFirstName"));
            customer.setCustomerLastName(object.getString("customerLastName"));
            customer.setNumberOfRoom(object.getString("numberOfRoom"));
            customer.setNumberOfBathRoom(object.getString("numberOfBathroom"));
            customer.setCustomerMobile(object.getString("customerMobile"));
            customer.setCustomerAddress(object.getString("customerAddress"));
            customer.setCustomerEmail(object.getString("customerEmail"));
            customer.setCustomerNote(object.getString("customerNote"));

            text_customer_name.setText(customer.getCustomerFirstName()+", "+customer.getCustomerLastName());
            text_number_of_room.setText(customer.getNumberOfRoom());
            text_number_of_bathroom.setText(customer.getNumberOfBathRoom());

            text_customer_mobile.setText(customer.getCustomerMobile());
            if(customer.getCustomerMobile() != null && !customer.getCustomerMobile().equals("")) {
                btn_call.setTag(customer.getCustomerMobile());
                btn_call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            util.call(""+view.getTag());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                btn_sms.setTag(customer.getCustomerMobile());
                btn_sms.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            util.checkSmsPermission(""+view.getTag());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            text_customer_address.setText(customer.getCustomerAddress());
            text_customer_email.setText(customer.getCustomerEmail());
            if(customer.getCustomerEmail() != null && !customer.getCustomerEmail().equals("")) {
                text_customer_email.setTag(customer.getCustomerEmail());
                text_customer_email.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            util.sendEmail(""+view.getTag());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            text_customer_note.setText(customer.getCustomerNote());


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void requestHistoryMore() {
        try {
            sAdapter.notifyDataSetChanged();
            lockListView = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void alert(String title, String message, String btnText) {

        try {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

            alertDialogBuilder.setTitle(title)
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton(btnText,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();

                                    Intent intent = new Intent(CustomerDetailActivity.this, CustomerListActivity.class);
                                    startActivity(intent);
                                    finish();

                                }
                            });


            alertDialogBuilder.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void confirm(String title, String message, String btnText) {

        try {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

            alertDialogBuilder.setTitle(title)
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton(btnText,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    new DeleteCustomer().execute();
                                }
                            });

            alertDialogBuilder.setNegativeButton(getResources().getString(R.string.text_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });


            alertDialogBuilder.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    View.OnClickListener goPage = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_modify:
                    Intent intent = new Intent(CustomerDetailActivity.this, CustomerModifyActivity.class);
                    intent.putExtra("customerSeqNo", customerSeqNo);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    break;

                case R.id.btn_delete:
                    confirm(getResources().getString(R.string.text_alert_title), getResources().getString(R.string.text_confirm_delete_customer),getResources().getString(R.string.text_ok));

                    break;

                default:
                    break;
            }
        }
    };

    class LoadCustomerDetail extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            util.showProgress(getResources().getString(R.string.text_loading));
        }

        @Override
        protected String doInBackground(Void... voids) {
            StringBuilder builder = new StringBuilder();

            try {
                URL url = new URL(AppSettings.URL_ADDRESS + AppSettings.URL_GET_CUSTOMER_DETAIL);
                URLConnection urlConnection = url.openConnection();

                String data = URLEncoder.encode("companyId", "UTF-8") + "=" + URLEncoder.encode(AppSettings.COMPANY_ID, "UTF-8");
                data += "&" + URLEncoder.encode("customerSeqNo", "UTF-8") + "=" + URLEncoder.encode(customerSeqNo, "UTF-8");
                data += "&" + URLEncoder.encode("startPageNum", "UTF-8") + "=" + URLEncoder.encode(""+startPageNum, "UTF-8");
                data += "&" + URLEncoder.encode("rowCount", "UTF-8") + "=" + URLEncoder.encode(""+rowCount, "UTF-8");

                Log.d("inputdata", data);
                urlConnection.setDoOutput(true);
                OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
                writer.write(data);
                writer.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return builder.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                Log.d("result==",result);
                JSONObject detailObject = new JSONObject(result);
                customerDetail = detailObject.getJSONArray("customerDetail");
                JSONArray customerHistoryList = detailObject.getJSONArray("customerHistoryList");

                if(customerHistoryList != null && customerHistoryList.length() > 0) {
                    for(int row=0;row<customerHistoryList.length();row++) {
                        JSONObject object = customerHistoryList.getJSONObject(row);
                        Task requestHistory = new Task();
                        requestHistory.setTaskSeqNo(object.getString("taskSeqNo"));
                        requestHistory.setCleanDate(object.getString("cleanDate"));
                        requestHistory.setCleanStartTime(object.getString("cleanStartTime"));
                        requestHistory.setCleanEndTime(object.getString("cleanEndTime"));
                        requestHistory.setCleanHours(object.getString("cleanHours"));
                        requestHistory.setCleaningType(object.getString("cleaningType"));
                        requestHistory.setTaskStatus(object.getString("taskStatus"));

                        requestHistoryArrayList.add(requestHistory);
                    }
                }
                else {
                    if(startPageNum != 0){
                        lockListView = true;
                    }
                }

                if(startPageNum == 0) {
                    displayDetail();
                }
                else if(customerHistoryList != null && customerHistoryList.length() > 0) {
                    requestHistoryMore();
                }


            }
            catch (Exception e) {
                util.hideProgress();
                e.printStackTrace();
            }
            util.hideProgress();
        }
    }

    class DeleteCustomer extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            util.showProgress(getResources().getString(R.string.text_loading));
        }

        @Override
        protected String doInBackground(Void... voids) {
            StringBuilder builder = new StringBuilder();

            try {
                URL url = new URL(AppSettings.URL_ADDRESS + AppSettings.URL_DELETE_CUSTOMER);
                URLConnection urlConnection = url.openConnection();

                String data = URLEncoder.encode("customerSeqNo", "UTF-8") + "=" + URLEncoder.encode(customerSeqNo, "UTF-8");

                urlConnection.setDoOutput(true);
                OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
                writer.write(data);
                writer.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return builder.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                Log.d("result==",result);
                JSONObject resultObject = new JSONObject(result);

                if(resultObject.getString("result").equals("success")) {
                    alert(getResources().getString(R.string.text_alert_title), getResources().getString(R.string.text_success_delete),getResources().getString(R.string.text_ok));
                }
                else {
                    util.alert(getResources().getString(R.string.text_alert_title), getResources().getString(R.string.text_fail),getResources().getString(R.string.text_ok));
                }

            }
            catch (Exception e) {
                util.hideProgress();
                e.printStackTrace();
            }
            util.hideProgress();
        }
    }
}
