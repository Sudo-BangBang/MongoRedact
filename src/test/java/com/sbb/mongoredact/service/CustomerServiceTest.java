package com.sbb.mongoredact.service;

import com.sbb.mongoredact.model.Address;
import com.sbb.mongoredact.model.CardDetails;
import com.sbb.mongoredact.model.Customer;
import com.sbb.mongoredact.repo.CustomerRepository;
import org.apache.commons.collections.CollectionUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CustomerServiceTest {

    @Autowired
    CustomerService customerService;

    @Autowired
    private CustomerRepository repository;

    @BeforeEach
    void before() {
        Customer alice = new Customer("Alice", "Smith");
        Customer bob = new Customer("Bob", "Smith");
        Customer james = new Customer("James", "McMan",
                new Address("72 James Street", "James Town"),
                null);

        Customer pete = new Customer("Pete", "Howdy",
                new Address("12 Moblin Lane", "Folkton", Arrays.asList("bbb")),
                new CardDetails("1235234734552345", "251", Arrays.asList("aaa")));


        repository.deleteAll();

        // save a couple of customers
        customerService.insertCustomer(alice);
        customerService.insertCustomer(bob);
        customerService.insertCustomer(james);
        customerService.insertCustomer(pete);
    }

    @Test
    void redactGet() {
        Customer customer = customerService.getRedactedCustomer("Pete", Arrays.asList("ALL", "aaa"));
        System.out.println(customer);
        assertNull(customer.getAddress());
        assertEquals("251", customer.getCardDetails().getCvv());

        customer = customerService.getRedactedCustomer("Pete", Arrays.asList("ALL", "bbb"));
        System.out.println(customer);
        assertEquals("12 Moblin Lane", customer.getAddress().getLineOne());
        assertNull(customer.getCardDetails());

        customer = customerService.getRedactedCustomer("Pete", Arrays.asList("ALL", "aaa", "bbb"));
        System.out.println(customer);
        assertEquals("12 Moblin Lane", customer.getAddress().getLineOne());
        assertEquals("251", customer.getCardDetails().getCvv());

        customer = customerService.getRedactedCustomer("Pete", Arrays.asList("ALL"));
        System.out.println(customer);
        assertNull(customer.getAddress());
        assertNull(customer.getCardDetails());
    }


    @Test
    void saveRedactedDoesNotLoseData() {
        //Fetch a fully redacted Pete
        Customer pete = customerService.getRedactedCustomer("Pete", Arrays.asList("ALL"));

        //Check we didn't get back data we shouldn't
        assertNull(pete.getAddress());
        assertNull(pete.getCardDetails());

        //Update Pete
        pete.setFirstName("Pete-updated-name");
        customerService.updateCustomer(pete, Arrays.asList("ALL"));

        //Fetch pete with all redacted data
        Customer newPete = customerService.getRedactedCustomer("Pete-updated-name", Arrays.asList("ALL", "aaa", "bbb"));
        assertEquals("Pete-updated-name", newPete.getFirstName());
        //Check we didnt lose anything in the update
        assertEquals("12 Moblin Lane", newPete.getAddress().getLineOne());
        assertEquals("251", newPete.getCardDetails().getCvv());

    }

    @Test
    void testScale(){

        Random random = new Random();

        //This can take a long time to run depending on these settings
        int numberOfPermissions = 5000;
        int numberOfCustomers = 10000;
        int maxNumberOfPermissionsPerUser = 1000;
        int maxPermissionsPerItem = 500;

        List<String> permissions = generatePermissions(numberOfPermissions);
        List<Customer> customers = generateCustomersAndFillDatabase(numberOfCustomers, maxPermissionsPerItem, permissions);

        AtomicInteger addressesRetrieved = new AtomicInteger(0);
        AtomicInteger cardsRetrieved = new AtomicInteger(0);
        AtomicInteger customersSearched = new AtomicInteger(0);

        customers.forEach(customer -> {
            List<String> userAccess = new ArrayList<>();
            //Add all so that we can retrieve the root customer objects
            userAccess.add("ALL");

            for (int i = 0; i < random.nextInt(maxNumberOfPermissionsPerUser); i++) {
                userAccess.add(permissions.get(random.nextInt(permissions.size())));
            }

            //retrieve the customer and check that we only got back items with matching permissions
            Customer retrievedCustomer = customerService.getRedactedCustomer(customer.getFirstName(), userAccess);

            if(retrievedCustomer.getAddress() != null){
                addressesRetrieved.getAndIncrement();
                assert(CollectionUtils.containsAny(retrievedCustomer.getAddress().getTags(), userAccess));
            }
            if(retrievedCustomer.getCardDetails() != null){
                cardsRetrieved.getAndIncrement();
                assert(CollectionUtils.containsAny(retrievedCustomer.getCardDetails().getTags(), userAccess));
            }

            //Log every 1000 customers, so we can keep an eye on progress
            customersSearched.getAndIncrement();
            if(customersSearched.get()%1000==0){
                System.out.println("--------------");
                System.out.println(customersSearched);
                System.out.println(addressesRetrieved);
                System.out.println(cardsRetrieved);
            }
        });

        //Check we don't get back everything or nothing
        assertNotEquals(0, addressesRetrieved);
        assertNotEquals(numberOfCustomers, addressesRetrieved);

        assertNotEquals(0, cardsRetrieved);
        assertNotEquals(numberOfCustomers, cardsRetrieved);

        System.out.println(addressesRetrieved);
        System.out.println(cardsRetrieved);
    }

    private List<String> generatePermissions(int numberOfPermissions){
        List<String> permissions = new ArrayList<>();

        for (int i = 0; i < numberOfPermissions; i++) {
            permissions.add(UUID.randomUUID().toString());
        }

        return permissions;
    }

    private Customer generateCustomer(List<String> permissions, int maxPermissionsPerItem){

        Random random = new Random();

        List<String> addressPermissions = new ArrayList<>();
        List<String> cardPermissions = new ArrayList<>();

        //Select a random number of permissions to add to the address and card
        for (int i = 0; i < random.nextInt(maxPermissionsPerItem); i++) {
            addressPermissions.add(permissions.get(random.nextInt(permissions.size())));
        }

        for (int i = 0; i < random.nextInt(maxPermissionsPerItem); i++) {
            cardPermissions.add(permissions.get(random.nextInt(permissions.size())));
        }

        return new Customer(UUID.randomUUID().toString(), UUID.randomUUID().toString(),
                new Address(UUID.randomUUID().toString(), UUID.randomUUID().toString(), addressPermissions),
                new CardDetails(UUID.randomUUID().toString(), UUID.randomUUID().toString(), cardPermissions));
    }

    private List<Customer> generateCustomersAndFillDatabase(int numberOfCustomers, int maxPermissionsPerItem, List<String> permissions){

        List<Customer> customers = new ArrayList<>();

        for (int i = 0; i < numberOfCustomers; i++) {
            customers.add(generateCustomer(permissions, maxPermissionsPerItem));
        }

        customerService.insertCustomers(customers);

        return customers;
    }
}