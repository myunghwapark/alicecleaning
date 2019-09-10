package com.alice.mhp.dao;

public class StaffWorkHistory {
    String workHistorySeqNo;
    String taskSeqNo;
    String workDate;
    String workStartTime;
    String workEndTime;
    String payAmount;
    String payComplete;

    public String getWorkHistorySeqNo() {
        return workHistorySeqNo;
    }

    public void setWorkHistorySeqNo(String workHistorySeqNo) {
        this.workHistorySeqNo = workHistorySeqNo;
    }

    public String getTaskSeqNo() {
        return taskSeqNo;
    }

    public void setTaskSeqNo(String taskSeqNo) {
        this.taskSeqNo = taskSeqNo;
    }

    public String getWorkDate() {
        return workDate;
    }

    public void setWorkDate(String workDate) {
        this.workDate = workDate;
    }

    public String getWorkStartTime() {
        return workStartTime;
    }

    public void setWorkStartTime(String workStartTime) {
        this.workStartTime = workStartTime;
    }

    public String getWorkEndTime() {
        return workEndTime;
    }

    public void setWorkEndTime(String workEndTime) {
        this.workEndTime = workEndTime;
    }

    public String getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(String payAmount) {
        this.payAmount = payAmount;
    }

    public String getPayComplete() {
        return payComplete;
    }

    public void setPayComplete(String payComplete) {
        this.payComplete = payComplete;
    }
}
