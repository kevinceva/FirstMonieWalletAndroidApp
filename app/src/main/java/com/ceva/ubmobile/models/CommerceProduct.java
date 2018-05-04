package com.ceva.ubmobile.models;

import java.util.List;

/**
 * Created by brian on 24/05/2017.
 */

public class CommerceProduct {
    public static final String KEY_ORGANIZATIONID = "ORGANIZATIONID";
    public static final String KEY_DISCOUNT_CASHBACK = "DISCOUNT_CASHBACK";
    public static final String KEY_PRODUCT_DESC = "PRODUCT_DESC";
    public static final String KEY_PRODUCT_ID = "PRODUCT_ID";
    public static final String KEY_PRODUCT_NAME = "PRODUCT_NAME";
    public static final String KEY_PRODUCT_CATEGORY_ID = "PRODUCT_CATEGORY_ID";
    public static final String KEY_PRODUCT_SUB_CATEGORY_ID = "PRODUCT_SUB_CATEGORY_ID";
    public static final String KEY_DISCOUNT_AMOUNT = "DISCOUNT_AMOUNT";
    public static final String KEY_END_DATE = "END_DATE";
    public static final String KEY_IMAGE = "IMAGE";
    public static final String KEY_TIME_LIMIT = "TIME_LIMIT";
    public static final String KEY_PRODUCT_CURRENT_PRICE = "PRODUCT_CURRENT_PRICE";
    public static final String KEY_CASHBACK_AMOUNT = "CASHBACK_AMOUNT";
    public static final String KEY_ORGANIZATIONNAME = "ORGANIZATIONNAME";
    public static final String KEY_MEDIA = "MEDIA";
    public static final String KEY_PRODUCT_IMAGE = "PRODUCT_IMAGE";

    private String ORGANIZATIONID;
    private String DISCOUNT_CASHBACK;
    private String PRODUCT_DESC;
    private String PRODUCT_ID;
    private String PRODUCT_NAME;
    private String PRODUCT_CATEGORY_ID;
    private String PRODUCT_SUB_CATEGORY_ID;
    private String DISCOUNT_AMOUNT;
    private String END_DATE;
    private String IMAGE;
    private String TIME_LIMIT;
    private String PRODUCT_CURRENT_PRICE;
    private String CASHBACK_AMOUNT;
    private String ORGANIZATIONNAME;
    private String PRODUCT_IMAGE;
    private List<String> MEDIA;

    public CommerceProduct(String ORGANIZATIONID, String DISCOUNT_CASHBACK, String PRODUCT_DESC, String PRODUCT_ID, String PRODUCT_NAME, String PRODUCT_CATEGORY_ID, String PRODUCT_SUB_CATEGORY_ID, String DISCOUNT_AMOUNT, String END_DATE, String IMAGE, String TIME_LIMIT, String PRODUCT_CURRENT_PRICE, String CASHBACK_AMOUNT, String ORGANIZATIONNAME, String PRODUCT_IMAGE, List<String> MEDIA) {
        this.ORGANIZATIONID = ORGANIZATIONID;
        this.DISCOUNT_CASHBACK = DISCOUNT_CASHBACK;
        this.PRODUCT_DESC = PRODUCT_DESC;
        this.PRODUCT_ID = PRODUCT_ID;
        this.PRODUCT_NAME = PRODUCT_NAME;
        this.PRODUCT_CATEGORY_ID = PRODUCT_CATEGORY_ID;
        this.PRODUCT_SUB_CATEGORY_ID = PRODUCT_SUB_CATEGORY_ID;
        this.DISCOUNT_AMOUNT = DISCOUNT_AMOUNT;
        this.END_DATE = END_DATE;
        this.IMAGE = IMAGE;
        this.TIME_LIMIT = TIME_LIMIT;
        this.PRODUCT_CURRENT_PRICE = PRODUCT_CURRENT_PRICE;
        this.CASHBACK_AMOUNT = CASHBACK_AMOUNT;
        this.ORGANIZATIONNAME = ORGANIZATIONNAME;
        this.PRODUCT_IMAGE = PRODUCT_IMAGE;
        this.MEDIA = MEDIA;
    }

    public String getORGANIZATIONID() {
        return ORGANIZATIONID;
    }

    public String getDISCOUNT_CASHBACK() {
        return DISCOUNT_CASHBACK;
    }

    public String getPRODUCT_DESC() {
        return PRODUCT_DESC;
    }

    public String getPRODUCT_ID() {
        return PRODUCT_ID;
    }

    public String getPRODUCT_NAME() {
        return PRODUCT_NAME;
    }

    public String getPRODUCT_CATEGORY_ID() {
        return PRODUCT_CATEGORY_ID;
    }

    public String getPRODUCT_SUB_CATEGORY_ID() {
        return PRODUCT_SUB_CATEGORY_ID;
    }

    public String getDISCOUNT_AMOUNT() {
        return DISCOUNT_AMOUNT;
    }

    public String getEND_DATE() {
        return END_DATE;
    }

    public String getIMAGE() {
        return IMAGE;
    }

    public String getTIME_LIMIT() {
        return TIME_LIMIT;
    }

    public String getPRODUCT_CURRENT_PRICE() {
        return PRODUCT_CURRENT_PRICE;
    }

    public String getCASHBACK_AMOUNT() {
        return CASHBACK_AMOUNT;
    }

    public String getORGANIZATIONNAME() {
        return ORGANIZATIONNAME;
    }

    public String getPRODUCT_IMAGE() {
        return PRODUCT_IMAGE;
    }

    public List<String> getMEDIA() {
        return MEDIA;
    }
}
