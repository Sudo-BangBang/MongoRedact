package com.sbb.mongoredact.service;

import com.sbb.mongoredact.model.Customer;
import com.sbb.mongoredact.repo.CustomerRepository;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.SerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository repository;

    public Customer getRedactedCustomer(String firstName, List<String> access){
        return repository.findByFirstNameRedacted(firstName, access);
    }

    public Customer insertCustomer(Customer customer){
        return repository.insert(customer);
    }

    public List<Customer> insertCustomers(List<Customer> customers){
        return repository.insert(customers);
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
