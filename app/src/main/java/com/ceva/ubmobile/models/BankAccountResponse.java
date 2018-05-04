package com.ceva.ubmobile.models;

import com.ceva.ubmobile.core.constants.Constants;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brian on 29/09/2016.
 */
public class BankAccountResponse {
    @SerializedName("respcode")
    private String responseCode;
    @SerializedName(Constants.KEY_MSG)
    private String respDesc;
    //bank messages
    @SerializedName(Constants.KEY_BANKRESP_CODE)
    private String respcode;
    @SerializedName(Constants.KEY_BANK_MSG)
    private String responseDesc;
    //end
    @SerializedName(Constants.KEY_FULLINFO)
    private List<BankAccount> customerInfo = new ArrayList<BankAccount>();
    @SerializedName(Constants.KEY_MINISTATEMENT_ENDPOINT)
    private List<TransactionsModel> ministatement = new ArrayList<TransactionsModel>();
    @SerializedName(Beneficiary.KEY_BEN_DATA)
    private List<Beneficiary> beneficiaries = new ArrayList<Beneficiary>();

    @SerializedName(Banks.BK_KEY_BANKSDATA)
    private List<Banks> banks = new ArrayList<Banks>();
    @SerializedName(Branches.BR_KEY_BRANCHESDATA)
    private List<Branches> branches = new ArrayList<Branches>();

    @SerializedName(Constants.KEY_BEN_ACCOUNTNAME)
    private String beneficiaryAccountName;

    @SerializedName(Beneficiary.KEY_NIP_DATA)
    private JSONObject nipData;

    @SerializedName(Constants.KEY_REFERENCE_ID)
    private String referenceID;
    @SerializedName(Constants.KEY_CUSTOMER_ID)
    private String customerID;
    @SerializedName(Constants.KEY_EXTACCOUNTNUMBER)
    private String accountNumber;
    @SerializedName("accountNO")
    private String openAccountNumber;
    @SerializedName(Constants.KEY_DoB)
    private String dateOfBirth;

    @SerializedName("custPhone")
    private String custPhone;

    @SerializedName("userstatus")
    private String userStatus;

    @SerializedName("gender")
    private String gender;

    @SerializedName("INBOX")
    private List<MailBoxModel> InBox = new ArrayList<>();

    @SerializedName("billersdata")
    private List<BillerModel> billers = new ArrayList<>();

    @SerializedName(Constants.KEY_ACCOUNTNAME)
    private String accountName;

    // @SerializedName("NIPENQDATA")
    // private JSONObject nipData;

    public String getOpenAccountNumber() {
        return openAccountNumber;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public JSONObject getNipData() {
        return nipData;
    }

    public String getAccountName() {
        return accountName;
    }

    public List<BillerModel> getBillers() {
        return billers;
    }

    public List<MailBoxModel> getInBox() {
        return InBox;
    }

    public String getCustPhone() {
        return custPhone;
    }

    public String getGender() {
        return gender;
    }

    public String getReferenceID() {
        return referenceID;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getRespcode() {
        return respcode;
    }

    public String getCustomerID() {
        return customerID;
    }

    //public String getNipBeneficiaryAccountName() {
    //  return nipBeneficiaryAccountName;
    // }

    public String getBeneficiaryAccountName() {
        return beneficiaryAccountName;
    }

    public List<Beneficiary> getBeneficiaries() {
        return beneficiaries;
    }


    public String getResponseDesc() {
        return responseDesc;
    }


    public String getResponseCode() {
        return responseCode;
    }


    public String getRespDesc() {
        return respDesc;
    }


    public List<BankAccount> getCustomerInfo() {
        return customerInfo;
    }

    public List<TransactionsModel> getMinistatement() {
        return ministatement;
    }

    public List<Banks> getBanks() {
        return banks;
    }

    public List<Branches> getBranches() {
        return branches;
    }
}
