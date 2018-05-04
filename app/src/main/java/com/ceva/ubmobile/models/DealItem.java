package com.ceva.ubmobile.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by brian on 13/01/2017.
 */

public class DealItem implements Parcelable {
    public static final String KEY_DEALS = "deals";
    public static final String KEY_IMAGE_1 = "image1";
    public static final String KEY_IMAGE_2 = "image2";
    public static final String KEY_IMAGE_3 = "image3";
    public static final String KEY_NAME = "productname";
    public static final String KEY_PRICE = "productprice";
    public static final String KEY_DESC = "description";
    public static final Creator<DealItem> CREATOR = new Creator<DealItem>() {
        @Override
        public DealItem createFromParcel(Parcel in) {
            return new DealItem(in);
        }

        @Override
        public DealItem[] newArray(int size) {
            return new DealItem[size];
        }
    };
    private String ImageUrl;
    private String businessName;
    private String savingsTag;
    private String secondImage;
    private String thirdImage;
    private String description;
    private String originalPrice;
    private String discountPrice;
    private String productName;
    private String merchantCode;
    private String productLink;

    public DealItem(String imageUrl, String businessName, String savingsTag) {
        ImageUrl = imageUrl;
        this.businessName = businessName;
        this.savingsTag = savingsTag;
    }

    public DealItem(String imageUrl, String businessName, String savingsTag, String secondImage, String thirdImage, String description) {
        ImageUrl = imageUrl;
        this.businessName = businessName;
        this.savingsTag = savingsTag;
        this.secondImage = secondImage;
        this.thirdImage = thirdImage;
        this.description = description;
    }

    public DealItem(String imageUrl, String businessName, String savingsTag, String secondImage, String thirdImage, String description, String originalPrice, String discountPrice, String productName, String merchantCode, String productLink) {
        ImageUrl = imageUrl;
        this.businessName = businessName;
        this.savingsTag = savingsTag;
        this.secondImage = secondImage;
        this.thirdImage = thirdImage;
        this.description = description;
        this.originalPrice = originalPrice;
        this.discountPrice = discountPrice;
        this.productName = productName;
        this.merchantCode = merchantCode;
        this.productLink = productLink;
    }

    protected DealItem(Parcel in) {
        ImageUrl = in.readString();
        businessName = in.readString();
        savingsTag = in.readString();
        secondImage = in.readString();
        thirdImage = in.readString();
        description = in.readString();
        originalPrice = in.readString();
        discountPrice = in.readString();
        productName = in.readString();
        merchantCode = in.readString();
        productLink = in.readString();
    }

    public String getProductLink() {
        return productLink;
    }

    public void setProductLink(String productLink) {
        this.productLink = productLink;
    }

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public String getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(String originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(String discountPrice) {
        this.discountPrice = discountPrice;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSecondImage() {
        return secondImage;
    }

    public void setSecondImage(String secondImage) {
        this.secondImage = secondImage;
    }

    public String getThirdImage() {
        return thirdImage;
    }

    public void setThirdImage(String thirdImage) {
        this.thirdImage = thirdImage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getSavingsTag() {
        return savingsTag;
    }

    public void setSavingsTag(String savingsTag) {
        this.savingsTag = savingsTag;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(ImageUrl);
        parcel.writeString(businessName);
        parcel.writeString(savingsTag);
        parcel.writeString(secondImage);
        parcel.writeString(thirdImage);
        parcel.writeString(description);
        parcel.writeString(originalPrice);
        parcel.writeString(discountPrice);
        parcel.writeString(productName);
        parcel.writeString(merchantCode);
        parcel.writeString(productLink);
    }
}
