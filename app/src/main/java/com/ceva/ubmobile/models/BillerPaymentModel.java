package com.ceva.ubmobile.models;

/**
 * Created by brian on 24/01/2017.
 */

public class BillerPaymentModel {
    public static final String KEY_BILL_AMOUNT = "amount";
    public static final String KEY_BILL_PAYNAME = "paymentName";
    public static final String KEY_BILL_FEE = "fee";
    public static final String KEY_BILL_ID = "billerId";
    public static final String KEY_BILL_CODE = "paymentCode";

    private String amount;
    private String paymentName;
    private String fee;
    private String billerID;
    private String paymentCode;

    public BillerPaymentModel(String amount, String paymentName, String fee, String billerID, String paymentCode) {
        this.amount = amount;
        this.paymentName = paymentName;
        this.fee = fee;
        this.billerID = billerID;
        this.paymentCode = paymentCode;
    }

    public String getAmount() {
        return amount;
    }

    public String getPaymentName() {
        return paymentName;
    }

    public String getFee() {
        return fee;
    }

    public String getBillerID() {
        return billerID;
    }

    public String getPaymentCode() {
        return paymentCode;
    }
}
