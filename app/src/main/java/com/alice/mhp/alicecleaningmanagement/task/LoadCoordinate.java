package com.alice.mhp.alicecleaningmanagement.task;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.os.AsyncTask;

import com.alice.mhp.alicecleaningmanagement.R;
import com.alice.mhp.common.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class LoadCoordinate extends AsyncTask<Void, Void, String> {
    Util util;
    Context mContext;
    JSONArray taskList = null;

    public LoadCoordinate(Context context, JSONArray taskList) {
        this.mContext = context;
        this.util = new Util(context);
        this.taskList = taskList;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        util.showProgress(mContext.getResources().getString(R.string.text_loading));
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {

            for (int row = 0; row < taskList.length(); row++) {
                JSONObject object = taskList.getJSONObject(row);
                if(object != null) {
                    List<Address> CoordinateList = util.getCoordinates(object.getString("cleanAddress"));
                    Address address = CoordinateList.get(0);
                    double latitude = address.getLatitude();
                    double longitude = address.getLongitude();

                    object.put("latitude", ""+latitude);
                    object.put("longitude", ""+longitude);

                }
            }

        }
        catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return taskList.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        try {

            Intent intent = new Intent((Activity)mContext, TaskMapActivity.class);
            intent.putExtra("taskList", result);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            mContext.startActivity(intent);

        }
        catch (Exception e) {
            util.hideProgress();
            e.printStackTrace();
        }
        util.hideProgress();
    }
}

