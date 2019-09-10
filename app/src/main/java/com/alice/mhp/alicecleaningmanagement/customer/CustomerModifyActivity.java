package com.alice.mhp.alicecleaningmanagement.customer;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alice.mhp.alicecleaningmanagement.R;
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

public class CustomerModifyActivity extends AppCompatActivity {

    Util util;
    EditText edit_first_name, edit_last_name, edit_customer_address, edit_number_of_room, edit_number_of_bathroom, edit_mobile_number,
            edit_email_address, edit_note;

    Button btn_cancel, btn_save;
    String customerSeqNo;
    JSONArray customerDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_new);

        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle(getResources().getString(R.string.text_customer));
            actionBar.setDisplayHomeAsUpEnabled(true);

            util = new Util(this);

            customerSeqNo = getIntent().getExtras().getString("customerSeqNo");
            Log.d("customerSeqNo==",customerSeqNo);

            edit_first_name = findViewById(R.id.edit_first_name);
            edit_last_name = findViewById(R.id.edit_last_name);
            edit_customer_address = findViewById(R.id.edit_customer_address);
            edit_number_of_room = findViewById(R.id.edit_number_of_room);
            edit_number_of_bathroom = findViewById(R.id.edit_number_of_bathroom);
            edit_mobile_number = findViewById(R.id.edit_mobile_number);
            edit_email_address = findViewById(R.id.edit_email_address);
            edit_note = findViewById(R.id.edit_note);

            btn_cancel = findViewById(R.id.btn_cancel);
            btn_cancel.setOnClickListener(btnClick);

            btn_save = findViewById(R.id.btn_save);
            btn_save.setOnClickListener(btnClick);

            new LoadCustomerDetail().execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        util.hideProgress();
    }

    // When back button click
    @Override
    public boolean onSupportNavigateUp()
    {
        this.finish();
        return super.onSupportNavigateUp();

    }


    public void saveCustomer() {

        try {
            if(checkForm()) {
                new UpdateCustomer().execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean checkForm() {

        boolean result = false;
        try {
            if(edit_first_name.getText().toString().length() == 0) {
                showToast(getResources().getString(R.string.text_please_enter)+" "+getResources().getString(R.string.text_first_name));
                result = false;
            }
            else if(edit_last_name.getText().toString().length() == 0) {
                showToast(getResources().getString(R.string.text_please_enter)+" "+getResources().getString(R.string.text_last_name));
                result = false;
            }
            else if(edit_customer_address.getText().toString().length() == 0) {
                showToast(getResources().getString(R.string.text_please_enter)+" "+getResources().getString(R.string.text_address));
                result = false;
            }/*
            else if(edit_number_of_room.getText().toString().length() == 0) {
                showToast(getResources().getString(R.string.text_please_enter)+" "+getResources().getString(R.string.text_number_of_room));
                result = false;
            }
            else if(edit_number_of_bathroom.getText().toString().length() == 0) {
                showToast(getResources().getString(R.string.text_please_enter)+" "+getResources().getString(R.string.text_number_of_bathroom));
                result = false;
            }
            else if(edit_mobile_number.getText().toString().length() == 0) {
                showToast(getResources().getString(R.string.text_please_enter)+" "+getResources().getString(R.string.text_mobile_number));
                result = false;
            }
            else if(edit_email_address.getText().toString().length() == 0) {
                showToast(getResources().getString(R.string.text_please_enter)+" "+getResources().getString(R.string.text_email_address));
                result = false;
            }*/
            else {
                result = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    View.OnClickListener btnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_cancel:
                    finish();
                    break;

                case R.id.btn_save:
                    saveCustomer();
                    break;

                default:
                    break;
            }
        }
    };

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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
                                    Intent intent = new Intent(CustomerModifyActivity.this, CustomerDetailActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.putExtra("customerSeqNo", customerSeqNo);
                                    startActivity(intent);
                                    finish();
                                }
                            });


            alertDialogBuilder.show();

        } catch (Exception e) {
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

            edit_first_name.setText(customer.getCustomerFirstName());
            edit_last_name.setText(customer.getCustomerLastName());
            edit_number_of_room.setText(customer.getNumberOfRoom());
            edit_number_of_bathroom.setText(customer.getNumberOfBathRoom());
            edit_mobile_number.setText(customer.getCustomerMobile());
            edit_customer_address.setText(customer.getCustomerAddress());
            edit_email_address.setText(customer.getCustomerEmail());
            edit_note.setText(customer.getCustomerNote());


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

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
                URL url = new URL(AppSettings.URL_ADDRESS + AppSettings.URL_GET_CUSTOMER_DETAIL_MODIFY);
                URLConnection urlConnection = url.openConnection();

                String data = URLEncoder.encode("companyId", "UTF-8") + "=" + URLEncoder.encode(AppSettings.COMPANY_ID, "UTF-8");
                data += "&" + URLEncoder.encode("customerSeqNo", "UTF-8") + "=" + URLEncoder.encode(customerSeqNo, "UTF-8");

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

                displayDetail();

            }
            catch (Exception e) {
                util.hideProgress();
                e.printStackTrace();
            }
            util.hideProgress();
        }
    }


    class UpdateCustomer extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            util.showProgress(getResources().getString(R.string.text_loading));
        }

        @Override
        protected String doInBackground(Void... voids) {
            StringBuilder builder = new StringBuilder();

            try {
                URL url = new URL(AppSettings.URL_ADDRESS + AppSettings.URL_UPDATE_CUSTOMER);

                URLConnection urlConnection = url.openConnection();

                String data = URLEncoder.encode("companyId", "UTF-8") + "=" + URLEncoder.encode(AppSettings.COMPANY_ID, "UTF-8");
                data += "&" + URLEncoder.encode("customerSeqNo", "UTF-8") + "=" + URLEncoder.encode(customerSeqNo, "UTF-8");
                data += "&" + URLEncoder.encode("customerFirstName", "UTF-8") + "=" + URLEncoder.encode(edit_first_name.getText()+"", "UTF-8");
                data += "&" + URLEncoder.encode("customerLastName", "UTF-8") + "=" + URLEncoder.encode(edit_last_name.getText()+"", "UTF-8");
                data += "&" + URLEncoder.encode("numberOfRoom", "UTF-8") + "=" + URLEncoder.encode(edit_number_of_room.getText()+"", "UTF-8");
                data += "&" + URLEncoder.encode("numberOfBathroom", "UTF-8") + "=" + URLEncoder.encode(edit_number_of_bathroom.getText()+"", "UTF-8");
                data += "&" + URLEncoder.encode("customerMobile", "UTF-8") + "=" + URLEncoder.encode(edit_mobile_number.getText()+"", "UTF-8");
                data += "&" + URLEncoder.encode("customerAddress", "UTF-8") + "=" + URLEncoder.encode(edit_customer_address.getText()+"", "UTF-8");
                data += "&" + URLEncoder.encode("customerEmail", "UTF-8") + "=" + URLEncoder.encode(edit_email_address.getText()+"", "UTF-8");
                data += "&" + URLEncoder.encode("customerNote", "UTF-8") + "=" + URLEncoder.encode(edit_note.getText()+"", "UTF-8");


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
                util.hideProgress();
                Log.d("result",result);
                JSONObject resultObject = new JSONObject(result);

                if(resultObject.getString("result").equals("success")) {
                    alert(getResources().getString(R.string.text_alert_title), getResources().getString(R.string.text_success_save),getResources().getString(R.string.text_ok));
                }
                else {
                    util.alert(getResources().getString(R.string.text_alert_title), getResources().getString(R.string.text_fail),getResources().getString(R.string.text_ok));
                }

            }
            catch (Exception e) {
                util.hideProgress();
                e.printStackTrace();
            }
        }
    }
}
