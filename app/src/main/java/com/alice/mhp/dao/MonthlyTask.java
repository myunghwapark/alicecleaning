package com.alice.mhp.dao;

import java.util.ArrayList;

public class MonthlyTask {
    String date;
    ArrayList<Task> taskArrayList;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<Task> getTaskArrayList() {
        return taskArrayList;
    }

    public void setTaskArrayList(ArrayList<Task> taskArrayList) {
        this.taskArrayList = taskArrayList;
    }

    public void addTaskArrayList(Task task) {
        this.taskArrayList.add(task);
    }
}
