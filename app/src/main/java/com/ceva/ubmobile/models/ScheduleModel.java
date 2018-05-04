package com.ceva.ubmobile.models;

/**
 * Created by brian on 03/02/2017.
 */

public class ScheduleModel {
    public static final String KEY_START_DATE = "startDate";
    public static final String KEY_END_DATE = "endDate";
    public static final String KEY_FREQUENCY = "frequency";
    public static final String KEY_AMOUNT = "amount";
    public static final String KEY_BEN_NAME = "beneficiaryAccountName";
    public static final String KEY_DEST_BANK = "destinationBankCode";
    public static final String KEY_ORIGIN = "originatorAccountNumber";
    public static final String KEY_BEN_ACC = "beneficiaryAccountNumber";

    private String startDate;
    private String endDate;
    private String frequency;
    private String amount;
    private String beneficiaryAccountName;
    private String destionBank;
    private String myAccountNumber;
    private String beneficiaryAccountNumber;

    public ScheduleModel(String startDate, String endDate, String frequency, String amount, String beneficiaryAccountName, String destionBank, String myAccountNumber, String beneficiaryAccountNumber) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.frequency = frequency;
        this.amount = amount;
        this.beneficiaryAccountName = beneficiaryAccountName;
        this.destionBank = destionBank;
        this.myAccountNumber = myAccountNumber;
        this.beneficiaryAccountNumber = beneficiaryAccountNumber;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getBeneficiaryAccountName() {
        return beneficiaryAccountName;
    }

    public void setBeneficiaryAccountName(String beneficiaryAccountName) {
        this.beneficiaryAccountName = beneficiaryAccountName;
    }

    public String getDestionBank() {
        return destionBank;
    }

    public void setDestionBank(String destionBank) {
        this.destionBank = destionBank;
    }

    public String getMyAccountNumber() {
        return myAccountNumber;
    }

    public void setMyAccountNumber(String myAccountNumber) {
        this.myAccountNumber = myAccountNumber;
    }

    public String getBeneficiaryAccountNumber() {
        return beneficiaryAccountNumber;
    }

    public void setBeneficiaryAccountNumber(String beneficiaryAccountNumber) {
        this.beneficiaryAccountNumber = beneficiaryAccountNumber;
    }
}
