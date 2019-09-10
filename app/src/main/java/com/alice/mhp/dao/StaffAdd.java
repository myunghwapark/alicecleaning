package com.alice.mhp.dao;

import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class StaffAdd {
    String staffSeqNo;
    TextView text_staff_name;
    TextView text_working_time_from;
    TextView text_working_time_to;
    EditText edit_wages;
    ImageButton btn_task_staff_delete;

    public String getStaffSeqNo() {
        return staffSeqNo;
    }

    public void setStaffSeqNo(String staffSeqNo) {
        this.staffSeqNo = staffSeqNo;
    }

    public TextView getText_staff_name() {
        return text_staff_name;
    }

    public void setText_staff_name(TextView text_staff_name) {
        this.text_staff_name = text_staff_name;
    }

    public TextView getText_working_time_from() {
        return text_working_time_from;
    }

    public void setText_working_time_from(TextView text_working_time_from) {
        this.text_working_time_from = text_working_time_from;
    }

    public TextView getText_working_time_to() {
        return text_working_time_to;
    }

    public void setText_working_time_to(TextView text_working_time_to) {
        this.text_working_time_to = text_working_time_to;
    }

    public EditText getEdit_wages() {
        return edit_wages;
    }

    public void setEdit_wages(EditText edit_wages) {
        this.edit_wages = edit_wages;
    }

    public ImageButton getBtn_task_staff_delete() {
        return btn_task_staff_delete;
    }

    public void setBtn_task_staff_delete(ImageButton btn_task_staff_delete) {
        this.btn_task_staff_delete = btn_task_staff_delete;
    }
}
