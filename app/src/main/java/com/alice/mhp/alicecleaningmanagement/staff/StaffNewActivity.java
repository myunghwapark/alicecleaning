package com.alice.mhp.alicecleaningmanagement.staff;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.alice.mhp.alicecleaningmanagement.R;
import com.alice.mhp.alicecleaningmanagement.customer.CustomerDetailActivity;
import com.alice.mhp.alicecleaningmanagement.customer.CustomerNewActivity;
import com.alice.mhp.alicecleaningmanagement.task.TaskNewActivity;
import com.alice.mhp.common.AppSettings;
import com.alice.mhp.common.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;

public class StaffNewActivity extends AppCompatActivity {

    EditText edit_first_name, edit_last_name, edit_staff_address, edit_account_number, edit_mobile_number, edit_email_address,
            edit_wages, edit_note;
    TextView text_start_time, text_end_time, text_day_of_birth;
    ImageButton btn_search_start_time, btn_search_end_time, btn_search_date;
    Spinner spinner_bank;
    Button btn_cancel, btn_save;
    Util util;
    String staffSeqNo, birthday;
    JSONArray bankJSONArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_new);

        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle(getResources().getString(R.string.text_staff));
            actionBar.setDisplayHomeAsUpEnabled(true);

            util = new Util(this);

            edit_first_name = findViewById(R.id.edit_first_name);
            edit_last_name = findViewById(R.id.edit_last_name);
            edit_staff_address = findViewById(R.id.edit_staff_address);
            edit_account_number = findViewById(R.id.edit_account_number);
            edit_mobile_number = findViewById(R.id.edit_mobile_number);
            edit_email_address = findViewById(R.id.edit_email_address);
            edit_wages = findViewById(R.id.edit_wages);
            edit_note = findViewById(R.id.edit_note);

            text_start_time = findViewById(R.id.text_start_time);
            text_start_time.setOnClickListener(btnFunction);

            text_end_time = findViewById(R.id.text_end_time);
            text_end_time.setOnClickListener(btnFunction);

            text_day_of_birth = findViewById(R.id.text_day_of_birth);
            text_day_of_birth.setOnClickListener(btnFunction);

            btn_search_start_time = findViewById(R.id.btn_search_start_time);
            btn_search_start_time.setOnClickListener(btnFunction);

            btn_search_end_time = findViewById(R.id.btn_search_end_time);
            btn_search_end_time.setOnClickListener(btnFunction);

            btn_search_date = findViewById(R.id.btn_search_date);
            btn_search_date.setOnClickListener(btnFunction);

            spinner_bank = findViewById(R.id.spinner_bank);

            btn_cancel = findViewById(R.id.btn_cancel);
            btn_cancel.setOnClickListener(btnClick);

            btn_save = findViewById(R.id.btn_save);
            btn_save.setOnClickListener(btnClick);

            new LoadBankCode().execute();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        util.hideProgress();
    }

    public void saveCustomer() {

        try {
            if(checkForm()) {
                new InsertStaff().execute();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void showDatePicker() {

        try {
            Calendar calendar = Calendar.getInstance();
            //Theme holo light design
            //DatePickerDialog dialog = new DatePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT, datePickerListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            DatePickerDialog dialog = new DatePickerDialog(this, datePickerListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

        @Override

        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            try {
                text_day_of_birth.setText(dayOfMonth+"/"+(monthOfYear+1)+"/"+year);
                String month = util.addZero(monthOfYear+1);
                String day = util.addZero(dayOfMonth);
                birthday = year+"-"+month+"-"+day;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    };

    public void showTimePicker(final String type, final TextView textText) {

        new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinutes) {

                try {

                    int hour, minute;

                    if(Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        hour = timePicker.getHour();        //selectedHour
                        minute = timePicker.getMinute();    //selectedMinutes
                    }
                    else {
                        hour = timePicker.getCurrentHour();
                        minute = timePicker.getCurrentMinute();
                    }
                    String hour_text = util.addZero(hour);
                    String minute_text = util.addZero(minute);


                    if(type.equals("start")) {
                        text_start_time.setText(hour_text+":"+minute_text);
                    }
                    else if(type.equals("end")) {
                        text_end_time.setText(hour_text+":"+minute_text);
                    }
                    else {
                        textText.setText(hour_text+":"+minute_text);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, Calendar.HOUR_OF_DAY, Calendar.MINUTE, true).show();

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
            else if(edit_staff_address.getText().toString().length() == 0) {
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

    View.OnClickListener btnFunction = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {

                case R.id.text_day_of_birth:
                    showDatePicker();
                    break;

                case R.id.text_start_time:
                    showTimePicker("start", null);
                    break;

                case R.id.text_end_time:
                    showTimePicker("end", null);
                    break;

                case R.id.btn_search_date:
                    showDatePicker();
                    break;

                case R.id.btn_search_start_time:
                    showTimePicker("start", null);
                    break;

                case R.id.btn_search_end_time:
                    showTimePicker("end", null);
                    break;

                default:
                    break;
            }
        }
    };

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
        try {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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
                                    Intent intent = new Intent(StaffNewActivity.this, StaffDetailActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.putExtra("staffSeqNo", staffSeqNo);
                                    startActivity(intent);
                                    finish();
                                }
                            });


            alertDialogBuilder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // When back button click
    @Override
    public boolean onSupportNavigateUp()
    {
        this.finish();
        return super.onSupportNavigateUp();

    }

    class LoadBankCode extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // util.showProgress(getResources().getString(R.string.text_loading));
        }

        @Override
        protected String doInBackground(Void... voids) {
            StringBuilder builder = new StringBuilder();

            bankJSONArray = new JSONArray();

            try {
                JSONArray bankCodeJSONArray = new JSONArray();
                JSONObject bankCodeSONObject = new JSONObject();
                bankCodeSONObject.put("codeGroup", "G005");
                bankCodeJSONArray.put(bankCodeSONObject);


                String data = URLEncoder.encode("codeGroupList", "UTF-8") + "=" + URLEncoder.encode(bankCodeJSONArray.toString(), "UTF-8");
                URL url = new URL(AppSettings.URL_ADDRESS + AppSettings.URL_GET_CLEANING_CODE);
                Log.d("data==", data);

                URLConnection urlConnection = url.openConnection();
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
                JSONObject object = new JSONObject(result);
                bankJSONArray = object.getJSONArray("G005");

                if(bankJSONArray.length() > 0) {
                    ArrayList<String> bankList = new ArrayList<String>();
                    for(int row=0; row<bankJSONArray.length(); row++) {
                        bankList.add(bankJSONArray.getJSONObject(row).getString("codeName"));

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(StaffNewActivity.this, R.layout.row_spinner, bankList);
                        adapter.setDropDownViewResource(R.layout.row_spinners_dropdown);

                        spinner_bank.setPrompt(getResources().getString(R.string.text_bank)); // spinner title
                        spinner_bank.setAdapter(adapter);
                    }

                }


            }
            catch (Exception e) {
                util.hideProgress();
                e.printStackTrace();
            }
            // util.hideProgress();
        }
    }

    class InsertStaff extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            util.showProgress(getResources().getString(R.string.text_loading));
        }

        @Override
        protected String doInBackground(Void... voids) {
            StringBuilder builder = new StringBuilder();

            try {
                URL url = new URL(AppSettings.URL_ADDRESS + AppSettings.URL_INSERT_STAFF);

                URLConnection urlConnection = url.openConnection();

                int selectedBank = spinner_bank.getSelectedItemPosition();
                String bankCode = bankJSONArray.getJSONObject(selectedBank).getString("codeNo");

                String data = URLEncoder.encode("companyId", "UTF-8") + "=" + URLEncoder.encode(AppSettings.COMPANY_ID, "UTF-8");
                data += "&" + URLEncoder.encode("staffFirstName", "UTF-8") + "=" + URLEncoder.encode(edit_first_name.getText()+"", "UTF-8");
                data += "&" + URLEncoder.encode("staffLastName", "UTF-8") + "=" + URLEncoder.encode(edit_last_name.getText()+"", "UTF-8");
                data += "&" + URLEncoder.encode("availableTimeFrom", "UTF-8") + "=" + URLEncoder.encode(text_start_time.getText()+"", "UTF-8");
                data += "&" + URLEncoder.encode("availableTimeTo", "UTF-8") + "=" + URLEncoder.encode(text_end_time.getText()+"", "UTF-8");
                data += "&" + URLEncoder.encode("accountNumber", "UTF-8") + "=" + URLEncoder.encode(edit_account_number.getText()+"", "UTF-8");
                data += "&" + URLEncoder.encode("accountBank", "UTF-8") + "=" + URLEncoder.encode(bankCode+"", "UTF-8");
                data += "&" + URLEncoder.encode("staffMobile", "UTF-8") + "=" + URLEncoder.encode(edit_mobile_number.getText()+"", "UTF-8");
                data += "&" + URLEncoder.encode("staffAddress", "UTF-8") + "=" + URLEncoder.encode(edit_staff_address.getText()+"", "UTF-8");
                data += "&" + URLEncoder.encode("staffEmail", "UTF-8") + "=" + URLEncoder.encode(edit_email_address.getText()+"", "UTF-8");
                data += "&" + URLEncoder.encode("staffNote", "UTF-8") + "=" + URLEncoder.encode(edit_note.getText()+"", "UTF-8");
                data += "&" + URLEncoder.encode("staffDayOfBirth", "UTF-8") + "=" + URLEncoder.encode(birthday+"", "UTF-8");
                data += "&" + URLEncoder.encode("wages", "UTF-8") + "=" + URLEncoder.encode(edit_wages.getText()+"", "UTF-8");


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
                    staffSeqNo = resultObject.getString("staffSeqNo");
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
