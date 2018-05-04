package com.ceva.ubmobile.models;

/**
 * Created by brian on 17/11/2016.
 */

public class AccountProducts {
    boolean selected = false;
    private String productName;
    private String productCode;

    public AccountProducts(String productName, String productCode, boolean selected) {
        this.productName = productName;
        this.productCode = productCode;
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }
}
