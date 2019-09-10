package com.alice.mhp.common;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;


import com.alice.mhp.alicecleaningmanagement.common.CommonActivity;

import java.util.ArrayList;


public class PermissionController extends CommonActivity {

    public static boolean permissionCheck;
    int targetSdkVersion;

    Context context;
    Activity activity;

    public PermissionController(Context cont, Activity activity, ArrayList<String> permissionList) {
        context = cont;
        this.activity = activity;

        try {
            final PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            targetSdkVersion = info.applicationInfo.targetSdkVersion;

            permissionCheck = grantExternalStoragePermission(permissionList);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }



    public boolean grantExternalStoragePermission(ArrayList<String> arrayPermission) {
        // in case of more than version 23
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            ArrayList<String> getPermissionList = new ArrayList<String> ();
            for(int permissionType=0; permissionType<arrayPermission.size(); permissionType++) {
                if(context.checkSelfPermission(arrayPermission.get(permissionType)) != PackageManager.PERMISSION_GRANTED) {
                    getPermissionList.add(arrayPermission.get(permissionType));
                }
            }
            if(getPermissionList.size() != 0) {
                String permissions [] = new String[getPermissionList.size()];
                for(int permission=0; permission<getPermissionList.size(); permission++) {
                    permissions[permission] = getPermissionList.get(permission);
                }
                ActivityCompat.requestPermissions(activity, permissions, 1);
                return false;
            }
            else {
                return true;
            }
        }
        else {
            return true;
        }

    }





}
