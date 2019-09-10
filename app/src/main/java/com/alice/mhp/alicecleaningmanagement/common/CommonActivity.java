package com.alice.mhp.alicecleaningmanagement.common;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.alice.mhp.alicecleaningmanagement.R;
import com.alice.mhp.alicecleaningmanagement.customer.CustomerListActivity;
import com.alice.mhp.alicecleaningmanagement.staff.StaffListActivity;
import com.alice.mhp.alicecleaningmanagement.task.TaskListActivity;

public class CommonActivity extends AppCompatActivity {

    Menu mMenu;
    ProgressDialog asyncDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_menu, menu);
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;

        switch (item.getItemId()) {
            case R.id.action_task_list:
                intent = new Intent(CommonActivity.this, TaskListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                return true;
            case R.id.action_customer_list:
                intent = new Intent(CommonActivity.this, CustomerListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                return true;
            case R.id.action_staff_list:
                intent = new Intent(CommonActivity.this, StaffListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                return true;
            case R.id.action_setting:
                intent = new Intent(CommonActivity.this, SettingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                return true;
            case R.id.action_youtube:
                intent = new Intent(CommonActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (asyncDialog != null) {
            asyncDialog.dismiss();
            asyncDialog = null;
        }
    }

    public void showProgress(String msg) {

        if(asyncDialog == null) {
            asyncDialog = new ProgressDialog(CommonActivity.this);
        }
        asyncDialog.setMessage(msg);
        asyncDialog.show();
    }

    public void hideProgress(){
        if( asyncDialog != null && asyncDialog.isShowing() ) {
            asyncDialog.dismiss();
        }
    }

    // When back button click
    @Override
    public boolean onSupportNavigateUp()
    {
        this.finish();
        return super.onSupportNavigateUp();

    }

}
