package com.ceva.ubmobile.models;

/**
 * Created by brian on 05/04/2017.
 */

public class BankProductModel {
    //"DateOfBirth":"29-MAY-79","EnrollmentBank":"214","MiddleName":"DAVID",
    // "FirstName":"TOLUWASE","PhoneNumber":"8038382068","LastName":"ASHAOLU"}
    public static final String KEY_NUMBER = "number";
    public static final String KEY_TITLE = "title";
    public static final String KEY_PRODUCT_CODE = "productcode";
    public static final String KEY_PRODUCT_TYPE = "producttype";
    public static final String KEY_PRODUCT_DESC = "desc";
    public static final String KEY_DOB = "DateOfBirth";
    public static final String KEY_MIDNAME = "MiddleName";
    public static final String KEY_FNAME = "FirstName";
    public static final String KEY_PHONE = "PhoneNumber";
    public static final String KEY_LNAME = "LastName";
    public static final String KEY_GENDER = "gender";
    public static final String KEY_BRANCHNAME = "branchname";
    public static final String KEY_BRANCH_CODE = "branchcode";
    public static final String KEY_BVN = "bvn";

    private String productCode;
    private String productType;
    private String productDescription;

    public BankProductModel(String productCode, String productType) {
        this.productCode = productCode;
        this.productType = productType;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }
}
