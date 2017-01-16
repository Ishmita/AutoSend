package com.example.android.autosend.data;

import java.io.Serializable;

/**
 * Created by Ishmita on 04-01-2017.
 */
public class Contact implements Serializable {
    private String contactPhoto;
    private String contactName;
    private String number;

    public void setNumber(String number) {
        this.number = number;
    }

    public String getNumber() {
        return number;
    }

    public String getContactName() {
        return contactName;
    }

    public String getContactPhoto() {
        return contactPhoto;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public void setContactPhoto(String contactPhoto) {
        this.contactPhoto = contactPhoto;
    }
}
