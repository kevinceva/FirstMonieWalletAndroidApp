package com.ceva.ubmobile.models;

/**
 * Created by brian on 01/04/2017.
 */

public class NewsModel {
    private String title;
    private String url;
    private int logo;
    private boolean isSelected;
    private String hostname;

    public NewsModel(String title, String url, int logo, String hostname) {
        this.title = title;
        this.url = url;
        this.logo = logo;
        this.hostname = hostname;
    }

    public NewsModel(String title, String url, int logo, boolean isSelected) {
        this.title = title;
        this.url = url;
        this.logo = logo;
        this.isSelected = isSelected;
    }

    public String getHostname() {
        return hostname;
    }

    public int getLogo() {
        return logo;
    }

    public void setLogo(int logo) {
        this.logo = logo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
