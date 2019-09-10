package com.alice.mhp.alicecleaningmanagement.common;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

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

public class LoginActivity extends AppCompatActivity {

    Util util;
    boolean isNetworkConnected;
    EditText edit_id, edit_password;
    Button btn_login, btn_sign_up;
    CheckBox checkbox_remember;
    SharedPreferences useInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle(getResources().getString(R.string.text_login));

            util = new Util(this);
            isNetworkConnected = util.networkStatus();

            edit_id = findViewById(R.id.edit_id);
            edit_password = findViewById(R.id.edit_password);
            btn_login = findViewById(R.id.btn_login);
            btn_login.setOnClickListener(btnClick);

            checkbox_remember = findViewById(R.id.checkbox_remember);

            btn_sign_up = findViewById(R.id.btn_sign_up);
            btn_sign_up.setOnClickListener(btnClick);

            useInfo = getSharedPreferences("useInfo", 0);
            if(useInfo != null) {
                String companyId = useInfo.getString("companyId","");
                if(companyId != null && !companyId.equals("")) {
                    edit_id.setText(companyId);
                    checkbox_remember.setChecked(true);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        util.hideProgress();
    }

    public boolean checkForm() {

        boolean result = false;
        try {
            if(edit_id.getText().toString().length() == 0) {
                Toast.makeText(this, getResources().getString(R.string.text_please_enter)+" "+getResources().getString(R.string.text_id), Toast.LENGTH_SHORT).show();
                result = false;
            }
            else if(edit_password.getText().toString().length() == 0) {
                Toast.makeText(this, getResources().getString(R.string.text_please_enter)+" "+getResources().getString(R.string.text_password), Toast.LENGTH_SHORT).show();
                result = false;
            }
            else {
                result = true;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    View.OnClickListener btnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent;
            switch (v.getId()) {
                case R.id.btn_login:
                    if(checkForm()) {
                        SharedPreferences.Editor editor = useInfo.edit();
                        if(checkbox_remember.isChecked()) {
                            String str = edit_id.getText().toString(); // value of user input
                            editor.putString("companyId", str);
                            editor.commit();
                        }
                        else {
                            editor.putString("companyId", "");
                            editor.commit();
                        }
                        new LoginTask().execute();
                    }
                    break;

                case R.id.btn_sign_up:
                    intent = new Intent(LoginActivity.this, SignUpActivity.class);
                    startActivity(intent);
                    break;
                default:
                    break;
            }

        }
    };

    class LoginTask extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            util.showProgress(getResources().getString(R.string.text_loading));
        }

        @Override
        protected String doInBackground(Void... voids) {
            StringBuilder builder = new StringBuilder();

            try {
                URL url = new URL(AppSettings.URL_ADDRESS + AppSettings.URL_COMPANY_LOGIN);

                URLConnection urlConnection = url.openConnection();

                String data = URLEncoder.encode("userId", "UTF-8") + "=" + URLEncoder.encode(edit_id.getText().toString(), "UTF-8");
                data += "&" + URLEncoder.encode("userPassword", "UTF-8") + "=" + URLEncoder.encode(edit_password.getText().toString(), "UTF-8");


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

                if(resultObject.getString("result").equals("success") && resultObject.getString("statusCode").equals("G002_002")) {
                    AppSettings.COMPANY_ID = edit_id.getText().toString();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else if(resultObject.getString("result").equals("success") && resultObject.getString("statusCode").equals("G002_001")) {
                    util.alert(getResources().getString(R.string.text_alert_title), getResources().getString(R.string.text_login_wait_approval),getResources().getString(R.string.text_ok));
                }
                else {
                    util.alert(getResources().getString(R.string.text_alert_title), getResources().getString(R.string.text_login_fail),getResources().getString(R.string.text_ok));
                }

            }
            catch (Exception e) {
                util.hideProgress();
                e.printStackTrace();
            }
        }
    }

}
