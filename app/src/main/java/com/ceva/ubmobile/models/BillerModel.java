package com.ceva.ubmobile.models;

import com.google.gson.annotations.SerializedName;


/**
 * Created by brian on 16/01/2017.
 */

public class BillerModel {
    /*"paymentName": "Insurance",
            "fee": "100",
            "amount": "0",
            "billerId": "516",
            "paymentCode": "51602"*/
    public static final String KEY_CATEGORY = "category";
    public static final String KEY_BILLER_ID = "billerId";
    public static final String KEY_CUSTOM_FIELD1 = "customFieldName";
    public static final String KEY_CUSTOM_FIELD2 = "customFieldName2";
    public static final String KEY_SHORT_NAME = "shortName";
    public static final String KEY_BILLER_NAME = "billerName";

    static final String KEY_PAYMENTNAME = "paymentName";
    static final String KEY_FEE = "fee";
    static final String KEY_AMOUNT = "amount";
    static final String KEY_PAYMENTCODE = "paymentCode";

    @SerializedName(BillerModel.KEY_CATEGORY)
    private String category;

    @SerializedName(BillerModel.KEY_BILLER_ID)
    private String billerID;

    @SerializedName(BillerModel.KEY_CUSTOM_FIELD1)
    private String customField1;

    @SerializedName(BillerModel.KEY_CUSTOM_FIELD2)
    private String customField2;

    @SerializedName(BillerModel.KEY_SHORT_NAME)
    private String shortName;

    @SerializedName(BillerModel.KEY_BILLER_NAME)
    private String billerName;

    @SerializedName(BillerModel.KEY_PAYMENTNAME)
    private String paymentName;

    @SerializedName(BillerModel.KEY_FEE)
    private String fee;

    @SerializedName(BillerModel.KEY_AMOUNT)
    private String amount;

    @SerializedName(BillerModel.KEY_PAYMENTCODE)
    private String paymentCode;

    public BillerModel(String category, String billerID, String customField1, String customField2, String shortName, String billerName) {
        this.category = category;
        this.billerID = billerID;
        this.customField1 = customField1;
        this.customField2 = customField2;
        this.shortName = shortName;
        this.billerName = billerName;
    }

    public BillerModel(String billerID, String paymentName, String fee, String amount, String paymentCode) {
        this.billerID = billerID;
        this.paymentName = paymentName;
        this.fee = fee;
        this.amount = amount;
        this.paymentCode = paymentCode;
    }

    public String getPaymentName() {
        return paymentName;
    }

    public String getFee() {
        return fee;
    }

    public String getAmount() {
        return amount;
    }

    public String getPaymentCode() {
        return paymentCode;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBillerID() {
        return billerID;
    }

    public void setBillerID(String billerID) {
        this.billerID = billerID;
    }

    public String getCustomField1() {
        return customField1;
    }

    public void setCustomField1(String customField1) {
        this.customField1 = customField1;
    }

    public String getCustomField2() {
        return customField2;
    }

    public void setCustomField2(String customField2) {
        this.customField2 = customField2;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getBillerName() {
        return billerName;
    }

    public void setBillerName(String billerName) {
        this.billerName = billerName;
    }
}
