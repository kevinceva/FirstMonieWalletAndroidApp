package com.ceva.ubmobile.models;

import com.ceva.ubmobile.core.constants.Constants;
import com.google.gson.annotations.SerializedName;

/**
 * Created by brian on 06/10/2016.
 */
public class Beneficiary {
    public static final String KEY_BEN_DATA = "savedbeneficiaries";
    public static final String KEY_NIP_DATA = "NIPENQDATA";
    public static final String KEY_BEN_BANKCODE = "destinationBank";
    public static final String KEY_BEN_ID = "beneficiaryId";
    //@SerializedName(Constants.KEY_BEN_)
    @SerializedName(Constants.KEY_BEN_ACCOUNTNAME)
    private String name;
    @SerializedName(Constants.KEY_BEN_ACCOUNT)
    private String accountNumber;
    @SerializedName(Constants.KEY_BEN_BANKNAME)
    private String bank;

    @SerializedName(Beneficiary.KEY_BEN_BANKCODE)
    private String bankCode;

    @SerializedName(Beneficiary.KEY_BEN_ID)
    private String beneficiaryId;

    private String phoneNumber;
    private String telco;
    private String type;
// TODO: add destination bank codes for every beneficiary


    /**
     * @param name
     * @param accountNumber substitute for phone number
     * @param bank          substitute for telco
     * @param type
     */
    public Beneficiary(String name, String accountNumber, String bank, String type, String bankCode, String beneficiaryId) {
        this.name = name;
        this.accountNumber = accountNumber;
        this.bank = bank;
        this.type = type;
        this.bankCode = bankCode;
        this.beneficiaryId = beneficiaryId;
    }

    public String getBeneficiaryId() {
        return beneficiaryId;
    }

    public String getBankCode() {
        return bankCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getTelco() {
        return telco;
    }

    public void setTelco(String telco) {
        this.telco = telco;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
