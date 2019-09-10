package com.alice.mhp.alicecleaningmanagement.customer;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.alice.mhp.adapter.CustomerListRecyclerViewAdapter;
import com.alice.mhp.alicecleaningmanagement.common.CommonActivity;
import com.alice.mhp.alicecleaningmanagement.R;
import com.alice.mhp.common.AppSettings;
import com.alice.mhp.common.Util;
import com.alice.mhp.dao.Customer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

public class CustomerListActivity extends CommonActivity {

    private RecyclerView customer_list_view;
    LinearLayout layout_search_customer;
    EditText edit_search_customer;
    ImageButton btn_search_customer, btn_new_customer;
    Spinner spinner_search_type;
    public CustomerListRecyclerViewAdapter sAdapter;
    public ArrayList<Customer> customerList;
    Util util;
    int pageNum = 0;
    boolean lockListView = false;

    String searchType = "";
    boolean searchYn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_list);

        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle(getResources().getString(R.string.text_customer));


            util = new Util(this);

            layout_search_customer = findViewById(R.id.layout_search_customer);

            edit_search_customer = findViewById(R.id.edit_search_customer);
            edit_search_customer.setOnEditorActionListener(editorActionListener);
            edit_search_customer.addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {}

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    lockListView = false;
                }
            });

            btn_search_customer = findViewById(R.id.btn_search_customer);
            btn_search_customer.setOnClickListener(btnClick);

            spinner_search_type = findViewById(R.id.spinner_search_type);
            spinner_search_type.setOnItemSelectedListener(onItemSelectedListener);

            customerList = new ArrayList<>();

            customer_list_view = findViewById(R.id.customer_list_view);
            customer_list_view.setLayoutManager(new LinearLayoutManager(this));
            customer_list_view.addOnScrollListener(recyclerScroll);

            btn_new_customer = findViewById(R.id.btn_new_customer);
            btn_new_customer.setOnClickListener(btnClick);

            new LoadCustomerList().execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        customerList = new ArrayList<>();
        new LoadCustomerList().execute();
    }

    public void setList() {

        try {
            sAdapter = new CustomerListRecyclerViewAdapter(customerList, false);
            customer_list_view.setAdapter(sAdapter);
            DividerItemDecoration dividerItemDecoration =
                    new DividerItemDecoration(getApplicationContext(), new LinearLayoutManager(this).getOrientation());
            customer_list_view.addItemDecoration(dividerItemDecoration);
            lockListView = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void moreList() {
        try {
            sAdapter.notifyDataSetChanged();
            lockListView = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void searchCustomer() {

        try {
            util.hideKeyboard(edit_search_customer.getWindowToken());

            if(!lockListView) {

                lockListView = true;

                if(edit_search_customer.getText().length() != 0) {
                    searchYn = true;
                }
                else {
                    searchYn = false;
                }
                pageNum = 0;
                customerList = new ArrayList<>();

                new LoadCustomerList().execute();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    View.OnClickListener btnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_search_customer:
                    searchCustomer();
                    break;

                case R.id.btn_new_customer:
                    Intent intent = new Intent(CustomerListActivity.this, CustomerNewActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    break;

                default:
                    break;
            }
        }
    };

    TextView.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchCustomer();
                return true;
            }
            return false;
        }
    };

    AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            lockListView = false;

            switch (position) {
                case 0:
                    searchType = "firstName";
                    break;
                case 1:
                    searchType = "lastName";
                    break;
                case 2:
                    searchType = "address";
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    RecyclerView.OnScrollListener recyclerScroll = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            try {
                // Top of List
                if (!customer_list_view.canScrollVertically(-1)) {

                    layout_search_customer.setVisibility(View.VISIBLE);
                }
                // End of List
                else if (!customer_list_view.canScrollVertically(1)) {
                    if(!lockListView) {
                        lockListView = true;
                        pageNum++;
                        new LoadCustomerList().execute();
                    }

                }
                // Middle of List
                else {
                    layout_search_customer.setVisibility(View.GONE);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    class LoadCustomerList extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            util.showProgress(getResources().getString(R.string.text_loading));
        }

        @Override
        protected String doInBackground(Void... voids) {
            StringBuilder builder = new StringBuilder();

            try {
                URL url;

                if(searchYn) {
                    url = new URL(AppSettings.URL_ADDRESS + AppSettings.URL_GET_CUSTOMER_SEARCH);
                }
                else {
                    url = new URL(AppSettings.URL_ADDRESS + AppSettings.URL_GET_CUSTOMER_LIST);
                }
                URLConnection urlConnection = url.openConnection();


                String data = URLEncoder.encode("companyId", "UTF-8") + "=" + URLEncoder.encode(AppSettings.COMPANY_ID, "UTF-8");
                data += "&" + URLEncoder.encode("startPageNum", "UTF-8") + "=" + URLEncoder.encode(pageNum+"", "UTF-8");

                if(searchYn) {
                    data += "&" + URLEncoder.encode("searchType", "UTF-8") + "=" + URLEncoder.encode(searchType, "UTF-8");
                    data += "&" + URLEncoder.encode("searchText", "UTF-8") + "=" + URLEncoder.encode(edit_search_customer.getText().toString(), "UTF-8");
                }

                Log.d("inputData", data);
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
                Log.d("result",result);
                JSONArray customerJSONList = new JSONArray(result);

                if(customerJSONList.length() > 0) {
                    for(int row=0; row<customerJSONList.length(); row++) {
                        Customer customer = new Customer();
                        JSONObject object = customerJSONList.getJSONObject(row);

                        Log.d("has_numberOfRoom",""+object.has("numberOfRoom"));
                        String customerSeqNo = object.getString("customerSeqNo");
                        String customerFirstName = object.getString("customerFirstName");
                        String customerLastName = object.getString("customerLastName");
                        String customerMobile = object.getString("customerMobile");
                        String customerAddress = object.getString("customerAddress");
                        String customerEmail = object.getString("customerEmail");

                        customer.setCustomerSeqNo(customerSeqNo);
                        customer.setCustomerFirstName(customerFirstName);
                        customer.setCustomerLastName(customerLastName);
                        customer.setCustomerMobile(customerMobile);
                        customer.setCustomerAddress(customerAddress);
                        customer.setCustomerEmail(customerEmail);

                        customerList.add(customer);
                    }

                    if(pageNum == 0) {
                        setList();
                    }
                    else {
                        moreList();
                    }
                }
                // in case of no data
                else {
                    if(pageNum != 0){
                        lockListView = true;
                    }
                    else {
                        customerList = new ArrayList<>();
                        setList();
                    }

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
