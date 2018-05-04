package com.ceva.ubmobile.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by brian on 20/01/2017.
 */

public class ConfirmModel implements Parcelable {
    public static final Creator<ConfirmModel> CREATOR = new Creator<ConfirmModel>() {
        @Override
        public ConfirmModel createFromParcel(Parcel in) {
            return new ConfirmModel(in);
        }

        @Override
        public ConfirmModel[] newArray(int size) {
            return new ConfirmModel[size];
        }
    };
    private String label;
    private String value;

    public ConfirmModel(String label, String value) {
        this.label = label;
        this.value = value;
    }

    protected ConfirmModel(Parcel in) {
        label = in.readString();
        value = in.readString();
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(label);
        parcel.writeString(value);
    }
}
