package com.alice.mhp.alicecleaningmanagement.task;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alice.mhp.alicecleaningmanagement.R;
import com.alice.mhp.common.AppSettings;
import com.alice.mhp.common.Util;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.NoRouteToHostException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DayFragment extends Fragment {

    Util util;
    ImageButton btn_arrow_left, btn_arrow_right, btn_map;
    LinearLayout layout_day_list;
    FrameLayout layout_parent;
    FrameLayout layout_schedule;
    TextView text_date;
    Calendar dCal;
    Date date;
    JSONArray taskList = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.tab_day, viewGroup, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        try {
            util = new Util(getContext());

            btn_arrow_right = view.findViewById(R.id.btn_arrow_right);
            btn_arrow_right.setOnClickListener(changeDate);

            btn_arrow_left = view.findViewById(R.id.btn_arrow_left);
            btn_arrow_left.setOnClickListener(changeDate);

            layout_parent = view.findViewById(R.id.layout_parent);
            layout_day_list = view.findViewById(R.id.layout_day_list);
            layout_schedule = view.findViewById(R.id.layout_schedule);

            text_date = view.findViewById(R.id.text_date);

            btn_map = view.findViewById(R.id.btn_map);
            btn_map.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new LoadCoordinate(getContext(), taskList).execute();
                }
            });

            dCal = Calendar.getInstance();
            long now = System.currentTimeMillis();

            date = new Date(now);

            setTime();
            setYearMonthDate(date);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onStop() {
        super.onStop();
        util.hideProgress();
    }

    public void setYearMonthDate(Date date) {
        try {
            SimpleDateFormat curDateFormat = new SimpleDateFormat("dd.MMMM.yyyy", Locale.ENGLISH);
            text_date.setText(curDateFormat.format(date));

            new LoadDailyTask().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTime() {
        try {
            for(int time=1;time<=24;time++) {
                View view = getLayoutInflater().inflate(R.layout.layout_day_list, null);
                TextView text_time = view.findViewById(R.id.text_time);
                text_time.setHeight(100);

                text_time.setText(util.timeFormat(time));

                layout_day_list.addView(view);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setDaySchedule() {

        layout_parent.removeViews(1, (layout_parent.getChildCount()-1));

        if(taskList.length() != 0) {
            try {
                int colors[] = {R.color.redOrange, R.color.btnBgBlue, R.color.btnBorderRedLight};
                int colorNum = 0;
                for (int row = 0; row < taskList.length(); row++) {
                    String taskSeqNo = taskList.getJSONObject(row).getString("taskSeqNo");
                    String customerSeqNo = taskList.getJSONObject(row).getString("customerSeqNo");
                    String customerFirstName = taskList.getJSONObject(row).getString("customerFirstName");
                    String cleanDate = taskList.getJSONObject(row).getString("cleanDate");
                    String cleanStartTime = taskList.getJSONObject(row).getString("cleanStartTime");
                    String cleanEndTime = taskList.getJSONObject(row).getString("cleanEndTime");
                    String cleanHours = taskList.getJSONObject(row).getString("cleanHours");
                    String cleaningType = taskList.getJSONObject(row).getString("cleaningType");
                    String taskNote = taskList.getJSONObject(row).getString("taskNote");
                    String taskStatus = taskList.getJSONObject(row).getString("taskStatus");
                    String staffNumber = taskList.getJSONObject(row).getString("staffNumber");

                    String cleanDay = cleanDate.substring(cleanDate.length() - 2, cleanDate.length());

                    LinearLayout layout = new LinearLayout(getContext());
                    layout.setOrientation(LinearLayout.VERTICAL);
                    layout.setBackgroundColor(getResources().getColor(colors[colorNum]));
                    if(colorNum < 2) {
                        colorNum++;
                    }
                    else {
                        colorNum = 0;
                    }

                    TextView schedule = new TextView(getContext());
                    schedule.setTextColor(getResources().getColor(R.color.white));
                    schedule.setText(cleanStartTime.substring(0,5)+"-"+cleanEndTime.substring(0,5));

                    TextView scheduleInfo = new TextView(getContext());
                    scheduleInfo.setTextColor(getResources().getColor(R.color.white));
                    scheduleInfo.setText(customerFirstName+", staff "+staffNumber);

                    layout.addView(schedule);
                    layout.addView(scheduleInfo);
                    layout.setTag(taskSeqNo);
                    layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity().getApplication(), TaskDetailActivity.class);
                            intent.putExtra("taskSeqNo", view.getTag()+"");
                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
                        }
                    });

                    int startTime = Integer.parseInt(cleanStartTime.substring(0,2));
                    int endTime = Integer.parseInt(cleanEndTime.substring(0,2));

                    int top = (startTime-1) * 103;
                    int height = (endTime - startTime) * 100;
                    layout.setMinimumHeight(height);
                    layout.setMinimumWidth(350);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMargins(250, top, 0, 0);

                    layout.setLayoutParams(params);
                    layout_parent.addView(layout);

                }
            }
            catch(JSONException e) {
                e.printStackTrace();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void minusDate() {
        try {
            dCal.add(Calendar.DATE, -1);
            date = dCal.getTime();
            setYearMonthDate(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void plusDate() {
        try {
            dCal.add(Calendar.DATE, +1);
            date = dCal.getTime();
            setYearMonthDate(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    View.OnClickListener changeDate = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            switch (view.getId()) {
                case R.id.btn_arrow_left:
                    minusDate();
                    break;

                case R.id.btn_arrow_right:
                    plusDate();
                    break;

                default:
                    break;
            }
        }
    };


    class LoadDailyTask extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            util.showProgress(getResources().getString(R.string.text_loading));
        }

        @Override
        protected String doInBackground(Void... voids) {
            StringBuilder builder = new StringBuilder();

            try {
                URL url = new URL(AppSettings.URL_ADDRESS + AppSettings.URL_GET_TASK_LIST);
                URLConnection urlConnection = url.openConnection();

                int year = dCal.get(Calendar.YEAR);
                int month = (dCal.get(Calendar.MONTH) + 1);
                int startDate = dCal.get(Calendar.DATE);
                int endDate = dCal.get(Calendar.DATE);

                String strYear = year+"";
                String strMonth = util.addZero(month);
                String strStartDate = util.addZero(startDate);
                String strEndDate = util.addZero(endDate);

                String data = URLEncoder.encode("companyId", "UTF-8") + "=" + URLEncoder.encode(AppSettings.COMPANY_ID, "UTF-8");
                data += "&" + URLEncoder.encode("startDate", "UTF-8") + "=" + URLEncoder.encode(strYear+"-"+strMonth+"-"+strStartDate, "UTF-8");
                data += "&" + URLEncoder.encode("endDate", "UTF-8") + "=" + URLEncoder.encode(strYear+"-"+strMonth+"-"+strEndDate, "UTF-8");

                Log.d("inputData_daily", data);
                urlConnection.setDoOutput(true);
                OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
                writer.write(data);
                writer.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            } catch(NoRouteToHostException e) {
                //util.alert(getResources().getString(R.string.text_alert_title), getResources().getString(R.string.text_no_route_host),getResources().getString(R.string.text_ok));
                e.printStackTrace();
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
                taskList = new JSONArray(result);

                setDaySchedule();

            }
            catch (Exception e) {
                util.hideProgress();
                e.printStackTrace();
            }
            util.hideProgress();
        }
    }

}
