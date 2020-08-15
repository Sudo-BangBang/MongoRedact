package com.sbb.mongoredact.repo;

import java.util.List;

import com.sbb.mongoredact.model.Customer;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface CustomerRepository extends MongoRepository<Customer, String>, CustomerRedactRepo {

    public Customer findByFirstName(String firstName);
    public List<Customer> findByLastName(String lastName);
    @Query("{'cardDetails.cvv': ?0}")
    public Customer findByCvv(String cvv);
}
