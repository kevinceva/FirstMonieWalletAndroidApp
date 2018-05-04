package com.ceva.ubmobile.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.ceva.ubmobile.core.constants.Constants;
import com.google.gson.annotations.SerializedName;

/**
 * Created by brian on 20/09/2016.
 */
public class AccountsModel implements Parcelable {
    public static final String KEY_CURRENCY = "accountcurrency";
    @SerializedName(Constants.KEY_ACCOUNTPRODUCT)
    private String accountType;
    @SerializedName(Constants.KEY_ACCOUNTNUMBER)
    private String accountNumber;
    @SerializedName(Constants.KEY_CLEAREDBALANCE)
    private String accountBalance;
    @SerializedName(KEY_CURRENCY)
    private String accountCurrency;
    private boolean isLoans;
    private boolean isWallet;
    private String accountName;

    public AccountsModel(String accountType, String accountNumber, String accountBalance, String accountCurrency, boolean isWallet, String accountName) {
        this.accountType = accountType;
        this.accountNumber = accountNumber;
        this.accountBalance = accountBalance;
        this.accountCurrency = accountCurrency;
        this.accountName = accountName;
        this.isWallet = isWallet;
    }

    public AccountsModel(String accountType, String accountNumber, String accountBalance) {
        this.accountType = accountType;
        this.accountNumber = accountNumber;
        this.accountBalance = accountBalance;
    }

    public AccountsModel(String accountType, String accountNumber, String accountBalance, String accountCurrency, boolean isLoans) {
        this.accountType = accountType;
        this.accountNumber = accountNumber;
        this.accountBalance = accountBalance;
        this.accountCurrency = accountCurrency;
        this.isLoans = isLoans;
    }


    protected AccountsModel(Parcel in) {
        accountType = in.readString();
        accountNumber = in.readString();
        accountBalance = in.readString();
        accountCurrency = in.readString();
        isLoans = in.readByte() != 0;
        isWallet = in.readByte() != 0;
        accountName = in.readString();
    }

    public static final Creator<AccountsModel> CREATOR = new Creator<AccountsModel>() {
        @Override
        public AccountsModel createFromParcel(Parcel in) {
            return new AccountsModel(in);
        }

        @Override
        public AccountsModel[] newArray(int size) {
            return new AccountsModel[size];
        }
    };

    public String getAccountName() {
        return accountName;
    }

    public boolean isWallet() {
        return isWallet;
    }

    public void setWallet(boolean wallet) {
        isWallet = wallet;
    }

    public boolean isLoans() {
        return isLoans;
    }

    public void setLoans(boolean loans) {
        isLoans = loans;
    }

    public String getAccountCurrency() {
        return accountCurrency;
    }

    public void setAccountCurrency(String accountCurrency) {
        this.accountCurrency = accountCurrency;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(String accountBalance) {
        this.accountBalance = accountBalance;
    }


    /**
     * Describe the kinds of special objects contained in this Parcelable
     * instance's marshaled representation. For example, if the object will
     * include a file descriptor in the output of {@link #writeToParcel(Parcel, int)},
     * the return value of this method must include the
     * {@link #CONTENTS_FILE_DESCRIPTOR} bit.
     *
     * @return a bitmask indicating the set of special object types marshaled
     * by this Parcelable object instance.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(accountType);
        dest.writeString(accountNumber);
        dest.writeString(accountBalance);
        dest.writeString(accountCurrency);
        dest.writeByte((byte) (isLoans ? 1 : 0));
        dest.writeByte((byte) (isWallet ? 1 : 0));
        dest.writeString(accountName);
    }
}
