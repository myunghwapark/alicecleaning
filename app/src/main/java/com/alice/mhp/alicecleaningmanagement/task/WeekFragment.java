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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alice.mhp.alicecleaningmanagement.R;
import com.alice.mhp.alicecleaningmanagement.task.TaskDetailActivity;
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

public class WeekFragment extends Fragment {

    ImageButton btn_arrow_left, btn_arrow_right, btn_map;
    TextView text_month;
    LinearLayout layout_week;

    Util util;
    Calendar sunday;
    JSONArray taskList = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.tab_week, viewGroup, false);
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
            btn_arrow_left = getView().findViewById(R.id.btn_arrow_left);
            btn_arrow_left.setOnClickListener(arrowClick);

            btn_arrow_right = getView().findViewById(R.id.btn_arrow_right);
            btn_arrow_right.setOnClickListener(arrowClick);

            text_month = getView().findViewById(R.id.text_month);
            layout_week = getView().findViewById(R.id.layout_week);

            btn_map = view.findViewById(R.id.btn_map);
            btn_map.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new LoadCoordinate(getContext(), taskList).execute();
                }
            });

            util = new Util(getContext());

            sunday = util.getCurSunday();
            new LoadWeeklyTask().execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        util.hideProgress();
    }


    public void nextWeek() {

        try {
            sunday.add(Calendar.DATE, 1);
            new LoadWeeklyTask().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void prevWeek() {

        try {
            sunday.add(Calendar.DATE, -13);
            java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy.MM.dd");
            //setWeek();
            new LoadWeeklyTask().execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setWeek() {


        try {
            String dayOfWeek[] = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

            String monthName = util.getMonth(sunday.get(Calendar.MONTH));
            text_month.setText(monthName);

            layout_week.removeAllViews();

            for(int dayNum=0;dayNum<dayOfWeek.length;dayNum++) {
                if(dayNum != 0) {
                    sunday.add(Calendar.DATE, 1);
                }
                Date date = sunday.getTime();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String dateStr = dateFormat.format(date);


                View view = getLayoutInflater().inflate(R.layout.layout_week, null);
                TextView text_week_date = view.findViewById(R.id.text_week_date);
                text_week_date.setText(""+sunday.get(Calendar.DATE));

                TextView text_day_of_the_week = view.findViewById(R.id.text_day_of_the_week);
                text_day_of_the_week.setText(""+dayOfWeek[sunday.get(Calendar.DAY_OF_WEEK)-1]);
                if(sunday.get(Calendar.DAY_OF_WEEK) == 1) {
                    text_week_date.setTextColor(getResources().getColor(R.color.redOrange));
                    text_day_of_the_week.setTextColor(getResources().getColor(R.color.redOrange));
                }
                else if(sunday.get(Calendar.DAY_OF_WEEK) == 7) {
                    text_week_date.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    text_day_of_the_week.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                }

                LinearLayout layout_week_schedule = view.findViewById(R.id.layout_week_schedule);

                if(taskList.length() != 0) {
                    for(int row=0; row< taskList.length();row++) {
                        String taskSeqNo = taskList.getJSONObject(row).getString("taskSeqNo");
                        String customerFirstName = taskList.getJSONObject(row).getString("customerFirstName");
                        String cleanDate = taskList.getJSONObject(row).getString("cleanDate");
                        String cleanStartTime = taskList.getJSONObject(row).getString("cleanStartTime");
                        String cleanEndTime = taskList.getJSONObject(row).getString("cleanEndTime");
                        String staffNumber = taskList.getJSONObject(row).getString("staffNumber");

                        if(dateStr.equals(cleanDate)) {
                            String cleaningTime = cleanStartTime.substring(0,5)+"-"+cleanEndTime.substring(0,5);
                            TextView text_schedule_detail = new TextView(getContext());
                            text_schedule_detail.setTextSize(16);
                            text_schedule_detail.setTextColor(getResources().getColor(R.color.black));
                            text_schedule_detail.setText(cleaningTime+"  "+customerFirstName+"  staff "+staffNumber);
                            text_schedule_detail.setTag(taskSeqNo);
                            text_schedule_detail.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(getActivity().getApplication(), TaskDetailActivity.class);
                                    intent.putExtra("taskSeqNo", view.getTag()+"");
                                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    startActivity(intent);
                                }
                            });

                            layout_week_schedule.addView(text_schedule_detail);

                        }
                    }
                }


                LinearLayout layout_line = view.findViewById(R.id.layout_line);

                if(dayNum == dayOfWeek.length-1) {
                    layout_line.setVisibility(View.GONE);
                }
                layout_week.addView(view);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    View.OnClickListener arrowClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_arrow_left:
                    prevWeek();
                    break;

                case R.id.btn_arrow_right:
                    nextWeek();
                    break;

                default:
                    break;
            }
        }
    };


    class LoadWeeklyTask extends AsyncTask<Void, Void, String> {
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

                int year = sunday.get(Calendar.YEAR);
                int month = (sunday.get(Calendar.MONTH) + 1);
                int startDate = sunday.get(Calendar.DATE);

                sunday.add(Calendar.DATE, 6);

                int endYear = sunday.get(Calendar.YEAR);
                int endMonth = (sunday.get(Calendar.MONTH) + 1);
                int endDate = sunday.get(Calendar.DATE);

                String strYear = year+"";
                String strMonth = util.addZero(month);
                String strStartDate = util.addZero(startDate);
                String strEndMonth = util.addZero(endMonth);
                String strEndDate = util.addZero(endDate);

                String data = URLEncoder.encode("companyId", "UTF-8") + "=" + URLEncoder.encode(AppSettings.COMPANY_ID, "UTF-8");
                data += "&" + URLEncoder.encode("startDate", "UTF-8") + "=" + URLEncoder.encode(strYear+"-"+strMonth+"-"+strStartDate, "UTF-8");
                data += "&" + URLEncoder.encode("endDate", "UTF-8") + "=" + URLEncoder.encode(endYear+"-"+strEndMonth+"-"+strEndDate, "UTF-8");

                Log.d("inputData_week", data);
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
                //util.hideProgress();
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
                Log.d("result",result);
                taskList = new JSONArray(result);

                sunday.add(Calendar.DATE, -6);
                setWeek();

            }
            catch (Exception e) {
                util.hideProgress();
                e.printStackTrace();
            }
            util.hideProgress();
        }
    }
}
