package com.alice.mhp.alicecleaningmanagement.task;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.design.widget.TabLayout;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.alice.mhp.adapter.TabsAdapter;
import com.alice.mhp.alicecleaningmanagement.common.CommonActivity;
import com.alice.mhp.alicecleaningmanagement.R;

public class TaskListActivity extends CommonActivity {

    EditText edit_search_task;
    ImageButton btn_search_task, btn_new_task;
    int tabPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle(getResources().getString(R.string.text_task));

            edit_search_task = findViewById(R.id.edit_search_task);
            btn_search_task = findViewById(R.id.btn_search_task);

            btn_new_task = findViewById(R.id.btn_new_task);
            btn_new_task.setOnClickListener(clickBtn);

            TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
            tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.text_month)));
            tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.text_week)));
            tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.text_day)));
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

            final ViewPager viewPager =(ViewPager)findViewById(R.id.view_pager);
            TabsAdapter tabsAdapter = new TabsAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
            tabsAdapter.addFragment(new MonthFragment(), getResources().getString(R.string.text_month));
            tabsAdapter.addFragment(new WeekFragment(), getResources().getString(R.string.text_week));
            tabsAdapter.addFragment(new DayFragment(), getResources().getString(R.string.text_day));

            viewPager.setAdapter(tabsAdapter);
            viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    tabPosition = tab.getPosition();
                    viewPager.setCurrentItem(tab.getPosition());
                }
                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }
                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    View.OnClickListener clickBtn = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent;
            switch (view.getId()) {
                case R.id.btn_new_task:
                    intent = new Intent(TaskListActivity.this, TaskNewActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    break;

                default:
                    break;
            }
        }
    };
}
