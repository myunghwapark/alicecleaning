package com.alice.mhp.alicecleaningmanagement.task;

import android.content.Context;
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
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alice.mhp.alicecleaningmanagement.R;
import com.alice.mhp.common.AppSettings;
import com.alice.mhp.common.Util;
import com.alice.mhp.dao.MonthlyTask;
import com.alice.mhp.dao.Task;

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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MonthFragment extends Fragment {

    Util util;
    private TextView text_month_year;
    private ImageButton btn_arrow_left, btn_arrow_right, btn_map;
    private GridAdapter gridAdapter;
    private ArrayList<MonthlyTask> dayList;
    private GridView grid_view;
    private LinearLayout layout_schedule_list;
    private Calendar mCal;
    JSONArray taskList = null;
    Date date;

    final SimpleDateFormat curYearFormat = new SimpleDateFormat("yyyy", Locale.ENGLISH);
    final SimpleDateFormat curMonthFormat = new SimpleDateFormat("MM", Locale.ENGLISH);
    final SimpleDateFormat curDayFormat = new SimpleDateFormat("dd", Locale.ENGLISH);


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.tab_month, viewGroup, false);
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
            btn_arrow_right.setOnClickListener(changeMonth);

            btn_arrow_left = view.findViewById(R.id.btn_arrow_left);
            btn_arrow_left.setOnClickListener(changeMonth);

            text_month_year = view.findViewById(R.id.text_month_year);
            grid_view = view.findViewById(R.id.grid_view);
            layout_schedule_list = view.findViewById(R.id.layout_schedule_list);

            btn_map = view.findViewById(R.id.btn_map);
            btn_map.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new LoadCoordinate(getContext(), taskList).execute();
                }
            });

            mCal = Calendar.getInstance();
            long now = System.currentTimeMillis();

            date = new Date(now);

            setMonthYear(date);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        util.hideProgress();
    }

    View.OnClickListener changeMonth = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mCal.set(Integer.parseInt(curYearFormat.format(date)), Integer.parseInt(curMonthFormat.format(date)) - 1, 1);
            switch (view.getId()) {
                case R.id.btn_arrow_left:
                    minusMonth();
                    break;

                case R.id.btn_arrow_right:
                    addMonth();
                    break;

                default:
                    break;
            }
        }
    };

    public void addMonth() {
        try {
            mCal.add(Calendar.MONTH, +1);
            date = mCal.getTime();
            setMonthYear(date);
            layout_schedule_list.removeAllViews();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void minusMonth() {
        try {
            mCal.add(Calendar.MONTH, -1);
            date = mCal.getTime();
            setMonthYear(date);
            layout_schedule_list.removeAllViews();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMonthYear(Date date) {

        try {
            //Judge what day of the month is 1: mCal.set(Year,Month,Day)
            mCal.set(Integer.parseInt(curYearFormat.format(date)), Integer.parseInt(curMonthFormat.format(date)) - 1, 1);
            //year and month setting
            text_month_year.setText(curYearFormat.format(date) + "/" + curMonthFormat.format(date));


            LoadMonthlyTask loadMonthlyTask = new LoadMonthlyTask();
            loadMonthlyTask.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCalendar() {

        try {
            mCal.set(Integer.parseInt(curYearFormat.format(date)), Integer.parseInt(curMonthFormat.format(date)) - 1, 1);

            dayList = new ArrayList<MonthlyTask>();
            MonthlyTask monthlyTask = new MonthlyTask();
            monthlyTask.setDate(getResources().getString(R.string.text_sun_day));
            dayList.add(monthlyTask);

            monthlyTask = new MonthlyTask();
            monthlyTask.setDate(getResources().getString(R.string.text_mon_day));
            dayList.add(monthlyTask);

            monthlyTask = new MonthlyTask();
            monthlyTask.setDate(getResources().getString(R.string.text_tue_day));
            dayList.add(monthlyTask);

            monthlyTask = new MonthlyTask();
            monthlyTask.setDate(getResources().getString(R.string.text_wed_day));
            dayList.add(monthlyTask);

            monthlyTask = new MonthlyTask();
            monthlyTask.setDate(getResources().getString(R.string.text_thu_day));
            dayList.add(monthlyTask);

            monthlyTask = new MonthlyTask();
            monthlyTask.setDate(getResources().getString(R.string.text_fri_day));
            dayList.add(monthlyTask);

            monthlyTask = new MonthlyTask();
            monthlyTask.setDate(getResources().getString(R.string.text_sat_day));
            dayList.add(monthlyTask);


            int dayNum = mCal.get(Calendar.DAY_OF_WEEK);

            //Add blank to match first day
            for (int i = 1; i < dayNum; i++) {
                monthlyTask = new MonthlyTask();
                monthlyTask.setDate("");
                dayList.add(monthlyTask);

            }

            setCalendarDate(mCal.get(Calendar.MONTH) + 1);


            gridAdapter = new GridAdapter(getContext(), dayList);
            grid_view.setAdapter(gridAdapter);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }





    private void setCalendarDate(int month) {


        try {
            mCal.set(Calendar.MONTH, month - 1);


            for (int i = 0; i < mCal.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
                MonthlyTask monthlyTask = new MonthlyTask();
                monthlyTask.setTaskArrayList(new ArrayList<Task>());

                String day = "" + (i + 1);
                monthlyTask.setDate(day);
                if(taskList.length() != 0) {
                    for(int row=0; row< taskList.length();row++) {
                        String taskSeqNo = taskList.getJSONObject(row).getString("taskSeqNo");
                        String customerFirstName = taskList.getJSONObject(row).getString("customerFirstName");
                        String cleanDate = taskList.getJSONObject(row).getString("cleanDate");
                        String cleanStartTime = taskList.getJSONObject(row).getString("cleanStartTime");
                        String cleanEndTime = taskList.getJSONObject(row).getString("cleanEndTime");
                        String cleanHours = taskList.getJSONObject(row).getString("cleanHours");
                        String cleaningType = taskList.getJSONObject(row).getString("cleaningType");
                        String staffNumber = taskList.getJSONObject(row).getString("staffNumber");

                        String cleanDay = cleanDate.substring(cleanDate.length()-2, cleanDate.length());

                        if(day.equals(cleanDay)) {
                            Task task = new Task();
                            task.setCustomerFirstName(customerFirstName);
                            task.setCleanDate(cleanDate);
                            task.setTaskSeqNo(taskSeqNo);
                            task.setCleanStartTime(cleanStartTime);
                            task.setCleanEndTime(cleanEndTime);
                            task.setCleanHours(cleanHours);
                            task.setCleaningType(cleaningType);
                            task.setStaffNumber(staffNumber);

                            monthlyTask.addTaskArrayList(task);
                        }
                    }
                }
                dayList.add(monthlyTask);
            }

        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }



    }

    public void showDayList(int position) {

        try {
            ArrayList<Task> taskArrayList = dayList.get(position).getTaskArrayList();
            layout_schedule_list.removeAllViews();

            if(taskArrayList != null && taskArrayList.size() != 0) {
                for(int num=0;num<taskArrayList.size();num++) {
                    Task task = taskArrayList.get(num);

                    View view = getLayoutInflater().inflate(R.layout.month_detail_list, null);
                    TextView text_cleaning_time = view.findViewById(R.id.text_cleaning_time);
                    String startTime = task.getCleanStartTime().substring(0, 5);
                    String endTime = task.getCleanEndTime().substring(0, 5);

                    text_cleaning_time.setText(startTime+"-"+endTime);

                    TextView text_customer_info = view.findViewById(R.id.text_customer_info);
                    text_customer_info.setText(task.getCustomerFirstName());

                    TextView text_staff_number = view.findViewById(R.id.text_staff_number);
                    text_staff_number.setText(getResources().getString(R.string.text_staff)+" "+task.getStaffNumber());

                    view.setTag(task.getTaskSeqNo());
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity().getApplication(), TaskDetailActivity.class);
                            intent.putExtra("taskSeqNo", view.getTag()+"");
                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
                        }
                    });

                    layout_schedule_list.addView(view);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private class GridAdapter extends BaseAdapter {


        private final List<MonthlyTask> list;
        private final LayoutInflater inflater;



        public GridAdapter(Context context, List<MonthlyTask> list) {

            this.list = list;
            this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }



        @Override
        public int getCount() {
            return list.size();
        }



        @Override
        public String getItem(int position) {

            return list.get(position).getDate();

        }



        @Override
        public long getItemId(int position) {

            return position;

        }



        @Override
        public View getView(int position, View convertView, ViewGroup parent) {


            try {
                ViewHolder holder = null;


                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.caleandar_item, parent, false);
                    holder = new ViewHolder();

                    holder.tvItemGridView = convertView.findViewById(R.id.text_calendar_item);
                    holder.imageWorkPoint = convertView.findViewById(R.id.image_point);

                    convertView.setTag(holder);

                } else {
                    holder = (ViewHolder)convertView.getTag();
                }


                if(dayList.get(position).getTaskArrayList() != null && dayList.get(position).getTaskArrayList().size() != 0)
                    holder.imageWorkPoint.setVisibility(View.VISIBLE);



                holder.tvItemGridView.setText("" + getItem(position));
                holder.tvItemGridView.setTag(position);
                holder.tvItemGridView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showDayList(Integer.parseInt(view.getTag()+""));
                    }
                });



                //Change current date's text color and background color
                mCal = Calendar.getInstance();

                //get today
                Integer today = mCal.get(Calendar.DAY_OF_MONTH);

                String sToday = String.valueOf(today);

                if (sToday.equals(getItem(position))) {

                    holder.tvItemGridView.setTextColor(getResources().getColor(R.color.btnBgRedOrange));

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return convertView;

        }


    }



    private class ViewHolder {

        TextView tvItemGridView;
        ImageView imageWorkPoint;

    }





    class LoadMonthlyTask extends AsyncTask<Void, Void, String> {
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

                int year = mCal.get(Calendar.YEAR);
                int month = (mCal.get(Calendar.MONTH) + 1);
                int startDate = mCal.getActualMinimum(Calendar.DATE);
                int endDate = mCal.getActualMaximum(Calendar.DATE);

                String strYear = year+"";
                String strMonth = util.addZero(month);
                String strStartDate = util.addZero(startDate);
                String strEndDate = util.addZero(endDate);

                String data = URLEncoder.encode("companyId", "UTF-8") + "=" + URLEncoder.encode(AppSettings.COMPANY_ID, "UTF-8");
                data += "&" + URLEncoder.encode("startDate", "UTF-8") + "=" + URLEncoder.encode(strYear+"-"+strMonth+"-"+strStartDate, "UTF-8");
                data += "&" + URLEncoder.encode("endDate", "UTF-8") + "=" + URLEncoder.encode(strYear+"-"+strMonth+"-"+strEndDate, "UTF-8");

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
            } catch(NoRouteToHostException e) {
                //util.alert(getResources().getString(R.string.text_alert_title), getResources().getString(R.string.text_no_route_host),getResources().getString(R.string.text_ok));
                e.printStackTrace();
            } catch(Exception e) {
                e.printStackTrace();
            }
            return builder.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                Log.d("result_monthly",result);
                taskList = new JSONArray(result);

                setCalendar();

            }
            catch (Exception e) {
                util.hideProgress();
                e.printStackTrace();
            }
            util.hideProgress();
        }
    }
}
