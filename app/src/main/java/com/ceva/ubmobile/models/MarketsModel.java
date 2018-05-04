package com.ceva.ubmobile.models;

/**
 * Created by brian on 27/02/2017.
 */

public class MarketsModel {
    public static final String KEY_CAT_DESC = "CATEGORY_DESC";
    public static final String KEY_CAT_ID = "CATEGORY_ID";
    public static final String KEY_SUBCAT_DESC = "SUB_CATEGORY_DESC";
    public static final String KEY_SUBCAT_ID = "SUB_CATEGORY_ID";

    private String category;
    private String id;

    public MarketsModel(String category, String id) {
        this.category = category;
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
