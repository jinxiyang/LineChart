package com.yang.chartlibrary;

/**
 * 每天的人次
 * Created by yang on 2016/10/20.
 */

public class EverydayPeople {
    private String date;
    private int peopleNum;

    public EverydayPeople(String date, int peopleNum) {
        this.date = date;
        this.peopleNum = peopleNum;
    }

    public EverydayPeople() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getPeopleNum() {
        return peopleNum;
    }

    public void setPeopleNum(int peopleNum) {
        this.peopleNum = peopleNum;
    }
}
