package com.alice.mhp.alicecleaningmanagement.staff;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.alice.mhp.alicecleaningmanagement.R;
import com.alice.mhp.common.AppSettings;
import com.alice.mhp.common.Util;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class PayComplete extends AsyncTask<Void, Void, String> {

    Util util;
    Context mContext;
    String workHistorySeqNo;
    String payCompleteYn;

    public PayComplete(Context context, String workHistorySeqNo, String payCompleteYn) {
        this.mContext = context;
        this.util = new Util(context);
        this.workHistorySeqNo = workHistorySeqNo;
        this.payCompleteYn = payCompleteYn;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        util.showProgress(mContext.getResources().getString(R.string.text_loading));
    }

    @Override
    protected String doInBackground(Void... voids) {
        StringBuilder builder = new StringBuilder();

        try {
            URL url = new URL(AppSettings.URL_ADDRESS + AppSettings.URL_UPDATE_STAFF_PAID);

            URLConnection urlConnection = url.openConnection();

            String data = URLEncoder.encode("workHistorySeqNo", "UTF-8") + "=" + URLEncoder.encode(workHistorySeqNo, "UTF-8");
            data += "&" + URLEncoder.encode("payCompleteYn", "UTF-8") + "=" + URLEncoder.encode(payCompleteYn, "UTF-8");


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
            JSONObject resultObject = new JSONObject(result);

            if(resultObject.getString("result").equals("success")) {
                util.alert(mContext.getResources().getString(R.string.text_alert_title), mContext.getResources().getString(R.string.text_success_save),mContext.getResources().getString(R.string.text_ok));
            }
            else {
                util.alert(mContext.getResources().getString(R.string.text_alert_title), mContext.getResources().getString(R.string.text_fail),mContext.getResources().getString(R.string.text_ok));
            }

        }
        catch (Exception e) {
            util.hideProgress();
            e.printStackTrace();
        }
    }
}
