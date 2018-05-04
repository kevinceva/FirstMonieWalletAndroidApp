package com.ceva.ubmobile.models;

/**
 * Created by brian on 07/11/2016.
 */

public class BottomMenuItem {
    private String title;
    private int icon;

    public BottomMenuItem(String title, int icon) {
        this.title = title;
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public int getIcon() {
        return icon;
    }
}
