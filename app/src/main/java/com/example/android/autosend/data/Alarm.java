package com.example.android.autosend.data;

import java.sql.Time;
import java.util.Date;

/**
 * Created by Ishmita on 09-01-2017.
 */
public class Alarm {
    private int id;
    private String alarmTitle;
    private String contactName;
    private String contactNumber;
    private String message;
    private String date;
    private int status;

    public Alarm(){

    }

    public Alarm(int id, String alarmTitle, String contactName, String contactNumber, String message,
                 String date, int status) {
        this.id = id;
        this.contactName = contactName;
        this.alarmTitle = alarmTitle;
        this.contactNumber = contactNumber;
        this.message = message;
        this.date = date;
        this.status = status;

    }
    public String getAlarmTitle() {
        return alarmTitle;
    }

    public String getContactName() {
        return contactName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getDate() {
        return date;
    }

    public int getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public void setAlarmTitle(String alarmTitle) {
        this.alarmTitle = alarmTitle;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}
