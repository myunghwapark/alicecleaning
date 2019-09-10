package com.alice.mhp.common;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.alice.mhp.alicecleaningmanagement.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Util {
    Context context;
    InputMethodManager imManager;
    public ProgressDialog asyncDialog;
    PermissionController permissionController;
    int permissionTarget = 0; // 0:internet, 1:phone call
    PopupWindow mPopupWindow;

    public Util(Context cont) {
        context = cont;
        imManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    }
    public boolean networkStatus() {
        boolean result = false;
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if (networkInfo == null || !networkInfo.isConnected()) {
                Toast.makeText(context, context.getResources().getString(R.string.text_no_internet), Toast.LENGTH_SHORT).show();
                result = false;
            } else {
                result = true;
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public void alert(String title, String message, String btnText) {

        try {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    context);

            alertDialogBuilder.setTitle(title)
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton(btnText,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });


            alertDialogBuilder.show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void hideKeyboard(IBinder windowToken) {

        try {
            // hide keyboard
            imManager.hideSoftInputFromWindow(windowToken, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Calendar getCurSunday(){

        Calendar calendar = Calendar.getInstance();;
        try {
            //java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy.MM.dd");

            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return calendar;
    }

    public static String getMonth(int month){
        String[] monthNames = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        return monthNames[month];
    }

    public void showProgress(String msg) {

        try {
            if (asyncDialog == null) {
                asyncDialog = new ProgressDialog(context);
            }
            if (!asyncDialog.isShowing()) {
                asyncDialog.setMessage(msg);
                asyncDialog.show();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void hideProgress(){
        try {
            if (asyncDialog != null && asyncDialog.isShowing()) {
                asyncDialog.dismiss();
                asyncDialog = null;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String addZero(int num) {
        String strNum = num+"";
        if(num < 10) {
            strNum = "0"+strNum;
        }
        return strNum;
    }

    public String timeFormat(int time) {
        String timeStr = "";
        try {
            if (time < 12) {
                timeStr = time + " AM";
            } else if (time == 12) {
                timeStr = time + " PM";
            } else {
                timeStr = (time - 12) + " PM";
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return timeStr;
    }

    public List<Address> getCoordinates(String address) {
        List<Address> list = null;
        final Geocoder geocoder = new Geocoder(context);

        try {
            list = geocoder.getFromLocationName(
                    address,
                    10);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }

    public void sendEmail(String email) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        try {
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});

            emailIntent.setType("text/html");
            emailIntent.setPackage("com.google.android.gm");
            if(emailIntent.resolveActivity(context.getPackageManager())!=null)
                context.startActivity(emailIntent);

            context.startActivity(emailIntent);
        } catch (Exception e) {
            e.printStackTrace();

            emailIntent.setType("text/html");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});

            context.startActivity(Intent.createChooser(emailIntent, "Send Email"));
        }
    }

    public void call(String phoneNumber) {

        ArrayList<String> permissionList = new ArrayList<String>();
        permissionList.add(Manifest.permission.CALL_PHONE);
        permissionTarget = 1;
        permissionController = new PermissionController(context, (Activity)context, permissionList);

        if(permissionController.permissionCheck) {
            try {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
                context.startActivity(intent);
            }
            catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    public void checkSmsPermission(String phoneNumber) {

        try {
            ArrayList<String> permissionList = new ArrayList<String>();
            permissionList.add(Manifest.permission.SEND_SMS);
            permissionTarget = 0;
            permissionController = new PermissionController(context, (Activity)context, permissionList);

            if(permissionController.permissionCheck) {
                showSmsDialog(phoneNumber);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void showSmsDialog(final String phoneNumber) {

        try {

            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View popupView = inflater.inflate(R.layout.layout_sms, null);
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

            final EditText edit_receiving_number = popupView.findViewById(R.id.edit_receiving_number);
            edit_receiving_number.setInputType(InputType.TYPE_CLASS_PHONE);
            edit_receiving_number.setText(phoneNumber);

            final EditText edit_message = popupView.findViewById(R.id.edit_message);

            Button btn_cancel = popupView.findViewById(R.id.btn_cancel);
            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPopupWindow.dismiss();
                }
            });

            Button btn_send = popupView.findViewById(R.id.btn_send);
            btn_send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(edit_receiving_number.getText().toString() != null && !edit_receiving_number.getText().toString().equals("")) {
                        sendSms(edit_receiving_number.getText().toString(), edit_message.getText().toString());
                        mPopupWindow.dismiss();
                    }
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendSms(String phoneNo, String smsContent) {
        try {
            //전송
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, smsContent, null, null);
            Toast.makeText(context, "Sent SMS successfully.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(context, "Sending SMS failed, please try again later.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

}
