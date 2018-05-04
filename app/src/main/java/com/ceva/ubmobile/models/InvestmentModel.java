package com.ceva.ubmobile.models;

/**
 * Created by brian on 06/02/2017.
 */

public class InvestmentModel {
    private String account;
    private String description;
    private String amount;
    private String details;

    public InvestmentModel(String account, String description, String amount, String details) {
        this.account = account;
        this.description = description;
        this.amount = amount;
        this.details = details;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
