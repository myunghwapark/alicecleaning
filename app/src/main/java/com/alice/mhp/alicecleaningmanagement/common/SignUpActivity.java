package com.alice.mhp.alicecleaningmanagement.common;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
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

public class SignUpActivity extends AppCompatActivity {

    EditText edit_id, edit_password, edit_password_confirm, edit_company_name, edit_owner_first_name, edit_owner_last_name,
            edit_email_address, edit_mobile_number;
    CheckBox checkbox_agree;
    Button btn_privacy, btn_cancel, btn_register, btn_check_id;
    Util util;
    boolean checkCompanyId = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle(getResources().getString(R.string.text_sign_up));
            actionBar.setDisplayHomeAsUpEnabled(true);

            util = new Util(this);

            edit_id = findViewById(R.id.edit_id);
            edit_password = findViewById(R.id.edit_password);
            edit_password_confirm = findViewById(R.id.edit_password_confirm);
            edit_company_name = findViewById(R.id.edit_company_name);
            edit_owner_first_name = findViewById(R.id.edit_owner_first_name);
            edit_owner_last_name = findViewById(R.id.edit_owner_last_name);
            edit_email_address = findViewById(R.id.edit_email_address);
            edit_mobile_number = findViewById(R.id.edit_mobile_number);
            btn_check_id = findViewById(R.id.btn_check_id);
            btn_check_id.setOnClickListener(btnRegister);
            //checkbox_agree = findViewById(R.id.checkbox_agree);
            //btn_privacy = findViewById(R.id.btn_privacy);
            //btn_privacy.setOnClickListener(btnLogin);

            btn_cancel = findViewById(R.id.btn_cancel);
            btn_cancel.setOnClickListener(btnRegister);

            btn_register = findViewById(R.id.btn_register);
            btn_register.setOnClickListener(btnRegister);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean checkForm() {

        boolean result = false;
        try {
            if(edit_id.getText().toString().length() == 0) {
                showToast(getResources().getString(R.string.text_please_enter)+" "+getResources().getString(R.string.text_id));
                result = false;
            }
            else if(!checkCompanyId) {
                showToast(getResources().getString(R.string.text_notice_check_id));
                result = false;
            }
            else if(edit_password.getText().toString().length() == 0) {
                showToast(getResources().getString(R.string.text_please_enter)+" "+getResources().getString(R.string.text_password));
                result = false;
            }
            else if(edit_password_confirm.getText().toString().length() == 0) {
                showToast(getResources().getString(R.string.text_please_enter)+" "+getResources().getString(R.string.text_password_confirm));
                result = false;
            }
            else if(!edit_password.getText().toString().equals(edit_password_confirm.getText().toString())) {
                showToast(getResources().getString(R.string.text_different_password));
                result = false;
            }
            else if(edit_company_name.getText().toString().length() == 0) {
                showToast(getResources().getString(R.string.text_please_enter)+" "+getResources().getString(R.string.text_company_name));
                result = false;
            }
            else if(edit_owner_first_name.getText().toString().length() == 0) {
                showToast(getResources().getString(R.string.text_please_enter)+" "+getResources().getString(R.string.text_owner_first_name));
                result = false;
            }
            else if(edit_owner_last_name.getText().toString().length() == 0) {
                showToast(getResources().getString(R.string.text_please_enter)+" "+getResources().getString(R.string.text_owner_last_name));
                result = false;
            }
            else if(edit_email_address.getText().toString().length() == 0) {
                showToast(getResources().getString(R.string.text_please_enter)+" "+getResources().getString(R.string.text_email_address));
                result = false;
            }
            else if(edit_mobile_number.getText().toString().length() == 0) {
                showToast(getResources().getString(R.string.text_please_enter)+" "+getResources().getString(R.string.text_mobile_number));
                result = false;
            }
            else {
                result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    View.OnClickListener btnRegister = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent;
            switch (v.getId()) {
                case R.id.btn_register:
                    if(checkForm()) {
                        new SaveSignUpData().execute();
                    }
                    break;

                case R.id.btn_cancel:
                    intent = new Intent(SignUpActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    break;

                case R.id.btn_check_id:
                    if(edit_id.getText().toString().length() != 0) {
                        new CheckCompanyId().execute();
                    }
                    else {
                        showToast(getResources().getString(R.string.text_please_enter)+" "+getResources().getString(R.string.text_id));
                    }
                    break;



                default:
                    break;
            }

        }
    };

    // When back button click
    @Override
    public boolean onSupportNavigateUp()
    {
        this.finish();
        return super.onSupportNavigateUp();

    }

    @Override
    protected void onStop() {
        super.onStop();
        util.hideProgress();
    }



    class SaveSignUpData extends AsyncTask<Void, Void, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            util.showProgress(getResources().getString(R.string.text_loading));
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            StringBuilder builder = new StringBuilder();

            try {
                URL url = new URL(AppSettings.URL_ADDRESS + AppSettings.URL_INSERT_COMPANY_REGISTER);
                URLConnection urlConnection = url.openConnection();

                String data = URLEncoder.encode("companyId", "UTF-8") + "=" + URLEncoder.encode(edit_id.getText().toString(), "UTF-8");
                data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(edit_password.getText().toString(), "UTF-8");
                data += "&" + URLEncoder.encode("companyName", "UTF-8") + "=" + URLEncoder.encode(edit_company_name.getText().toString(), "UTF-8");
                data += "&" + URLEncoder.encode("firstName", "UTF-8") + "=" + URLEncoder.encode(edit_owner_first_name.getText().toString(), "UTF-8");
                data += "&" + URLEncoder.encode("lastName", "UTF-8") + "=" + URLEncoder.encode(edit_owner_last_name.getText().toString(), "UTF-8");
                data += "&" + URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(edit_email_address.getText().toString(), "UTF-8");
                data += "&" + URLEncoder.encode("mobile", "UTF-8") + "=" + URLEncoder.encode(edit_mobile_number.getText().toString(), "UTF-8");

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
            return Integer.parseInt(builder.toString());
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            try {

                if(result == 1) {
                    alert(getResources().getString(R.string.text_alert_title), getResources().getString(R.string.text_success_register), getResources().getString(R.string.text_ok));

                }
                else {
                    alert(getResources().getString(R.string.text_alert_title), getResources().getString(R.string.text_fail_register), getResources().getString(R.string.text_ok));
                }

            }
            catch (Exception e) {
                util.hideProgress();
                e.printStackTrace();
            }
            util.hideProgress();

        }
    }

    class CheckCompanyId extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            util.showProgress(getResources().getString(R.string.text_loading));
        }

        @Override
        protected String doInBackground(Void... voids) {
            StringBuilder builder = new StringBuilder();

            try {
                URL url = new URL(AppSettings.URL_ADDRESS + AppSettings.URL_CHECK_COMPANY_ID);
                URLConnection urlConnection = url.openConnection();

                String data = URLEncoder.encode("companyId", "UTF-8") + "=" + URLEncoder.encode(edit_id.getText().toString(), "UTF-8");

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
            Log.d("result==",result);
            String count = "";

            util.hideProgress();
            try {
                JSONObject object = new JSONObject(result);
                count = object.getString("companyIdCount");
                if(count.equals("0")) {
                    checkCompanyId = true;
                    util.alert(getResources().getString(R.string.text_alert_title), getResources().getString(R.string.text_available_id), getResources().getString(R.string.text_ok));

                }
                else {
                    checkCompanyId = false;
                    util.alert(getResources().getString(R.string.text_alert_title), getResources().getString(R.string.text_unavailable_id), getResources().getString(R.string.text_ok));
                    edit_id.setText("");
                }


            }
            catch (Exception e) {
                util.hideProgress();
                e.printStackTrace();
            }


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
                                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                }
                            });


            alertDialogBuilder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
