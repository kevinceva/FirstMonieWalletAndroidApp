package com.ceva.ubmobile.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by brian on 17/08/2017.
 */

public class TransactionProcessor implements Parcelable {
    public static final Creator<TransactionProcessor> CREATOR = new Creator<TransactionProcessor>() {
        @Override
        public TransactionProcessor createFromParcel(Parcel in) {
            return new TransactionProcessor(in);
        }

        @Override
        public TransactionProcessor[] newArray(int size) {
            return new TransactionProcessor[size];
        }
    };
    boolean isTransaction;
    private String endpoint;
    private String priority;
    private String service_type;
    private List<ConfirmModel> confirmModelList;
    private List<String> params;
    private String pin;
    private String buttonName;

    protected TransactionProcessor(Parcel in) {
        endpoint = in.readString();
        priority = in.readString();
        service_type = in.readString();
        isTransaction = in.readByte() != 0;
    }

    /**
     * @param endpoint
     * @param priority
     * @param service_type
     * @param confirmModelList
     * @param params
     * @param pin
     * @param isTransaction
     * @param buttonName
     */
    public TransactionProcessor(String endpoint, String priority, String service_type, List<ConfirmModel> confirmModelList, List<String> params, String pin, boolean isTransaction, String buttonName) {
        this.endpoint = endpoint;
        this.priority = priority;
        this.service_type = service_type;
        this.confirmModelList = confirmModelList;
        this.params = params;
        this.pin = pin;
        this.isTransaction = isTransaction;
        this.buttonName = buttonName;
    }

    public String getButtonName() {
        return buttonName;
    }

    public void setButtonName(String buttonName) {
        this.buttonName = buttonName;
    }

    public List<String> getParams() {
        return params;
    }

    public void setParams(List<String> params) {
        this.params = params;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getService_type() {
        return service_type;
    }

    public void setService_type(String service_type) {
        this.service_type = service_type;
    }

    public List<ConfirmModel> getConfirmModelList() {
        return confirmModelList;
    }

    public void setConfirmModelList(List<ConfirmModel> confirmModelList) {
        this.confirmModelList = confirmModelList;
    }

    public boolean isTransaction() {
        return isTransaction;
    }

    public void setTransaction(boolean transaction) {
        isTransaction = transaction;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(endpoint);
        parcel.writeString(priority);
        parcel.writeString(service_type);
        parcel.writeByte((byte) (isTransaction ? 1 : 0));
    }
}
