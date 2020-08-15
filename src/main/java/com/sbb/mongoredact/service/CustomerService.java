package com.sbb.mongoredact.service;

import com.sbb.mongoredact.model.Address;
import com.sbb.mongoredact.model.CardDetails;
import com.sbb.mongoredact.model.Customer;
import com.sbb.mongoredact.repo.CustomerRepository;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.SerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository repository;

    public void doADb() {

        Customer alice = new Customer("Alice", "Smith");
        Customer bob = new Customer("Bob", "Smith");
        Customer james = new Customer("James", "McMan", new Address("72 James Street", "James Town"), null);;
        Customer pete = new Customer("Pete", "Howdy", new Address("12 Moblin Lane", "Folkton", Arrays.asList("bbb")), new CardDetails("1235234734552345", "251", Arrays.asList("aaa")));;

        repository.deleteAll();

        // save a couple of customers
        insertCustomer(alice);
        insertCustomer(bob);
        insertCustomer(james);
        insertCustomer(pete);

        // fetch all customers
        System.out.println("Customers found with findAll():");
        System.out.println("-------------------------------");
        for (
            Customer customer : repository.findAll()) {
            System.out.println(customer);
        }
        System.out.println();

        // fetch an individual customer
        System.out.println("Customer found with findByFirstName('Alice'):");
        System.out.println("--------------------------------");
        System.out.println(repository.findByFirstName("Alice"));

        System.out.println("Customers found with findByLastName('Smith'):");
        System.out.println("--------------------------------");
        for (Customer customer : repository.findByLastName("Smith")) {
            System.out.println(customer);
        }

        // fetch an individual customer
        System.out.println("--------------------------------");
        System.out.println("Customer found with findByFirstName('Pete'):");
        System.out.println("--------------------------------");
        System.out.println(repository.findByFirstName("Pete").getCardDetails().getCvv());


        System.out.println("Customer found with cvv('251'):");
        System.out.println("--------------------------------");
        System.out.println(repository.findByCvv("251"));

    }

    public Customer getRedactedCustomer(String firstName, List<String> access){
        return repository.findByFirstNameRedacted(firstName, access);
    }

    public Customer insertCustomer(Customer customer){
        return repository.insert(customer);
    }

    public Customer updateCustomer(Customer customer, List<String> userAccess){
        repository.save(mergeUpdatedCustomerWithDbCustomer(customer, userAccess));
        return this.getRedactedCustomer(customer.getFirstName(), userAccess);
    }

    private Customer mergeUpdatedCustomerWithDbCustomer(Customer updatedCustomer, List<String> userAccess){

        //We don't want to modify the passed customer with restricted data so clone it
        Customer mergedCustomer = (Customer) SerializationUtils.clone(updatedCustomer);

        Customer fullCustomer = repository.findById(mergedCustomer.getId()).get();

        //If there are no element in common between the user access and the tags on the address, then that means the user can't see it and that we should keep the one in the database
        if(!CollectionUtils.containsAny(fullCustomer.getAddress().getTags(), userAccess)){
            mergedCustomer.setAddress(fullCustomer.getAddress());
        }

        if(!CollectionUtils.containsAny(fullCustomer.getCardDetails().getTags(), userAccess)){
            mergedCustomer.setCardDetails(fullCustomer.getCardDetails());
        }

        return mergedCustomer;
    }
}
