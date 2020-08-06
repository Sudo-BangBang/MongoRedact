package com.sbb.mongoredact.service;

import com.sbb.mongoredact.model.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CustomerServiceTest {

    @Autowired
    CustomerService customerService;

    @Test
    void doADb() {
        customerService.doADb();
    }


    @Test
    void redactGet() {
        Customer customer = customerService.getRedactedCustomer("Pete",  Arrays.asList("ALL", "aaa"));
        System.out.println(customer);
        assertNull(customer.getAddress());
        assertEquals("251", customer.getCardDetails().getCvv());
    }
}