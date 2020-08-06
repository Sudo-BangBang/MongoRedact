package com.sbb.mongoredact.service;

import com.sbb.mongoredact.model.Address;
import com.sbb.mongoredact.model.CardDetails;
import com.sbb.mongoredact.model.Customer;
import com.sbb.mongoredact.repo.CustomerRepository;
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
        repository.save(alice);
        repository.save(bob);
        repository.save(james);
        repository.save(pete);

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

//        Customer alice = repository.findByFirstName("Alice");
//        Customer bob = repository.findByFirstName("Bob");

    }

    public Customer getRedactedCustomer(String firstName, List<String> access){

        return repository.findByFirstNameRedacted(firstName, access);
    }
}
