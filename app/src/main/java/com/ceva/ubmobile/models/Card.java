package com.ceva.ubmobile.models;

/**
 * Created by brian on 27/10/2016.
 */

public class Card {
    private String cardNumber;
    private String cardName;
    private String cardExpiry;
    private String cardStatus;
    private String cardLimit;

    public Card(String cardNumber, String cardName, String cardExpiry) {
        this.cardNumber = cardNumber;
        this.cardName = cardName;
        this.cardExpiry = cardExpiry;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getCardName() {
        return cardName;
    }

    public String getCardExpiry() {
        return cardExpiry;
    }
}
