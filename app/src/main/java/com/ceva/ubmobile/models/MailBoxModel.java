package com.ceva.ubmobile.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by brian on 28/10/2016.
 */

public class MailBoxModel {
    public static final String MESSAGE = "SMSMSG";
    public static final String DATE = "MSG_DATE";
    @SerializedName("SMSMSG")
    private String title;
    @SerializedName("MSG_DATE")
    private String date;

    public MailBoxModel(String title, String date) {
        this.title = title;
        this.date = date;
    }

    //public MailBoxModel(String title) {
    //    this.title = title;
    //}

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }
}
