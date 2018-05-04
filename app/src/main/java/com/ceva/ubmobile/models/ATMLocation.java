package com.ceva.ubmobile.models;

/**
 * Created by brian on 28/02/2017.
 */

public class ATMLocation {
    public static final String KEY_LOCATION = "BANK_LOCAION";
    public static final String KEY_LATITUDE = "BANK_LATITUDE";
    public static final String KEY_LONGITUDE = "BANK_LONGITUDE";
    public static final String KEY_BANKADDR = "BANKADDRESS";
    // public static final String KEY_
    private String location;
    private String bankAddress;
    private String latitude;
    private String longitude;

    /**
     * @param location
     * @param bankAddress
     * @param latitude
     * @param longitude
     */

    public ATMLocation(String location, String bankAddress, String latitude, String longitude) {
        this.location = location;
        this.bankAddress = bankAddress;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getBankAddress() {
        return bankAddress;
    }

    public void setBankAddress(String bankAddress) {
        this.bankAddress = bankAddress;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
