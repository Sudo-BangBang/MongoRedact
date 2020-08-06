package com.sbb.mongoredact.model;

import org.springframework.data.annotation.Id;

import java.util.Arrays;
import java.util.List;

public class CardDetails extends SecureDocument {

    @Id
    private String id;

    private String cardNumber;
    private String cvv;

    public CardDetails() {
    }

    public CardDetails(String cardNumber, String cvv) {
        this.cardNumber = cardNumber;
        this.cvv = cvv;
        this.setTags(Arrays.asList("ALL"));
    }

    public CardDetails(String cardNumber, String cvv, List<String> tags) {
        this.cardNumber = cardNumber;
        this.cvv = cvv;
        this.setTags(tags);
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    @Override
    public String toString() {
        return "CardDetails{" +
                "id='" + id + '\'' +
                ", cardNumber='" + cardNumber + '\'' +
                ", cvv='" + cvv + '\'' +
                '}';
    }
}
