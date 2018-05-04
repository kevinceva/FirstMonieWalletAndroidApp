package com.ceva.ubmobile.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by brian on 09/11/2016.
 */

public class Banks {
    //"nibssCode":"000014","cbnBankCode":"044","bankName":"ACCESS BANK PLC","shortName":"ACCESS"
    public static final String BK_KEY_NIBSSCODE = "nibssCode";
    public static final String BK_KEY_CBNBANKCODE = "cbnBankCode";
    public static final String BK_KEY_BANKNAME = "bankName";
    public static final String BK_KEY_SHORTNAME = "shortName";
    public static final String BK_KEY_BANKSDATA = "banksdata";
    public static final String BK_KEY_BANKS_PARAM = "LOAD_BANKS";
    @SerializedName(Banks.BK_KEY_NIBSSCODE)
    private String bankCode;
    @SerializedName(Banks.BK_KEY_CBNBANKCODE)
    private String cbnBankCode;
    @SerializedName(Banks.BK_KEY_BANKNAME)
    private String bankName;
    @SerializedName(Banks.BK_KEY_SHORTNAME)
    private String shortName;

    public Banks(String bankCode, String cbnBankCode, String bankName, String shortName) {
        this.bankCode = bankCode;
        this.cbnBankCode = cbnBankCode;
        this.bankName = bankName;
        this.shortName = shortName;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getCbnBankCode() {
        return cbnBankCode;
    }

    public void setCbnBankCode(String cbnBankCode) {
        this.cbnBankCode = cbnBankCode;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }
}
