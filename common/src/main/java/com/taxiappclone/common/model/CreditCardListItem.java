package com.taxiappclone.common.model;

/**
 * Created by Adee on 13/3/2018.
 */

public class CreditCardListItem {
    public int cardId;
    public String cardHolderName, cardNetwork, cardFirstDigit, cardLastDigits, billingAddress, expiresAt;

    public CreditCardListItem(int cardId, String cardHolderName, String cardNetwork, String cardFirstDigit, String cardLastDigits, String billingAddress, String expiresAt)
    {
        this.cardId = cardId;
        this.cardHolderName = cardHolderName;
        this.cardNetwork = cardNetwork;
        this.cardFirstDigit = cardFirstDigit;
        this.cardLastDigits = cardLastDigits;
        this.billingAddress = billingAddress;
        this.expiresAt = expiresAt;
    }
}