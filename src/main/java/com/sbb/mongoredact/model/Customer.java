package com.sbb.mongoredact.model;

import org.springframework.data.annotation.Id;

import java.util.Arrays;

public class Customer extends SecureDocument {

    @Id
    private String id;

    private String firstName;
    private String lastName;
    private Address address;
    private CardDetails cardDetails;

    public Customer() {}

    public Customer(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.setTags(Arrays.asList("ALL"));
    }

    public Customer(String firstName, String lastName, Address address, CardDetails cardDetails) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.cardDetails = cardDetails;
        this.setTags(Arrays.asList("ALL"));
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public CardDetails getCardDetails() {
        return cardDetails;
    }

    public void setCardDetails(CardDetails cardDetails) {
        this.cardDetails = cardDetails;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", address=" + address +
                ", cardDetails=" + cardDetails +
                '}';
    }
}