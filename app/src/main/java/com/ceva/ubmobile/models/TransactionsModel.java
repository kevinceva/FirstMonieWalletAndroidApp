package com.ceva.ubmobile.models;

import com.ceva.ubmobile.core.constants.Constants;
import com.google.gson.annotations.SerializedName;

/**
 * Created by brian on 23/09/2016.
 */
public class TransactionsModel {
    @SerializedName(Constants.KEY_TX_NARRATION)
    private String narrative;
    @SerializedName(Constants.KEY_TX_DATE)
    private String transactionDate;
    @SerializedName(Constants.KEY_TX_DRCR)
    private String transactionType;
    @SerializedName("xybalance")
    private String transactionAmount;

    @SerializedName("withdraw")
    private String transactionAmountWithdraw;

    @SerializedName("lodgement")
    private String transactionAmountLodgement;

    private String transactionAccount;
    private String transactionRef;//also currency

    public TransactionsModel(String narrative, String transactionDate,
                             String transactionType, String transactionAmount, String transactionAmountWithdraw, String transactionAmountLodgement, String transactionAccount,
                             String transactionRef) {
        this.narrative = narrative;
        this.transactionDate = transactionDate;
        this.transactionType = transactionType;
        this.transactionAmount = transactionAmount;
        this.transactionAmountWithdraw = transactionAmountWithdraw;
        this.transactionAmountLodgement = transactionAmountLodgement;
        this.transactionAccount = transactionAccount;

        this.transactionRef = transactionRef;
    }

    public String getTransactionAmountWithdraw() {
        return transactionAmountWithdraw;
    }

    public void setTransactionAmountWithdraw(String transactionAmountWithdraw) {
        this.transactionAmountWithdraw = transactionAmountWithdraw;
    }

    public String getTransactionAmountLodgement() {
        return transactionAmountLodgement;
    }

    public void setTransactionAmountLodgement(String transactionAmountLodgement) {
        this.transactionAmountLodgement = transactionAmountLodgement;
    }

    public String getNarrative() {
        return narrative;
    }

    public void setNarrative(String narrative) {
        this.narrative = narrative;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(String transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public String getTransactionAccount() {
        return transactionAccount;
    }

    public void setTransactionAccount(String transactionAccount) {
        this.transactionAccount = transactionAccount;
    }

    public String getTransactionRef() {
        return transactionRef;
    }

    public void setTransactionRef(String transactionRef) {
        this.transactionRef = transactionRef;
    }
}
