package com.alice.mhp.alicecleaningmanagement.common;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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

import com.alice.mhp.adapter.StaffListRecyclerViewAdapter;
import com.alice.mhp.alicecleaningmanagement.R;
import com.alice.mhp.common.AppSettings;
import com.alice.mhp.common.Util;
import com.alice.mhp.dao.Staff;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

public class GetStaffActivity extends AppCompatActivity {

    private RecyclerView staff_list_view;
    LinearLayout layout_search_staff;
    EditText edit_search_staff;
    ImageButton btn_search_staff, btn_close;
    Spinner spinner_search_type;
    public StaffListRecyclerViewAdapter sAdapter;
    public ArrayList<Staff> staffList;
    Util util;
    int pageNum = 0;
    boolean lockListView = false;

    String searchType = "";
    boolean searchYn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_staff);

        try {
            getSupportActionBar().hide();

            util = new Util(this);

            btn_close = findViewById(R.id.btn_close);
            btn_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent pickStaffIntent = new Intent();
                    setResult(RESULT_CANCELED, pickStaffIntent);
                    finish();
                }
            });

            layout_search_staff = findViewById(R.id.layout_search_staff);

            edit_search_staff = findViewById(R.id.edit_search_staff);
            edit_search_staff.setOnEditorActionListener(editorActionListener);
            edit_search_staff.addTextChangedListener(new TextWatcher() {

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

            btn_search_staff = findViewById(R.id.btn_search_staff);
            btn_search_staff.setOnClickListener(searchOnClick);

            spinner_search_type = findViewById(R.id.spinner_search_type);
            spinner_search_type.setOnItemSelectedListener(onItemSelectedListener);

            staffList = new ArrayList<>();

            staff_list_view = findViewById(R.id.staff_list_view);
            staff_list_view.setLayoutManager(new LinearLayoutManager(this));
            staff_list_view.addOnScrollListener(recyclerScroll);

            new LoadStaffList().execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        util.hideProgress();
    }

    public void setList() {

        try {
            sAdapter = new StaffListRecyclerViewAdapter(staffList, true);
            staff_list_view.setAdapter(sAdapter);
            DividerItemDecoration dividerItemDecoration =
                    new DividerItemDecoration(getApplicationContext(),new LinearLayoutManager(this).getOrientation());
            staff_list_view.addItemDecoration(dividerItemDecoration);
            //staff_list_view.addItemDecoration(new RecyclerViewDecoration(10));
            lockListView = false;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void moreList() {
        sAdapter.notifyDataSetChanged();
        lockListView = false;
    }

    public void searchStaff() {

        try {
            util.hideKeyboard(edit_search_staff.getWindowToken());

            if(!lockListView) {

                lockListView = true;

                if(edit_search_staff.getText().length() != 0) {
                    searchYn = true;
                }
                else {
                    searchYn = false;
                }
                pageNum = 0;
                staffList = new ArrayList<>();
                new LoadStaffList().execute();

            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    View.OnClickListener searchOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            searchStaff();
        }
    };

    TextView.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchStaff();
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
                if (!staff_list_view.canScrollVertically(-1)) {

                    layout_search_staff.setVisibility(View.VISIBLE);
                }
                // End of List
                else if (!staff_list_view.canScrollVertically(1)) {
                    if(!lockListView) {
                        lockListView = true;
                        pageNum++;
                        new LoadStaffList().execute();
                    }

                }
                // Middle of List
                else {
                    layout_search_staff.setVisibility(View.GONE);
                }

            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    class LoadStaffList extends AsyncTask<Void, Void, String> {
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
                    url = new URL(AppSettings.URL_ADDRESS + AppSettings.URL_GET_STAFF_SEARCH);
                }
                else {
                    url = new URL(AppSettings.URL_ADDRESS + AppSettings.URL_GET_STAFF_LIST);
                }
                URLConnection urlConnection = url.openConnection();


                String data = URLEncoder.encode("companyId", "UTF-8") + "=" + URLEncoder.encode(AppSettings.COMPANY_ID, "UTF-8");
                data += "&" + URLEncoder.encode("startPageNum", "UTF-8") + "=" + URLEncoder.encode(pageNum+"", "UTF-8");

                if(searchYn) {
                    data += "&" + URLEncoder.encode("searchType", "UTF-8") + "=" + URLEncoder.encode(searchType, "UTF-8");
                    data += "&" + URLEncoder.encode("searchText", "UTF-8") + "=" + URLEncoder.encode(edit_search_staff.getText().toString(), "UTF-8");
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
                JSONArray staffJSONList = new JSONArray(result);

                if(staffJSONList.length() > 0) {
                    for(int row=0; row<staffJSONList.length(); row++) {
                        Staff staff = new Staff();
                        String staffSeqNo = staffJSONList.getJSONObject(row).getString("staffSeqNo");
                        String staffFirstName = staffJSONList.getJSONObject(row).getString("staffFirstName");
                        String staffLastName = staffJSONList.getJSONObject(row).getString("staffLastName");
                        String availableTimeFrom = staffJSONList.getJSONObject(row).getString("availableTimeFrom");
                        String availableTimeTo = staffJSONList.getJSONObject(row).getString("availableTimeTo");
                        String accountNumber = staffJSONList.getJSONObject(row).getString("accountNumber");
                        String accountBank = staffJSONList.getJSONObject(row).getString("accountBank");
                        String staffMobile = staffJSONList.getJSONObject(row).getString("staffMobile");
                        String staffAddress = staffJSONList.getJSONObject(row).getString("staffAddress");
                        String staffEmail = staffJSONList.getJSONObject(row).getString("staffEmail");
                        String staffDayOfBirth = staffJSONList.getJSONObject(row).getString("staffDayOfBirth");
                        String wages = staffJSONList.getJSONObject(row).getString("wages");
                        String staffNote = staffJSONList.getJSONObject(row).getString("staffNote");

                        staff.setStaffSeqNo(staffSeqNo);
                        staff.setStaffFirstName(staffFirstName);
                        staff.setStaffLastName(staffLastName);
                        staff.setAvailableTimeFrom(availableTimeFrom);
                        staff.setAvailableTimeTo(availableTimeTo);
                        staff.setAccountNumber(accountNumber);
                        staff.setAccountBank(accountBank);
                        staff.setStaffMobile(staffMobile);
                        staff.setStaffAddress(staffAddress);
                        staff.setStaffEmail(staffEmail);
                        staff.setStaffDayOfBirth(staffDayOfBirth);
                        staff.setWages(wages);
                        staff.setStaffNote(staffNote);

                        staffList.add(staff);
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
                        staffList = new ArrayList<>();
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
