package com.alice.mhp.alicecleaningmanagement.task;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alice.mhp.alicecleaningmanagement.common.CommonActivity;
import com.alice.mhp.alicecleaningmanagement.R;
import com.alice.mhp.alicecleaningmanagement.staff.PayComplete;
import com.alice.mhp.alicecleaningmanagement.staff.StaffDetailActivity;
import com.alice.mhp.common.AppSettings;
import com.alice.mhp.common.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class TaskDetailActivity extends CommonActivity {

    String taskSeqNo;
    Util util;
    JSONArray taskInformation, taskStaffList;
    TextView text_customer_name, text_clean_date, text_clean_address, text_cleaning_start_time,
            text_cleaning_end_time, text_cleaning_hours, text_type_of_cleaning, text_task_status, text_task_note,
            text_team_member_count;
    ImageButton btn_share;
    LinearLayout layout_task_staff_list;
    Button btn_delete, btn_modify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle(getResources().getString(R.string.text_task));
            actionBar.setDisplayHomeAsUpEnabled(true);

            util = new Util(this);

            taskSeqNo = getIntent().getExtras().getString("taskSeqNo");
            Log.d("taskSeqNo==",taskSeqNo);

            text_customer_name = findViewById(R.id.text_customer_name);
            text_clean_date = findViewById(R.id.text_clean_date);
            text_clean_address = findViewById(R.id.text_clean_address);
            text_cleaning_start_time = findViewById(R.id.text_cleaning_start_time);
            text_cleaning_end_time = findViewById(R.id.text_cleaning_end_time);
            text_cleaning_hours = findViewById(R.id.text_cleaning_hours);
            text_type_of_cleaning = findViewById(R.id.text_type_of_cleaning);
            text_task_status = findViewById(R.id.text_task_status);
            text_task_note = findViewById(R.id.text_task_note);
            text_team_member_count = findViewById(R.id.text_team_member_count);
            layout_task_staff_list = findViewById(R.id.layout_task_staff_list);
            btn_share = findViewById(R.id.btn_share);
            btn_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showShareDialog();
                }
            });

            btn_delete = findViewById(R.id.btn_delete);
            btn_delete.setOnClickListener(goPage);

            btn_modify = findViewById(R.id.btn_modify);
            btn_modify.setOnClickListener(goPage);


            new LoadTaskDetail().execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showShareDialog() {

        try {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(getResources().getString(R.string.text_cleaning_schedule));
            String content = "["+text_clean_date.getText().toString()+" "
                    +text_cleaning_start_time.getText().toString()+" - "+text_cleaning_end_time.getText().toString()
                    +"] \n"+text_clean_address.getText().toString()
                    +"\n"+text_type_of_cleaning.getText().toString();
            dialog.setMessage(content);

            final EditText edit_message = new EditText(this);
            dialog.setView(edit_message);

            dialog.setPositiveButton(getResources().getString(R.string.text_share), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    shareTask(edit_message.getText().toString());
                    dialog.dismiss();
                }
            });

            dialog.setNegativeButton(getResources().getString(R.string.text_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            dialog.show();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void shareTask(String addMessage) {

        try {
            Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setType("text/plain");
            String subject = getResources().getString(R.string.text_cleaning_schedule);
            String content = "["+text_clean_date.getText().toString()+" "
                    +text_cleaning_start_time.getText().toString()+" - "+text_cleaning_end_time.getText().toString()
                    +"] \n"+text_clean_address.getText().toString()
                    +"\n"+text_type_of_cleaning.getText().toString()
                    +"\n"+addMessage;
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            intent.putExtra(Intent.EXTRA_TEXT, content);

            Intent chooser = Intent.createChooser(intent, "Share Task");
            startActivity(chooser);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void displayDetail() {

        try {
            String customerName = taskInformation.getJSONObject(0).getString("customerName");
            String cleanDate = taskInformation.getJSONObject(0).getString("cleanDate");
            String cleanAddress = taskInformation.getJSONObject(0).getString("cleanAddress");
            String cleanStartTime = taskInformation.getJSONObject(0).getString("cleanStartTime");
            String cleanEndTime = taskInformation.getJSONObject(0).getString("cleanEndTime");
            String cleanHours = taskInformation.getJSONObject(0).getString("cleanHours");
            String cleaningType = taskInformation.getJSONObject(0).getString("cleaningType");
            String taskStatus = taskInformation.getJSONObject(0).getString("taskStatus");
            String taskNote = taskInformation.getJSONObject(0).getString("taskNote");

            text_customer_name.setText(customerName);
            text_clean_date.setText(cleanDate);
            text_clean_address.setText(cleanAddress);
            text_cleaning_start_time.setText(cleanStartTime.substring(0, 5));
            text_cleaning_end_time.setText(cleanEndTime.substring(0, 5));
            text_cleaning_hours.setText(cleanHours);
            text_type_of_cleaning.setText(cleaningType);
            text_task_status.setText(taskStatus);
            text_task_note.setText(taskNote);
            text_team_member_count.setText("("+taskStaffList.length()+")");

            if(taskStaffList != null && taskStaffList.length() > 0) {
                for(int num=0;num<taskStaffList.length();num++) {

                    View view = getLayoutInflater().inflate(R.layout.layout_task_staff_detail, null);
                    TextView text_staff_name = view.findViewById(R.id.text_staff_name);
                    TextView text_staff_working_time = view.findViewById(R.id.text_staff_working_time);
                    TextView text_staff_wages = view.findViewById(R.id.text_staff_wages);
                    Button btn_pay_complete_yn = view.findViewById(R.id.btn_pay_complete_yn);

                    text_staff_name.setText(taskStaffList.getJSONObject(num).getString("staffName"));
                    String workStartTime = taskStaffList.getJSONObject(num).getString("workStartTime");
                    String workEndTime = taskStaffList.getJSONObject(num).getString("workEndTime");
                    text_staff_working_time.setText(workStartTime.substring(0, 5)+"-"+workEndTime.substring(0, 5));
                    text_staff_wages.setText("$"+taskStaffList.getJSONObject(num).getString("payAmount"));

                    String payCompleteYn = taskStaffList.getJSONObject(num).getString("payComplete");
                    String payComplete = "";
                    if(payCompleteYn.equals("Y")) {
                        payComplete = getResources().getString(R.string.text_paid);
                        btn_pay_complete_yn.setBackground(getResources().getDrawable(R.drawable.btn_round_red_orange));
                    }
                    else {
                        payComplete = getResources().getString(R.string.text_unpaid);
                        btn_pay_complete_yn.setBackground(getResources().getDrawable(R.drawable.btn_round_gray));
                    }
                    btn_pay_complete_yn.setText(payComplete);
                    btn_pay_complete_yn.setTag(payCompleteYn+"_"+taskStaffList.getJSONObject(num).getString("workHistorySeqNo"));
                    btn_pay_complete_yn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String tags[] = (view.getTag()+"").split("_");
                            if(tags[0].equals("Y")) {
                                ((Button)view).setText(getResources().getString(R.string.text_unpaid));
                                view.setBackground(getResources().getDrawable(R.drawable.btn_round_gray));
                                view.setTag("N_"+tags[1]);
                                new PayComplete(TaskDetailActivity.this, tags[1]+"", "N").execute();
                            }
                            else {
                                ((Button)view).setText(getResources().getString(R.string.text_paid));
                                view.setBackground(getResources().getDrawable(R.drawable.btn_round_red_orange));
                                view.setTag("Y_"+tags[1]);
                                new PayComplete(TaskDetailActivity.this, tags[1]+"", "Y").execute();
                            }
                        }
                    });

                    layout_task_staff_list.addView(view);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void goModifyPage() {

        try {
            Intent intent = new Intent(TaskDetailActivity.this, TaskModifyActivity.class);
            intent.putExtra("taskSeqNo", taskSeqNo);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    View.OnClickListener goPage = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_modify:
                    goModifyPage();
                    break;

                case R.id.btn_delete:
                    confirm(getResources().getString(R.string.text_alert_title), getResources().getString(R.string.text_confirm_delete_task),getResources().getString(R.string.text_ok));
                    break;

                default:
                    break;
            }
        }
    };


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
                                    Intent intent = new Intent(TaskDetailActivity.this, TaskListActivity.class);
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
                                    new DeleteTask().execute();
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


    class DeleteTask extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            util.showProgress(getResources().getString(R.string.text_loading));
        }

        @Override
        protected String doInBackground(Void... voids) {
            StringBuilder builder = new StringBuilder();

            try {
                URL url = new URL(AppSettings.URL_ADDRESS + AppSettings.URL_DELETE_TASK);
                URLConnection urlConnection = url.openConnection();

                String data = URLEncoder.encode("taskSeqNo", "UTF-8") + "=" + URLEncoder.encode(taskSeqNo, "UTF-8");

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
