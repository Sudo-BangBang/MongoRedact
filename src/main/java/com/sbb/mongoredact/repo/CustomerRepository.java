package com.sbb.mongoredact.repo;

import java.util.List;

import com.sbb.mongoredact.model.Customer;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface CustomerRepository extends MongoRepository<Customer, String> {

    public Customer findByFirstName(String firstName);
    public List<Customer> findByLastName(String lastName);
    @Query("{'cardDetails.cvv': ?0}")
    public Customer findByCvv(String cvv);

    @Aggregation("{ \n" +
            "            \"$redact\" : { \n" +
            "                \"$cond\" : { \n" +
            "                    \"if\" : { \n" +
            "                        \"$anyElementTrue\" : [\n" +
            "                            { \n" +
            "                                \"$map\" : { \n" +
            "                                    \"input\" : \"$tags\", \n" +
            "                                    \"as\" : \"objectAccess\", \n" +
            "                                    \"in\" : { \n" +
            "                                        \"$setIsSubset\" : [\n" +
            "                                            [\n" +
            "                                                \"$$objectAccess\"\n" +
            "                                            ], \n" +
            "                                            [\n" +
            "                                                \"ALL\", \n" +
            "                                                \"aaa\"\n" +
            "                                            ]\n" +
            "                                        ]\n" +
            "                                    }\n" +
            "                                }\n" +
            "                            }\n" +
            "                        ]\n" +
            "                    }, \n" +
            "                    \"then\" : \"$$DESCEND\", \n" +
            "                    \"else\" : \"$$PRUNE\"\n" +
            "                }\n" +
            "            }\n" +
            "        }")
    public Customer findByFirstNameRedacted(String firstName, List<String> access);

}
