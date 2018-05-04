package com.ceva.ubmobile.security;

import android.content.Context;
import android.content.SharedPreferences;

import com.ceva.ubmobile.core.UBNApplication;
import com.ceva.ubmobile.core.constants.Constants;
import com.ceva.ubmobile.core.ui.Log;
import com.ceva.ubmobile.models.AccountsModel;
import com.ceva.ubmobile.models.BankAccount;
import com.ceva.ubmobile.models.Banks;
import com.ceva.ubmobile.models.Beneficiary;
import com.ceva.ubmobile.models.Branches;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brian on 29/09/2016.
 */
public class UBNSession {
    public static final String PREFS_NAME = "UBN_PREFS";
    public static final String PREFS_SETACCOUNTS = "setaccounts";
    public static final String PREFS_SET_BEN = "beneficiaries";
    public static final String KEY_LOGIN_STATUS = "loginstatus";
    public static final String KEY_APP_CLOSE = "closeapp";
    public static final String KEY_BIO = "fingerprintenabled";
    static final String KEY_CAMPAIGN = "campaignnotemaneno";
    // Editor for Shared preferences
    static SharedPreferences.Editor editor;
    // Shared Preferences
    public SharedPreferences pref;
    // Context
    Context _context;

    public UBNSession(Context _context) {
        this._context = _context;
        //pref = _context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        pref = UBNApplication.get().getSharedPreferences();
        editor = pref.edit();
    }

    public static void set(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public void setBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void enableorDisableBio(boolean state) {
        setBoolean(KEY_BIO, state);
    }

    public boolean isBioEnabled() {
        return getBoolean(KEY_BIO);
    }

    public boolean getBoolean(String key) {
        return pref.getBoolean(key, false);
    }

    /**
     * @param key
     * @param value
     */
    public void setString(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public void setStringArray(String key, List<String> list) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i)).append(",");
        }
        editor.putString(key, sb.toString());
        editor.commit();
    }

    public List<String> getStringArray(String key) {
        List<String> list = new ArrayList<>();
        String[] array = getString(key).split(",");
        int k = array.length;
        for (int i = 0; i < k; i++) {
            list.add(array[i]);
        }
        return list;
    }

    public String getString(String key) {
        return pref.getString(key, null);
    }

    public void setInt(String key, int i) {
        editor.putInt(key, i);
        editor.commit();
    }

    public int getInt(String key) {
        return pref.getInt(key, 0);

    }

    public void clearData(String key) {

        editor.remove(key);

    }

    public void setCampaignNote(String note) {
        setString(KEY_CAMPAIGN, note);
    }

    public String getCampaignNote() {
        return getString(KEY_CAMPAIGN);
    }

    public String getUserName() {
        String username = pref.getString(Constants.KEY_USER_ID, null);
        return username;
    }

    public void setUserName(String userName) {
        editor.putString(Constants.KEY_USER_ID, userName);
        editor.commit();
    }

    public <T> void setBankAccounts(List<T> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        set(Banks.BK_KEY_BANKSDATA, json);
    }
    public String getAccountName() {
        String username = pref.getString(Constants.KEY_ACCOUNTNAME, Constants.KEY_ACCOUNTNAME);
        return username;
    }

    public String getAccountName(String accountNumber) {

        List<BankAccount> accounts = new ArrayList<>();
        String accountsListString = pref.getString(Constants.KEY_FULLINFO, null);
        Gson gson = new Gson();
        accounts = gson.fromJson(accountsListString, new TypeToken<ArrayList<BankAccount>>() {
        }.getType());

        String accountname = null;

        for (int i = 0; i < accounts.size(); i++) {
            BankAccount bankAccount = accounts.get(i);

            if (accountNumber.trim().equalsIgnoreCase(bankAccount.getAccountNumber())) {
                accountname = bankAccount.getAccountName();

            }
        }

        return accountname;
    }

    public void setAccountName(String accountName) {
        editor.putString(Constants.KEY_ACCOUNTNAME, accountName);
        editor.commit();
    }

    public String getPhoneNumber() {
        String username = pref.getString(Constants.KEY_PHONENUMBER, Constants.KEY_PHONENUMBER);
        return username;
    }

    public void setPhoneNumber(String phoneNumber) {
        editor.putString(Constants.KEY_PHONENUMBER, phoneNumber);
        editor.commit();
    }

    public <T> void setAccountsData(String key, List<T> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        set(key, json);
    }

    public List<Banks> getBanks() {
        List<Banks> banksList = new ArrayList<>();
        String accountsListString = pref.getString(Banks.BK_KEY_BANKSDATA, null);
        Gson gson = new Gson();
        banksList = gson.fromJson(accountsListString, new TypeToken<ArrayList<Banks>>() {
        }.getType());
        return banksList;
    }

    public <T> void setBanks(List<T> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        set(Banks.BK_KEY_BANKSDATA, json);
    }

    public List<Branches> getBranches() {
        List<Branches> branchesList = new ArrayList<>();
        String accountsListString = pref.getString(Branches.BR_KEY_BRANCHESDATA, null);
        Gson gson = new Gson();
        branchesList = gson.fromJson(accountsListString, new TypeToken<ArrayList<Branches>>() {
        }.getType());
        return branchesList;
    }

    public <T> void setBranches(List<T> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        set(Branches.BR_KEY_BRANCHESDATA, json);
    }

    public <T> void setAccountsNumbers(List<T> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        set(PREFS_SETACCOUNTS, json);
    }

    public List<String> getAccountNumbers() {
        String accountsListString = pref.getString(Constants.KEY_FULLINFO, null);
        Gson gson = new Gson();
        List<String> accountList = new ArrayList<>();
        List<BankAccount> accounts = gson.fromJson(accountsListString, new TypeToken<ArrayList<BankAccount>>() {
        }.getType());
        int k = accounts.size();
        for (int j = 0; j < k; j++) {
            List<AccountsModel> acc = accounts.get(j).getCustomerInfo();
            accountList.add(acc.get(0).getAccountType() + " - " + accounts.get(j).getAccountNumber());
        }
        return accountList;
    }

    public List<String> getAccountNumbersNoDOM() {
        String accountsListString = pref.getString(Constants.KEY_FULLINFO, null);
        Gson gson = new Gson();
        List<String> accountList = new ArrayList<>();
        List<BankAccount> accounts = gson.fromJson(accountsListString, new TypeToken<ArrayList<BankAccount>>() {
        }.getType());
        int k = accounts.size();
        for (int j = 0; j < k; j++) {
            List<AccountsModel> acc = accounts.get(j).getCustomerInfo();
            if (acc.get(0).getAccountType().toLowerCase().contains("dom")) {
                //do nothing
            } else {
                String balance = Constants.KEY_NAIRA + accounts.get(j).getCustomerInfo().get(0).getAccountBalance();
                if (acc.get(0).isWallet()) {
                    accountList.add(acc.get(0).getAccountType() + "(" + balance + ")" + " - " + accounts.get(j).getPhoneNumber());
                } else {
                    accountList.add(acc.get(0).getAccountType() + "(" + balance + ")" + " - " + accounts.get(j).getAccountNumber());
                }
            }
        }
        return accountList;
    }

    public List<String> getAccountNumbersDOM() {
        String accountsListString = pref.getString(Constants.KEY_FULLINFO, null);
        Gson gson = new Gson();
        List<String> accountList = new ArrayList<>();
        List<BankAccount> accounts = gson.fromJson(accountsListString, new TypeToken<ArrayList<BankAccount>>() {
        }.getType());
        int k = accounts.size();
        for (int j = 0; j < k; j++) {
            List<AccountsModel> acc = accounts.get(j).getCustomerInfo();
            if (acc.get(0).getAccountType().toLowerCase().contains("dom")) {

                accountList.add(acc.get(0).getAccountType() + " - " + accounts.get(j).getAccountNumber());
            } else {
                //do nothing
            }
        }
        return accountList;
    }

    /**
     * @param list
     * @param <T>
     */
    public <T> void setBeneficiary(List<T> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        set(PREFS_SET_BEN, json);
    }

    /**
     * @return
     */
    public List<Beneficiary> getBeneficiaries() {
        String benListString = pref.getString(PREFS_SET_BEN, null);
        Gson gson = new Gson();

        List<Beneficiary> beneficiaries = gson.fromJson(benListString, new TypeToken<ArrayList<Beneficiary>>() {
        }.getType());

        return beneficiaries;
    }

    public List<String> getBranchNames() {
        List<Branches> branchesList = getBranches();
        int k = branchesList.size();
        List<String> branchNames = new ArrayList<>();
        Log.debug("*************BranchName start**************** ");
        for (int i = 0; i < k; i++) {
            Log.debug("BranchName: " + branchesList.get(i).getbranchName());
            branchNames.add(branchesList.get(i).getbranchName());
        }
        Log.debug("***************BranchName end**************");
        return branchNames;
    }


    public void clearData() {

        editor.remove(Constants.KEY_FULLINFO);//bank account model
        editor.remove(PREFS_SETACCOUNTS);//bank accounts list
    }
}
