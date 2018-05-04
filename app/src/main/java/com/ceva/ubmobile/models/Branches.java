package com.ceva.ubmobile.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by brian on 09/11/2016.
 */

public class Branches {
    //"branchName":"101, IKWERRE ROAD P\/H","branchCode":"655"}
    public static final String BR_KEY_BRANCHCODE = "branchCode";
    public static final String BR_KEY_BRANCHNAME = "branchName";
    public static final String BR_KEY_BRANCHESDATA = "branchesdata";
    public static final String BR_KEY_BRANCHES_PARAM = "LOAD_BRANCHES";
    @SerializedName(Branches.BR_KEY_BRANCHCODE)
    private String branchCode;
    @SerializedName(Branches.BR_KEY_BRANCHNAME)
    private String branchName;


    public Branches(String branchCode, String branchName) {
        this.branchCode = branchCode;
        this.branchName = branchName;

    }

    public String getbranchCode() {
        return branchCode;
    }

    public void setbranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getbranchName() {
        return branchName;
    }

    public void setbranchName(String branchName) {
        this.branchName = branchName;
    }

}
