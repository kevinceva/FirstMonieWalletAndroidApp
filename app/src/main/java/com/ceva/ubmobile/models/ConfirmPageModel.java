package com.ceva.ubmobile.models;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by brian on 26/02/2018.
 */

public class ConfirmPageModel implements Parcelable {
    public static final String KEY_PIN = "{pin}";
    public static final String KEY_AUTH = "{auth}";
    public static final String KEY_AUTH_VAL = "{authval}";
    public static final Creator<ConfirmPageModel> CREATOR = new Creator<ConfirmPageModel>() {
        @Override
        public ConfirmPageModel createFromParcel(Parcel in) {
            return new ConfirmPageModel(in);
        }

        @Override
        public ConfirmPageModel[] newArray(int size) {
            return new ConfirmPageModel[size];
        }
    };
    private List<String> params;
    private String endpoint;
    private Intent intent;
    private List<ConfirmModel> confirmModelList;
    private String serviceType;
    private String transActionType; //Wallet = W, Other Bank = O, UBN = U, Bill Payment = B, Cardless = C
    private String positions; //pinpos|auth|auth_val
    private String buttonText;

    public ConfirmPageModel(List<String> params, String endpoint, Intent intent, List<ConfirmModel> confirmModelList, String serviceType, String transActionType, String positions, String buttonText) {
        this.params = params;
        this.endpoint = endpoint;
        this.intent = intent;
        this.confirmModelList = confirmModelList;
        this.serviceType = serviceType;
        this.transActionType = transActionType;
        this.positions = positions;
        this.buttonText = buttonText;
    }

    protected ConfirmPageModel(Parcel in) {
        params = in.createStringArrayList();
        endpoint = in.readString();
        intent = in.readParcelable(Intent.class.getClassLoader());
        confirmModelList = in.createTypedArrayList(ConfirmModel.CREATOR);
        serviceType = in.readString();
        transActionType = in.readString();
        positions = in.readString();
        buttonText = in.readString();
    }

    public List<String> getParams() {
        return params;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public Intent getIntent() {
        return intent;
    }

    public List<ConfirmModel> getConfirmModelList() {
        return confirmModelList;
    }

    public String getServiceType() {
        return serviceType;
    }

    public String getTransActionType() {
        return transActionType;
    }

    public String getPositions() {
        return positions;
    }

    public String getButtonText() {
        return buttonText;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(params);
        dest.writeString(endpoint);
        dest.writeParcelable(intent, flags);
        dest.writeTypedList(confirmModelList);
        dest.writeString(serviceType);
        dest.writeString(transActionType);
        dest.writeString(positions);
        dest.writeString(buttonText);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
