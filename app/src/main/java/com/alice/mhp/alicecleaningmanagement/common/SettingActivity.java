package com.alice.mhp.alicecleaningmanagement.common;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TableRow;
import android.widget.TextView;

import com.alice.mhp.alicecleaningmanagement.R;
import com.alice.mhp.common.AppSettings;
import com.alice.mhp.common.PermissionController;

import java.util.ArrayList;

public class SettingActivity extends CommonActivity {

    TableRow btn_policy, btn_facebook, layout_pay_per_hour;
    TextView text_pay_per_hour, text_email;
    PermissionController permissionController;
    int permissionTarget = 0; // 0:internet, 1:phone call
    private PopupWindow mPopupWindow ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle(getResources().getString(R.string.text_setting));

            text_pay_per_hour = findViewById(R.id.text_pay_per_hour);

            layout_pay_per_hour = findViewById(R.id.layout_pay_per_hour);
            layout_pay_per_hour.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPayPerHourDialog();
                }
            });


            btn_policy = findViewById(R.id.btn_policy);
            btn_policy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkInternetPermission("privacyPolicy");
                }
            });

            text_email = findViewById(R.id.text_email);
            text_email.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendEmail();
                }
            });

            SharedPreferences dataOfPayPerHour = getSharedPreferences("setting", 0);
            if(dataOfPayPerHour != null) {
                String payPerHour = dataOfPayPerHour.getString("payPerHour","");
                if(payPerHour != null && !payPerHour.equals("")) {
                    text_pay_per_hour.setText("$"+payPerHour);
                }
            }

            btn_facebook = findViewById(R.id.btn_facebook);
            btn_facebook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkInternetPermission("facebook");
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showPayPerHourDialog() {

        try {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(getResources().getString(R.string.text_pay_per_hour));

            final EditText edit_pay = new EditText(this);
            edit_pay.setInputType(InputType.TYPE_CLASS_NUMBER);
            dialog.setView(edit_pay);

            dialog.setPositiveButton(getResources().getString(R.string.text_save), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    text_pay_per_hour.setText("$"+edit_pay.getText().toString());

                    SharedPreferences dataOfPayPerHour = getSharedPreferences("setting", 0);
                    SharedPreferences.Editor editor = dataOfPayPerHour.edit();
                    String str = edit_pay.getText().toString(); // value of user input
                    editor.putString("payPerHour", str);
                    editor.commit();

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

    public void checkInternetPermission(String type) {

        try {
            ArrayList<String> permissionList = new ArrayList<String>();
            permissionList.add(Manifest.permission.INTERNET);
            permissionTarget = 0;
            permissionController = new PermissionController(this, SettingActivity.this, permissionList);

            if(permissionController.permissionCheck) {
                showWebView(type);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showWebView(String type) {

        try {
            View popupView = getLayoutInflater().inflate(R.layout.webview, null);
            mPopupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

            // when click dialog outside, popup window finish
            mPopupWindow.setFocusable(true);
            mPopupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

            Button btnClose = popupView.findViewById(R.id.btn_close);
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPopupWindow.dismiss();
                }
            });

            TextView webViewTitle = popupView.findViewById(R.id.text_web_view_title);
            WebView webView = popupView.findViewById(R.id.webview_policy);
            webView.setWebViewClient(new WebViewClient());
            WebSettings mWebSettings = webView.getSettings();
            mWebSettings.setJavaScriptEnabled(true);

            String url = "";
            if(type.equals("privacyPolicy")) {
                url = AppSettings.privacyPolicyUrl;
                webViewTitle.setText(getResources().getString(R.string.text_privacy_policy));
            }
            else {
                url = AppSettings.facebookUrl;
                webViewTitle.setText(getResources().getString(R.string.text_facebook));
            }
            webView.loadUrl(url);
        }
        catch (SecurityException e) {
            e.printStackTrace();
        }

    }


    public void sendEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        try {
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{text_email.getText().toString()});

            emailIntent.setType("text/html");
            emailIntent.setPackage("com.google.android.gm");
            if(emailIntent.resolveActivity(getPackageManager())!=null)
                startActivity(emailIntent);

            startActivity(emailIntent);
        } catch (Exception e) {
            e.printStackTrace();

            emailIntent.setType("text/html");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{text_email.getText().toString()});

            startActivity(Intent.createChooser(emailIntent, "Send Email"));
        }
    }
}
