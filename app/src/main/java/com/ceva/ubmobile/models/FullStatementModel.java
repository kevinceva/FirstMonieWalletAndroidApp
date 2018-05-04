package com.ceva.ubmobile.models;

/**
 * Created by brian on 25/01/2017.
 */

public class FullStatementModel {
    public static final String KEY_ST_BALANCE = "xybalance";
    public static final String KEY_ST_DESC = "desc";
    public static final String KEY_ST_WITHDRAWAL = "withdraw";
    public static final String KEY_ST_DEPOSIT = "lodgement";
    public static final String KEY_ST_DATE = "valueDate";

    private String balance;
    private String desc;
    private String withdrawal;
    private String deposit;
    private String date;
    private String currency;

    public FullStatementModel(String balance, String desc, String withdrawal, String deposit, String date, String currency) {
        this.balance = balance;
        this.desc = desc;
        this.withdrawal = withdrawal;
        this.deposit = deposit;
        this.date = date;
        this.currency = currency;
    }

    public String getCurrency() {
        return currency;
    }

    public String getBalance() {
        return balance;
    }

    public String getDesc() {
        return desc;
    }

    public String getWithdrawal() {
        return withdrawal;
    }

    public String getDeposit() {
        return deposit;
    }

    public String getDate() {
        return date;
    }
}
