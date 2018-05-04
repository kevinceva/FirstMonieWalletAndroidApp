package com.ceva.ubmobile.models;

import android.annotation.SuppressLint;

/**
 * Created by brian on 30/10/2016.
 */

@SuppressLint("ParcelCreator")
public class ShopItem {
    private String title;
    private String description;
    private String price;
    private String imageUrl;
    /* Parcelable interface implementation */
    /*public static final Parcelable.Creator<ShopItem> CREATOR = new Parcelable.Creator<ShopItem>() {
        @Override public ShopItem createFromParcel(@NonNull Parcel in) {
            return new ShopItem(in);
        }

        @Override @NonNull public ShopItem[] newArray(int size) {
            return new ShopItem[size];
        }
    };*/

    public ShopItem(String title, String imageUrl, String price, String description) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.price = price;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
