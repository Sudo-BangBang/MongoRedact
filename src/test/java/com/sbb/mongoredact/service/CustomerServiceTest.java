package com.sbb.mongoredact.service;

import com.sbb.mongoredact.model.Address;
import com.sbb.mongoredact.model.CardDetails;
import com.sbb.mongoredact.model.Customer;
import com.sbb.mongoredact.repo.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CustomerServiceTest {

    @Autowired
    CustomerService customerService;

    @Autowired
    private CustomerRepository repository;

    @BeforeEach
    void before(){
        Customer alice = new Customer("Alice", "Smith");
        Customer bob = new Customer("Bob", "Smith");
        Customer james = new Customer("James", "McMan",
                new Address("72 James Street", "James Town"),
                null);;
        Customer pete = new Customer("Pete", "Howdy",
                new Address("12 Moblin Lane", "Folkton", Arrays.asList("bbb")),
                new CardDetails("1235234734552345", "251", Arrays.asList("aaa")));;

        repository.deleteAll();

        // save a couple of customers
        customerService.insertCustomer(alice);
        customerService.insertCustomer(bob);
        customerService.insertCustomer(james);
        customerService.insertCustomer(pete);
    }

    @Test
    void doADb() {
//        customerService.doADb();
    }


    @Test
    void redactGet() {
        Customer customer = customerService.getRedactedCustomer("Pete",  Arrays.asList("ALL", "aaa"));
        System.out.println(customer);
        assertNull(customer.getAddress());
        assertEquals("251", customer.getCardDetails().getCvv());

        customer = customerService.getRedactedCustomer("Pete",  Arrays.asList("ALL", "bbb"));
        System.out.println(customer);
        assertEquals("12 Moblin Lane", customer.getAddress().getLineOne());
        assertNull(customer.getCardDetails());

        customer = customerService.getRedactedCustomer("Pete",  Arrays.asList("ALL", "aaa", "bbb"));
        System.out.println(customer);
        assertEquals("12 Moblin Lane", customer.getAddress().getLineOne());
        assertEquals("251", customer.getCardDetails().getCvv());

        customer = customerService.getRedactedCustomer("Pete",  Arrays.asList("ALL"));
        System.out.println(customer);
        assertNull(customer.getAddress());
        assertNull(customer.getCardDetails());

    }
}