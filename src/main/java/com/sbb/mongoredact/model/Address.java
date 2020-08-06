package com.sbb.mongoredact.model;

import org.springframework.data.annotation.Id;

import java.util.Arrays;
import java.util.List;

public class Address extends SecureDocument {

    @Id
    private String id;

    private String lineOne;
    private String lineTwo;

    public Address() {
    }

    public Address(String lineOne, String lineTwo) {
        this.lineOne = lineOne;
        this.lineTwo = lineTwo;
        this.setTags(Arrays.asList("ALL"));
    }

    public Address(String lineOne, String lineTwo, List<String> tags) {
        this.lineOne = lineOne;
        this.lineTwo = lineTwo;
        this.setTags(tags);
    }

    public String getLineOne() {
        return lineOne;
    }

    public void setLineOne(String lineOne) {
        this.lineOne = lineOne;
    }

    public String getLineTwo() {
        return lineTwo;
    }

    public void setLineTwo(String lineTwo) {
        this.lineTwo = lineTwo;
    }

    @Override
    public String toString() {
        return "Address{" +
                "lineOne='" + lineOne + '\'' +
                ", lineTwo='" + lineTwo + '\'' +
                '}';
    }


}
