package com.ceva.ubmobile.models;

import com.ceva.ubmobile.core.constants.Constants;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brian on 29/09/2016.
 */
public class BankAccount {
    @SerializedName(Constants.KEY_FIRSTNAME)
    private String firstanme;
    @SerializedName(Constants.KEY_LASTNAME)
    private String lastname;
    @SerializedName(Constants.KEY_PHONENUMBER)
    private String phoneNumber;
    @SerializedName(Constants.KEY_ACCOUNTNUMBER)
    private String accountNumber;
    @SerializedName(Constants.KEY_ACCOUNTCURRENCY)
    private String accountCurrency;
    @SerializedName(Constants.KEY_TRANSFERLIMIT)
    private double transferLimit;
    @SerializedName(Constants.KEY_EMAIL)
    private String emailAddress;
    @SerializedName(Constants.KEY_ACCOUNTSTATUS)
    private String accountStatus;
    @SerializedName(Constants.KEY_ACCOUNTNAME)
    private String accountName;
    @SerializedName(Constants.KEY_ACCOUNTPRODUCT)
    private String accountProduct;
    @SerializedName(Constants.KEY_CUSTINFO)
    private List<AccountsModel> customerInfo = new ArrayList<AccountsModel>();


    public BankAccount(String firstanme, String lastname, String phoneNumber, String accountNumber, String accountCurrency, double transferLimit, String emailAddress, String accountStatus, String accountName, List<AccountsModel> customerInfo) {
        this.firstanme = firstanme;
        this.lastname = lastname;
        this.phoneNumber = phoneNumber;
        this.accountNumber = accountNumber;
        this.accountCurrency = accountCurrency;
        this.transferLimit = transferLimit;
        this.emailAddress = emailAddress;
        this.accountStatus = accountStatus;
        this.accountName = accountName;
        this.customerInfo = customerInfo;
    }

    public List<AccountsModel> getCustomerInfo() {
        return customerInfo;
    }

    public void setCustomerInfo(List<AccountsModel> customerInfo) {
        this.customerInfo = customerInfo;
    }

    public String getFirstanme() {
        return firstanme;
    }

    public void setFirstanme(String firstanme) {
        this.firstanme = firstanme;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountCurrency() {
        return accountCurrency;
    }

    public void setAccountCurrency(String accountCurrency) {
        this.accountCurrency = accountCurrency;
    }

    public double getTransferLimit() {
        return transferLimit;
    }

    public void setTransferLimit(double transferLimit) {
        this.transferLimit = transferLimit;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }
}
