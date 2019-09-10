package com.alice.mhp.alicecleaningmanagement.task;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.alice.mhp.alicecleaningmanagement.common.GetCustomerActivity;
import com.alice.mhp.alicecleaningmanagement.common.GetStaffActivity;
import com.alice.mhp.alicecleaningmanagement.R;
import com.alice.mhp.common.AppSettings;
import com.alice.mhp.common.Util;
import com.alice.mhp.dao.StaffAdd;

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
import java.util.Calendar;

public class TaskModifyActivity extends AppCompatActivity {

    TextView text_customer, text_date, text_start_time, text_end_time;
    ImageButton btn_search_customer, btn_search_date, btn_search_start_time, btn_search_end_time;
    EditText edit_customer_address, edit_note, edit_cleaning_hours;
    Spinner spinner_cleaning_type, spinner_task_status;
    Button btn_add_team_members, btn_cancel, btn_save;
    LinearLayout layout_task_staff_title, layout_task_staff_list;
    ArrayList workHoursList;
    Util util;
    ArrayList<StaffAdd> staffAddArrayList;
    static final int PICK_STAFF_REQUEST = 1;  // The request code
    static final int PICK_CUSTOMER_REQUEST = 2;  // The request code
    JSONArray typeOfCleaningJSONArray, taskStatusJSONArray;
    JSONArray taskInformation, taskStaffList;
    String cleanDate = "";
    String taskSeqNo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_modify);

        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getResources().getString(R.string.text_task));
            taskSeqNo = getIntent().getExtras().getString("taskSeqNo");


            util = new Util(this);

            text_customer = findViewById(R.id.text_customer);
            text_customer.setOnClickListener(btnFunction);

            text_date = findViewById(R.id.text_date);
            text_date.setOnClickListener(btnFunction);

            text_start_time = findViewById(R.id.text_start_time);
            text_start_time.setOnClickListener(btnFunction);

            text_end_time = findViewById(R.id.text_end_time);
            text_end_time.setOnClickListener(btnFunction);

            btn_search_customer = findViewById(R.id.btn_search_customer);
            btn_search_customer.setOnClickListener(btnFunction);

            btn_search_date = findViewById(R.id.btn_search_date);
            btn_search_date.setOnClickListener(btnFunction);

            btn_search_start_time = findViewById(R.id.btn_search_start_time);
            btn_search_start_time.setOnClickListener(btnFunction);

            btn_search_end_time = findViewById(R.id.btn_search_end_time);
            btn_search_end_time.setOnClickListener(btnFunction);

            edit_customer_address = findViewById(R.id.edit_customer_address);
            edit_note = findViewById(R.id.edit_note);

            edit_cleaning_hours = findViewById(R.id.edit_cleaning_hours);

            spinner_cleaning_type = findViewById(R.id.spinner_cleaning_type);

            spinner_task_status = findViewById(R.id.spinner_task_status);

            btn_add_team_members = findViewById(R.id.btn_add_team_members);
            btn_add_team_members.setOnClickListener(btnFunction);

            btn_cancel = findViewById(R.id.btn_cancel);
            btn_cancel.setOnClickListener(btnGoPage);

            btn_save = findViewById(R.id.btn_save);
            btn_save.setOnClickListener(btnGoPage);

            layout_task_staff_title = findViewById(R.id.layout_task_staff_title);
            layout_task_staff_list = findViewById(R.id.layout_task_staff_list);

            staffAddArrayList = new ArrayList<>();

            new LoadTypeOfCleaning().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        util.hideProgress();
    }

    // When back button click
    @Override
    public boolean onSupportNavigateUp()
    {
        this.finish();
        return super.onSupportNavigateUp();

    }@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // Check which request we're responding to
                if (requestCode == PICK_STAFF_REQUEST) {

                    String staffSeqNo = data.getStringExtra("staffSeqNo");
                    String staffName = data.getStringExtra("staffName");
                    String wages = data.getStringExtra("wages");

                    // If the employee's wage is not set, the default wage is displayed.
                    if(wages == null || wages.equals("") || wages.equals("0.00")) {
                        SharedPreferences dataOfPayPerHour = getSharedPreferences("setting", 0);
                        if(dataOfPayPerHour != null) {
                            String payPerHour = dataOfPayPerHour.getString("payPerHour","");
                            if(payPerHour != null && !payPerHour.equals("")) {
                                wages = payPerHour;
                            }
                        }
                    }
                    addStaff(staffSeqNo, staffName, wages);
                }
                else if (requestCode == PICK_CUSTOMER_REQUEST) {
                    String customerSeqNo = data.getStringExtra("customerSeqNo");
                    String customerName = data.getStringExtra("customerName");
                    addCustomer(customerSeqNo, customerName);
                }
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
                text_date.setText(dayOfMonth+"/"+(monthOfYear+1)+"/"+year);
                String month = util.addZero(monthOfYear+1);
                String day = util.addZero(dayOfMonth);
                cleanDate = year+"-"+month+"-"+day;

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    };

    public void showTimePicker(final String type, final TextView textText) {

        try {
            new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinutes) {
                    int hour, minute;
                    if(Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        hour = timePicker.getHour();        //selectedHour
                        minute = timePicker.getMinute();    //selectedMinutes
                    }
                    else {
                        hour = timePicker.getCurrentHour();
                        minute = timePicker.getCurrentMinute();
                    }
                    String hour_text = ""+hour;
                    String minute_text = ""+minute;
                    if(hour < 10) {
                        hour_text = "0"+hour;
                    }
                    if(minute < 10) {
                        minute_text = "0"+minute;
                    }

                    if(type.equals("start")) {
                        text_start_time.setText(hour_text+":"+minute_text);
                    }
                    else if(type.equals("end")) {
                        text_end_time.setText(hour_text+":"+minute_text);
                    }
                    else {
                        textText.setText(hour_text+":"+minute_text);
                    }


                }
            }, Calendar.HOUR_OF_DAY, Calendar.MINUTE, true).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showCustomerPopup() {
        try {
            Intent pickCustomerIntent = new Intent(TaskModifyActivity.this, GetCustomerActivity.class);
            startActivityForResult(pickCustomerIntent, PICK_CUSTOMER_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showStaffPopup() {
        try {
            Intent pickStaffIntent = new Intent(TaskModifyActivity.this, GetStaffActivity.class);
            startActivityForResult(pickStaffIntent, PICK_STAFF_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addStaff(String staffSeqNo, String staffName, String wages) {

        try {
            View view = getLayoutInflater().inflate(R.layout.layout_task_staff, null);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.topMargin = 10;
            view.setLayoutParams(params);

            TextView text_staff_name = view.findViewById(R.id.text_staff_name);
            text_staff_name.setText(staffName);
            TextView text_working_time_from = view.findViewById(R.id.edit_working_time_from);
            text_working_time_from.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showTimePicker("staffWorkStart", (TextView)view);
                }
            });
            TextView text_working_time_to = view.findViewById(R.id.edit_working_time_to);
            text_working_time_to.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showTimePicker("staffWorkEnd", (TextView)view);
                }
            });
            EditText edit_wages = view.findViewById(R.id.edit_wages);
            edit_wages.setText(wages);

            ImageButton btn_task_staff_delete = view.findViewById(R.id.btn_task_staff_delete);
            btn_task_staff_delete.setTag(staffAddArrayList.size());
            btn_task_staff_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("delNum",""+view.getTag());
                    Log.d("staffAddArrayList_1",""+staffAddArrayList.size());
                    staffAddArrayList.remove(view.getTag());
                    Log.d("staffAddArrayList_2",""+staffAddArrayList.size());
                    ((ViewGroup)view.getParent().getParent()).removeView((ViewGroup)view.getParent());
                }
            });

            StaffAdd staffAdd = new StaffAdd();
            staffAdd.setStaffSeqNo(staffSeqNo);
            staffAdd.setText_staff_name(text_staff_name);
            staffAdd.setText_working_time_from(text_working_time_from);
            staffAdd.setText_working_time_to(text_working_time_to);
            staffAdd.setEdit_wages(edit_wages);
            staffAdd.setBtn_task_staff_delete(btn_task_staff_delete);
            staffAddArrayList.add(staffAdd);

            layout_task_staff_list.addView(view);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addCustomer(String customerSeqNo, String customerName) {
        try {
            text_customer.setText(customerName);
            text_customer.setTag(customerSeqNo);
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
                                    Intent intent = new Intent(TaskModifyActivity.this, TaskDetailActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.putExtra("taskSeqNo", taskSeqNo);
                                    startActivity(intent);
                                    finish();
                                }
                            });


            alertDialogBuilder.show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    View.OnClickListener btnFunction = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.text_customer:
                    showCustomerPopup();
                    break;

                case R.id.text_date:
                    showDatePicker();
                    break;

                case R.id.text_start_time:
                    showTimePicker("start", null);
                    break;

                case R.id.text_end_time:
                    showTimePicker("end", null);
                    break;

                case R.id.btn_search_customer:
                    showCustomerPopup();
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

                case R.id.btn_add_team_members:
                    showStaffPopup();
                    break;

                default:
                    break;
            }
        }
    };


    View.OnClickListener btnGoPage = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {

                case R.id.btn_cancel:
                    finish();
                    break;

                case R.id.btn_save:
                    new UpdateTask().execute();
                    break;

                default:
                    break;
            }
        }
    };


    public void displayDetail() {
        try {
            String customerName = taskInformation.getJSONObject(0).getString("customerName");
            String customerSeqNo = taskInformation.getJSONObject(0).getString("customerSeqNo");
            String cleanDateDetail = taskInformation.getJSONObject(0).getString("cleanDate");
            String cleanAddress = taskInformation.getJSONObject(0).getString("cleanAddress");
            String cleanStartTime = taskInformation.getJSONObject(0).getString("cleanStartTime");
            String cleanEndTime = taskInformation.getJSONObject(0).getString("cleanEndTime");
            String cleanHours = taskInformation.getJSONObject(0).getString("cleanHours");
            String cleaningType = taskInformation.getJSONObject(0).getString("cleaningType");
            String taskStatus = taskInformation.getJSONObject(0).getString("taskStatus");
            String taskNote = taskInformation.getJSONObject(0).getString("taskNote");

            text_customer.setText(customerName);
            text_customer.setTag(customerSeqNo);
            text_date.setText(cleanDateDetail);
            cleanDate = cleanDateDetail;
            edit_customer_address.setText(cleanAddress);
            text_start_time.setText(cleanStartTime.substring(0, 5));
            text_end_time.setText(cleanEndTime.substring(0, 5));
            edit_cleaning_hours.setText(cleanHours);

            for(int num=0; num<typeOfCleaningJSONArray.length(); num++) {
                if(cleaningType.equals(typeOfCleaningJSONArray.getJSONObject(num).getString("codeName")))
                    spinner_cleaning_type.setSelection(num);
            }

            for(int num=0; num<taskStatusJSONArray.length(); num++) {
                if(taskStatus.equals(taskStatusJSONArray.getJSONObject(num).getString("codeName")))
                    spinner_task_status.setSelection(num);
            }

            edit_note.setText(taskNote);

            Log.d("taskStaffList", ""+taskStaffList.length());
            if(taskStaffList != null && taskStaffList.length() > 0) {
                for(int num=0;num<taskStaffList.length();num++) {

                    String staffName = taskStaffList.getJSONObject(num).getString("staffName");
                    String staffSeqNo = taskStaffList.getJSONObject(num).getString("staffSeqNo");
                    String workStartTime = taskStaffList.getJSONObject(num).getString("workStartTime").substring(0, 5);
                    String workEndTime = taskStaffList.getJSONObject(num).getString("workEndTime").substring(0, 5);
                    String wages = taskStaffList.getJSONObject(num).getString("payAmount");

                    View view = getLayoutInflater().inflate(R.layout.layout_task_staff, null);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.topMargin = 10;
                    view.setLayoutParams(params);

                    TextView text_staff_name = view.findViewById(R.id.text_staff_name);
                    text_staff_name.setText(staffName);
                    TextView text_working_time_from = view.findViewById(R.id.edit_working_time_from);
                    text_working_time_from.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showTimePicker("staffWorkStart", (TextView)view);
                        }
                    });
                    text_working_time_from.setText(workStartTime);

                    TextView text_working_time_to = view.findViewById(R.id.edit_working_time_to);
                    text_working_time_to.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showTimePicker("staffWorkEnd", (TextView)view);
                        }
                    });
                    text_working_time_to.setText(workEndTime);

                    EditText edit_wages = view.findViewById(R.id.edit_wages);
                    edit_wages.setText(wages);

                    ImageButton btn_task_staff_delete = view.findViewById(R.id.btn_task_staff_delete);
                    btn_task_staff_delete.setTag(num);
                    btn_task_staff_delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            staffAddArrayList.remove(Integer.parseInt(""+view.getTag()));
                            ((ViewGroup)view.getParent().getParent()).removeView((ViewGroup)view.getParent());
                        }
                    });

                    StaffAdd staffAdd = new StaffAdd();
                    staffAdd.setStaffSeqNo(staffSeqNo);
                    staffAdd.setText_staff_name(text_staff_name);
                    staffAdd.setText_working_time_from(text_working_time_from);
                    staffAdd.setText_working_time_to(text_working_time_to);
                    staffAdd.setEdit_wages(edit_wages);
                    staffAdd.setBtn_task_staff_delete(btn_task_staff_delete);
                    staffAddArrayList.add(staffAdd);

                    layout_task_staff_list.addView(view);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class LoadTypeOfCleaning extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            util.showProgress(getResources().getString(R.string.text_loading));
        }

        @Override
        protected String doInBackground(Void... voids) {
            StringBuilder builder = new StringBuilder();

            typeOfCleaningJSONArray = new JSONArray();
            taskStatusJSONArray = new JSONArray();

            try {
                JSONArray staffJSONArray = new JSONArray();
                JSONObject staffJSONObject = new JSONObject();
                staffJSONObject.put("codeGroup", "G003");
                staffJSONArray.put(staffJSONObject);

                staffJSONObject = new JSONObject();
                staffJSONObject.put("codeGroup", "G004");
                staffJSONArray.put(staffJSONObject);


                String data = URLEncoder.encode("codeGroupList", "UTF-8") + "=" + URLEncoder.encode(staffJSONArray.toString(), "UTF-8");
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
                Log.d("result==",result);
                JSONObject object = new JSONObject(result);
                typeOfCleaningJSONArray = object.getJSONArray("G003");
                taskStatusJSONArray = object.getJSONArray("G004");

                if(typeOfCleaningJSONArray.length() > 0) {
                    ArrayList<String> typdOfCleaningList = new ArrayList<String>();
                    for(int row=0; row<typeOfCleaningJSONArray.length(); row++) {
                        typdOfCleaningList.add(typeOfCleaningJSONArray.getJSONObject(row).getString("codeName"));

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(TaskModifyActivity.this, R.layout.row_spinner, typdOfCleaningList);
                        adapter.setDropDownViewResource(R.layout.row_spinners_dropdown);

                        spinner_cleaning_type.setPrompt(getResources().getString(R.string.text_type_of_cleaning)); // spinner title
                        spinner_cleaning_type.setAdapter(adapter);
                    }

                }

                if(taskStatusJSONArray.length() > 0) {
                    ArrayList<String> taskStatusList = new ArrayList<String>();
                    for(int row=0; row<taskStatusJSONArray.length(); row++) {
                        taskStatusList.add(taskStatusJSONArray.getJSONObject(row).getString("codeName"));

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(TaskModifyActivity.this, R.layout.row_spinner, taskStatusList);
                        adapter.setDropDownViewResource(R.layout.row_spinners_dropdown);

                        spinner_task_status.setPrompt(getResources().getString(R.string.text_task_status)); // spinner title
                        spinner_task_status.setAdapter(adapter);
                    }

                }


                new LoadTaskDetail().execute();



            }
            catch (Exception e) {
                util.hideProgress();
                e.printStackTrace();
            }
            util.hideProgress();
        }
    }

    class LoadTaskDetail extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            util.showProgress(getResources().getString(R.string.text_loading));
        }

        @Override
        protected String doInBackground(Void... voids) {
            StringBuilder builder = new StringBuilder();

            try {
                URL url = new URL(AppSettings.URL_ADDRESS + AppSettings.URL_GET_TASK_DETAIL);
                URLConnection urlConnection = url.openConnection();

                String data = URLEncoder.encode("companyId", "UTF-8") + "=" + URLEncoder.encode(AppSettings.COMPANY_ID, "UTF-8");
                data += "&" + URLEncoder.encode("taskSeqNo", "UTF-8") + "=" + URLEncoder.encode(taskSeqNo, "UTF-8");

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
                taskInformation = detailObject.getJSONArray("taskInformation");
                taskStaffList = detailObject.getJSONArray("taskStaffList");

                displayDetail();

            }
            catch (Exception e) {
                util.hideProgress();
                e.printStackTrace();
            }
            util.hideProgress();
        }
    }


    class UpdateTask extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            util.showProgress(getResources().getString(R.string.text_loading));
        }

        @Override
        protected String doInBackground(Void... voids) {
            StringBuilder builder = new StringBuilder();

            try {
                URL url = new URL(AppSettings.URL_ADDRESS + AppSettings.URL_UPDATE_TASK);

                URLConnection urlConnection = url.openConnection();

                JSONArray staffJSONArray = new JSONArray();
                Log.d("staffAddArrayList_size",""+staffAddArrayList.size());
                for(int num=0;num<staffAddArrayList.size();num++) {
                    if(staffAddArrayList.get(num).getStaffSeqNo() != null) {
                        JSONObject staffJSONObject = new JSONObject();
                        staffJSONObject.put("companyId", AppSettings.COMPANY_ID);
                        staffJSONObject.put("staffSeqNo", staffAddArrayList.get(num).getStaffSeqNo());
                        String workStartTime = "";
                        if(staffAddArrayList.get(num).getText_working_time_from().getText() != null) {
                            workStartTime = staffAddArrayList.get(num).getText_working_time_from().getText().toString();
                        }
                        staffJSONObject.put("workStartTime", workStartTime);

                        String workEndTime = "";
                        if(staffAddArrayList.get(num).getText_working_time_to().getText() != null) {
                            workEndTime = staffAddArrayList.get(num).getText_working_time_to().getText().toString();
                        }
                        staffJSONObject.put("workEndTime", workEndTime);

                        String wages = "";
                        if(staffAddArrayList.get(num).getEdit_wages().getText() != null) {
                            wages = staffAddArrayList.get(num).getEdit_wages().getText().toString();
                        }
                        staffJSONObject.put("payAmount", wages);

                        staffJSONArray.put(staffJSONObject);
                    }

                }

                int selectedCleaningNum = spinner_cleaning_type.getSelectedItemPosition();
                String cleaningType = typeOfCleaningJSONArray.getJSONObject(selectedCleaningNum).getString("codeNo");

                int selectedTaskStatusNum = spinner_task_status.getSelectedItemPosition();
                String taskStatus = taskStatusJSONArray.getJSONObject(selectedTaskStatusNum).getString("codeNo");

                String data = URLEncoder.encode("companyId", "UTF-8") + "=" + URLEncoder.encode(AppSettings.COMPANY_ID, "UTF-8");
                data += "&" + URLEncoder.encode("taskSeqNo", "UTF-8") + "=" + URLEncoder.encode(taskSeqNo, "UTF-8");
                data += "&" + URLEncoder.encode("customerSeqNo", "UTF-8") + "=" + URLEncoder.encode(text_customer.getTag()+"", "UTF-8");
                data += "&" + URLEncoder.encode("cleanAddress", "UTF-8") + "=" + URLEncoder.encode(edit_customer_address.getText()+"", "UTF-8");
                data += "&" + URLEncoder.encode("cleanDate", "UTF-8") + "=" + URLEncoder.encode(cleanDate, "UTF-8");
                data += "&" + URLEncoder.encode("cleanStartTime", "UTF-8") + "=" + URLEncoder.encode(text_start_time.getText()+"", "UTF-8");
                data += "&" + URLEncoder.encode("cleanEndTime", "UTF-8") + "=" + URLEncoder.encode(text_end_time.getText()+"", "UTF-8");
                data += "&" + URLEncoder.encode("cleanHours", "UTF-8") + "=" + URLEncoder.encode(edit_cleaning_hours.getText().toString()+"", "UTF-8");
                data += "&" + URLEncoder.encode("typeOfCleaning", "UTF-8") + "=" + URLEncoder.encode(cleaningType+"", "UTF-8");
                data += "&" + URLEncoder.encode("taskNote", "UTF-8") + "=" + URLEncoder.encode(edit_note.getText()+"", "UTF-8");
                data += "&" + URLEncoder.encode("taskStatus", "UTF-8") + "=" + URLEncoder.encode(taskStatus+"", "UTF-8");
                data += "&" + URLEncoder.encode("staffWorkingList", "UTF-8") + "=" + URLEncoder.encode(staffJSONArray.toString(), "UTF-8");


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
