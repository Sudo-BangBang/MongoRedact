package com.sbb.mongoredact.repo;

import com.sbb.mongoredact.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.DEFAULT_CONTEXT;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.redact;
import static org.springframework.data.mongodb.core.aggregation.ArrayOperators.arrayOf;
import static org.springframework.data.mongodb.core.aggregation.ConditionalOperators.when;
import static org.springframework.data.mongodb.core.aggregation.SetOperators.arrayAsSet;
import static org.springframework.data.mongodb.core.aggregation.VariableOperators.mapItemsOf;

public class CustomerRedactRepoImpl implements CustomerRedactRepo {

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public Customer findByFirstNameRedacted(String firstName, List<String> userAccess) {

        System.out.println(mongoTemplate);
        MatchOperation matchStage = Aggregation.match(new Criteria("firstName").is(firstName));

            RedactOperation redact = redact(when(
                arrayAsSet(
                        mapItemsOf("tags")
                                .as("objectAccess")
                                .andApply(
                                        arrayOf(userAccess).containsValue("$$objectAccess")
                                )
                ).anyElementTrue()
            ).then(RedactOperation.DESCEND)
            .otherwise(RedactOperation.PRUNE));

        System.out.println(redact.toDocument(Aggregation.DEFAULT_CONTEXT).toJson());

        Aggregation aggregation
                = Aggregation.newAggregation(matchStage, redact);

        System.out.println(aggregation.toDocument("customer", DEFAULT_CONTEXT).toJson());

        AggregationResults<Customer> output
                = mongoTemplate.aggregate(aggregation, Customer.class, Customer.class);

        return output.getMappedResults().get(0);
    }
}
