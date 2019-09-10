package com.alice.mhp.alicecleaningmanagement.staff;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alice.mhp.adapter.RecyclerViewDecoration;
import com.alice.mhp.adapter.StaffWorkListRecyclerViewAdapter;
import com.alice.mhp.alicecleaningmanagement.common.CommonActivity;
import com.alice.mhp.alicecleaningmanagement.R;
import com.alice.mhp.alicecleaningmanagement.customer.CustomerDetailActivity;
import com.alice.mhp.alicecleaningmanagement.customer.CustomerListActivity;
import com.alice.mhp.alicecleaningmanagement.task.TaskDetailActivity;
import com.alice.mhp.alicecleaningmanagement.task.TaskListActivity;
import com.alice.mhp.alicecleaningmanagement.task.TaskModifyActivity;
import com.alice.mhp.common.AppSettings;
import com.alice.mhp.common.Util;
import com.alice.mhp.dao.Staff;
import com.alice.mhp.dao.StaffWorkHistory;

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

public class StaffDetailActivity extends CommonActivity {

    TextView text_staff_name, text_available_time, text_account_number, text_staff_address, text_staff_email, text_day_of_birth,
            text_staff_wages, text_staff_note, text_staff_mobile;
    ImageButton btn_call, btn_sms;

    JSONArray staffInformation;
    ArrayList<StaffWorkHistory> workHistoryArrayList;
    String staffSeqNo;
    Util util;
    int startPageNum = 0;
    int rowCount = 10;
    StaffWorkListRecyclerViewAdapter sAdapter;
    boolean lockListView = false;
    Button btn_delete, btn_modify, btn_working_history;
    private PopupWindow mPopupWindow ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_detail);

        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle(getResources().getString(R.string.text_staff));
            actionBar.setDisplayHomeAsUpEnabled(true);

            util = new Util(this);

            staffSeqNo = getIntent().getExtras().getString("staffSeqNo");
            Log.d("staffSeqNo==",staffSeqNo);

            text_staff_name = findViewById(R.id.text_staff_name);
            text_available_time = findViewById(R.id.text_available_time);
            text_account_number = findViewById(R.id.text_account_number);
            text_staff_address = findViewById(R.id.text_staff_address);
            text_staff_email = findViewById(R.id.text_staff_email);
            text_day_of_birth = findViewById(R.id.text_day_of_birth);
            text_staff_wages = findViewById(R.id.text_staff_wages);
            text_staff_note = findViewById(R.id.text_staff_note);
            text_staff_mobile = findViewById(R.id.text_staff_mobile);

            btn_call = findViewById(R.id.btn_call);
            btn_sms = findViewById(R.id.btn_sms);

            workHistoryArrayList = new ArrayList<>();

            btn_delete = findViewById(R.id.btn_delete);
            btn_delete.setOnClickListener(goPage);

            btn_modify = findViewById(R.id.btn_modify);
            btn_modify.setOnClickListener(goPage);

            btn_working_history = findViewById(R.id.btn_working_history);
            btn_working_history.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showWorkingHistory();
                }
            });

            new LoadStaffDetail().execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onRestart() {
        super.onRestart();

        workHistoryArrayList = new ArrayList<>();
        new LoadStaffDetail().execute();
    }

    View.OnClickListener goPage = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_modify:
                    Intent intent = new Intent(StaffDetailActivity.this, StaffModifyActivity.class);
                    intent.putExtra("staffSeqNo", staffSeqNo);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    break;

                case R.id.btn_delete:
                    confirm(getResources().getString(R.string.text_alert_title), getResources().getString(R.string.text_confirm_delete_staff),getResources().getString(R.string.text_ok));
                    break;

                default:
                    break;
            }
        }
    };


    public void showWorkingHistory() {

        try {
            View popupView = getLayoutInflater().inflate(R.layout.layout_staff_working_history, null);
            mPopupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

            // when click dialog outside, popup window finish
            mPopupWindow.setFocusable(true);
            mPopupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

            TextView text_title = popupView.findViewById(R.id.text_title);
            text_title.setText(getResources().getString(R.string.text_working_history));

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
                                new LoadStaffDetail().execute();
                            }

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            sAdapter = new StaffWorkListRecyclerViewAdapter(workHistoryArrayList);
            recycler_view.setAdapter(sAdapter);

            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getApplicationContext(),new LinearLayoutManager(this).getOrientation());
            recycler_view.addItemDecoration(dividerItemDecoration);

            lockListView = false;

            if(workHistoryArrayList.size() == 0) {
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
            JSONObject object = staffInformation.getJSONObject(0);

            Staff staff = new Staff();
            staff.setStaffFirstName(object.getString("staffFirstName"));
            staff.setStaffLastName(object.getString("staffLastName"));
            staff.setAvailableTimeFrom(object.getString("availableTimeFrom"));
            staff.setAvailableTimeTo(object.getString("availableTimeTo"));
            staff.setAccountNumber(object.getString("accountNumber"));
            staff.setAccountBank(object.getString("accountBank"));

            staff.setStaffMobile(object.getString("staffMobile"));
            staff.setStaffAddress(object.getString("staffAddress"));
            staff.setStaffEmail(object.getString("staffEmail"));
            staff.setStaffDayOfBirth(object.getString("staffDayOfBirth"));
            staff.setWages(object.getString("wages"));
            staff.setStaffNote(object.getString("staffNote"));

            if(staff.getAvailableTimeFrom() != null && !staff.getAvailableTimeFrom().equals("")) {
                int timeFrom = Integer.parseInt(staff.getAvailableTimeFrom());
                staff.setAvailableTimeFrom(util.timeFormat(timeFrom));
            }
            if(staff.getAvailableTimeTo() != null && !staff.getAvailableTimeTo().equals("")) {
                int timeTo = Integer.parseInt(staff.getAvailableTimeTo());
                staff.setAvailableTimeTo(util.timeFormat(timeTo));
            }
            text_staff_name.setText(staff.getStaffFirstName()+", "+staff.getStaffLastName());
            text_available_time.setText(staff.getAvailableTimeFrom()+" - "+staff.getAvailableTimeTo());
            text_account_number.setText(staff.getAccountNumber()+" "+staff.getAccountBank());
            text_staff_address.setText(staff.getStaffAddress());

            text_staff_email.setText(staff.getStaffEmail());
            if(staff.getStaffEmail() != null && !staff.getStaffEmail().equals("")) {
                text_staff_email.setTag(staff.getStaffEmail());
                text_staff_email.setOnClickListener(new View.OnClickListener() {
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


            text_day_of_birth.setText(staff.getStaffDayOfBirth());
            text_staff_wages.setText("$"+staff.getWages());
            text_staff_note.setText(staff.getStaffNote());

            text_staff_mobile.setText(staff.getStaffMobile());
            if(staff.getStaffMobile() != null && !staff.getStaffMobile().equals("")) {
                btn_call.setTag(staff.getStaffMobile());
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

                btn_sms.setTag(staff.getStaffMobile());
                btn_sms.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            util.showSmsDialog(""+view.getTag());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void workingHistoryMore() {
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

                                    Intent intent = new Intent(StaffDetailActivity.this, StaffListActivity.class);
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
                                    new DeleteStaff().execute();
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


    class LoadStaffDetail extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            util.showProgress(getResources().getString(R.string.text_loading));
        }

        @Override
        protected String doInBackground(Void... voids) {
            StringBuilder builder = new StringBuilder();

            try {
                URL url = new URL(AppSettings.URL_ADDRESS + AppSettings.URL_GET_STAFF_DETAIL);
                URLConnection urlConnection = url.openConnection();

                String data = URLEncoder.encode("companyId", "UTF-8") + "=" + URLEncoder.encode(AppSettings.COMPANY_ID, "UTF-8");
                data += "&" + URLEncoder.encode("staffSeqNo", "UTF-8") + "=" + URLEncoder.encode(staffSeqNo, "UTF-8");
                data += "&" + URLEncoder.encode("startPageNum", "UTF-8") + "=" + URLEncoder.encode(""+startPageNum, "UTF-8");
                data += "&" + URLEncoder.encode("rowCount", "UTF-8") + "=" + URLEncoder.encode(""+rowCount, "UTF-8");

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
                staffInformation = detailObject.getJSONArray("staffInformation");
                JSONArray staffTaskList = detailObject.getJSONArray("staffTaskList");

                if(staffTaskList != null && staffTaskList.length() > 0) {
                    for(int row=0;row<staffTaskList.length();row++) {
                        JSONObject object = staffTaskList.getJSONObject(row);
                        StaffWorkHistory workHistory = new StaffWorkHistory();
                        workHistory.setWorkHistorySeqNo(object.getString("workHistorySeqNo"));
                        workHistory.setPayAmount(object.getString("payAmount"));
                        workHistory.setPayComplete(object.getString("payComplete"));
                        workHistory.setTaskSeqNo(object.getString("taskSeqNo"));
                        workHistory.setWorkDate(object.getString("workDate"));
                        workHistory.setWorkStartTime(object.getString("workStartTime"));
                        workHistory.setWorkEndTime(object.getString("workEndTime"));
                        workHistoryArrayList.add(workHistory);
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
                else if(staffTaskList != null && staffTaskList.length() > 0) {
                    workingHistoryMore();
                }

            }
            catch (Exception e) {
                util.hideProgress();
                e.printStackTrace();
            }
            util.hideProgress();
        }
    }

    class DeleteStaff extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            util.showProgress(getResources().getString(R.string.text_loading));
        }

        @Override
        protected String doInBackground(Void... voids) {
            StringBuilder builder = new StringBuilder();

            try {
                URL url = new URL(AppSettings.URL_ADDRESS + AppSettings.URL_DELETE_STAFF);
                URLConnection urlConnection = url.openConnection();

                String data = URLEncoder.encode("staffSeqNo", "UTF-8") + "=" + URLEncoder.encode(staffSeqNo, "UTF-8");

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
