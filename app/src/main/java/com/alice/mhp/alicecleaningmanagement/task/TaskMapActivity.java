package com.alice.mhp.alicecleaningmanagement.task;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.alice.mhp.alicecleaningmanagement.R;
import com.alice.mhp.alicecleaningmanagement.customer.CustomerListActivity;
import com.alice.mhp.alicecleaningmanagement.staff.StaffListActivity;
import com.alice.mhp.common.PermissionController;
import com.alice.mhp.common.Util;
import com.alice.mhp.dao.MarkerItem;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class TaskMapActivity extends AppCompatActivity implements OnMapReadyCallback {


    ImageButton btn_back, btn_task, btn_customer, btn_staff;
    TextView text_map_title;
    ArrayList<MarkerOptions> mapArray;
    MapFragment mapFragment;
    PermissionController permissionController;
    GoogleMap mMap;
    int DEFAULT_ZOOM = 12;
    Util util;
    String prevPage, taskSeqNo;
    JSONArray taskList = null;
    Marker marker;
    boolean mMoveMapByAPI = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_map);

        try {
            getSupportActionBar().hide();

            String taskStr = getIntent().getExtras().getString("taskList");

            taskList = new JSONArray(taskStr);

            ArrayList<String> permissionList = new ArrayList<String>();
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
            permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            permissionList.add(Manifest.permission.INTERNET);

            permissionController = new PermissionController(this, TaskMapActivity.this, permissionList);
            util = new Util(this);

            btn_back = findViewById(R.id.btn_back);
            btn_back.setOnClickListener(btnMenuClick);
            btn_task = findViewById(R.id.btn_task);
            btn_task.setOnClickListener(btnMenuClick);
            btn_customer = findViewById(R.id.btn_customer);
            btn_customer.setOnClickListener(btnMenuClick);
            btn_staff = findViewById(R.id.btn_staff);
            btn_staff.setOnClickListener(btnMenuClick);
            text_map_title = findViewById(R.id.text_map_title);

            prevPage = getIntent().getExtras().getString("prevPage");
            taskSeqNo = getIntent().getExtras().getString("taskSeqNo");


            FragmentManager fragmentManager = getFragmentManager();
            mapFragment = (MapFragment)fragmentManager.findFragmentById(R.id.travel_map);

            if(permissionController.permissionCheck) {
                mapFragment.getMapAsync(this);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        util.hideProgress();
    }

    @Override
    public void onMapReady(GoogleMap map) {

        try {
            mMap = map;
            mMap.setOnMarkerClickListener(onMarkerClick);
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

                @Override
                public void onInfoWindowClick(Marker marker) {
                    Intent intent = new Intent(TaskMapActivity.this, TaskDetailActivity.class);
                    intent.putExtra("taskSeqNo", ""+marker.getTag());
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);

                }
            });

            mapArray = new ArrayList<>();
            if(taskList.length() != 0) {
                for (int row = 0; row < taskList.length(); row++) {
                    JSONObject object = taskList.getJSONObject(row);
                    if(object != null) {
                        double latitude = Double.parseDouble(object.getString("latitude"));
                        double longitude = Double.parseDouble(object.getString("longitude"));

                        MarkerItem markerItem = new MarkerItem();
                        markerItem.setLatitude(latitude);
                        markerItem.setLongitude(longitude);
                        markerItem.setCustomerFirstName(taskList.getJSONObject(row).getString("customerFirstName"));
                        Log.d("map_taskSeqNo==",taskList.getJSONObject(row).getString("taskSeqNo"));
                        markerItem.setTaskSeqNo(taskList.getJSONObject(row).getString("taskSeqNo"));
                        addMarker(markerItem, false);
                    }
                }
                util.hideProgress();
            }
            else {
                mMoveMapByAPI = true;
                setDefaultLocation();

            }

            //text_map_title.setText(title);
            mMap.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private Marker addMarker(MarkerItem markerItem, boolean isSelectedMarker) {

        try {
            View marker_layout = LayoutInflater.from(this).inflate(R.layout.marker_layout, null);
            ImageView image_photo = marker_layout.findViewById(R.id.image_marker);

            LatLng position = new LatLng(markerItem.getLatitude(), markerItem.getLongitude());


            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.title(markerItem.getCustomerFirstName());
            markerOptions.snippet(markerItem.getAddress());
            markerOptions.position(position);
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, marker_layout)));


            mMap.moveCamera(CameraUpdateFactory.newLatLng(position));

            marker = mMap.addMarker(markerOptions);
            marker.setTag(markerItem.getTaskSeqNo());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return marker;

    }

    private Bitmap createDrawableFromView(Context context, View view) {

        Bitmap bitmap = null;
        try {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
            view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
            view.buildDrawingCache();
            bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(bitmap);
            view.draw(canvas);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public void setDefaultLocation() {

        try {
            if(mMoveMapByAPI) {
                //Default location, Auckland
                LatLng DEFAULT_LOCATION = new LatLng(-36.848461, 174.763336);
                String markerTitle = "";
                String markerSnippet = "";

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(DEFAULT_LOCATION);
                markerOptions.title(markerTitle);
                markerOptions.snippet(markerSnippet);
                markerOptions.draggable(true);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                marker = mMap.addMarker(markerOptions);

                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15);
                mMap.moveCamera(cameraUpdate);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public GoogleMap.OnMarkerClickListener onMarkerClick = new GoogleMap.OnMarkerClickListener() {

        @Override
        public boolean onMarkerClick(Marker marker) {
            return false;
        }


    };



    public View.OnClickListener btnMenuClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            Intent intent;

            switch (view.getId()) {

                case R.id.btn_back:
                    finish();
                    break;

                case R.id.btn_task:

                    intent = new Intent(TaskMapActivity.this, TaskListActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                    break;

                case R.id.btn_customer:

                    intent = new Intent(TaskMapActivity.this, CustomerListActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                    break;

                case R.id.btn_staff:

                    intent = new Intent(TaskMapActivity.this, StaffListActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                    break;

                default:
                    break;

            }
        }
    };


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {

        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                boolean permissionYn = false;
                for(int result=0; result<grantResults.length; result++) {
                    if(grantResults[result]== PackageManager.PERMISSION_GRANTED){
                        Log.v(TAG,"Permission: "+permissions[result]+ "was "+grantResults[result]);
                        permissionYn = true;
                    }
                    else {
                        permissionYn = false;
                    }
                }

                if(permissionYn) {
                    permissionController.permissionCheck = true;
                    mapFragment.getMapAsync(this);
                }
                else {
                    permissionController.permissionCheck = false;
                }

            }
            else {
                permissionController.permissionCheck = true;
                mapFragment.getMapAsync(this);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
