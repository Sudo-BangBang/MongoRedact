package com.sbb.mongoredact.repo;

import com.mongodb.BasicDBObject;
import com.sbb.mongoredact.model.Customer;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;

import java.util.*;

import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Filters.eq;

public class CustomerRedactRepoImpl implements CustomerRedactRepo {

    @Override
    public Customer findByFirstNameRedacted(String firstName, List<String> access) {

        AggregationOperation redactOperation = aggregationOperationContext -> {
            Map<String, Object> map = new LinkedHashMap<>();
            BasicDBObject cmp =  new BasicDBObject("$cmp", Arrays.asList( "$items.address","$dist.location"));
            map.put("if", new BasicDBObject("$eq", Arrays.asList(cmp, 0)));
            map.put("then", "$$KEEP");
            map.put("else", "$$PRUNE");
            return new BasicDBObject("$redact", new BasicDBObject("$cond", map));
        };

        // Requires official MongoShell 3.6+
        use redact_test;
        db.getCollection("customer").aggregate(
                [
                {
                        "$match" : {
            "firstName" : "Pete"
        }
        },
        {
            "$redact" : {
            "$cond" : {
                "if" : {
                    "$anyElementTrue" : [
                    {
                        "$map" : {
                        "input" : "$tags",
                                "as" : "objectAccess",
                                "in" : {
                            "$setIsSubset" : [
                                            [
                            "$$objectAccess"
                                            ],
                                            [
                            "ALL",
                                    "aaa"
                                            ]
                                        ]
                        }
                    }
                    }
                        ]
                },
                "then" : "$$DESCEND",
                        "else" : "$$PRUNE"
            }
        }
        }
    ],
        {
            "allowDiskUse" : false
        }
);


        MatchOperation match = null;
        AggregationOperation aggOp = Aggregation.newAggregation(Customer.class, Aggregation.redact())

//        Aggregation redactAgg = Aggregation.newAggregation(match);
        Aggregation redactAgg = Aggregation.newAggregation(Customer.class, Arrays.asList(
            match(eq("firstName", firstName)),
            new Document("$redact",
                new Document("$cond", Arrays.asList(
                    new Document("$anyElementTrue",
                        new Document("$map", new Document("input", "$tags")
                            .append("as", "objectAccess")
                            .append("in",
                                new Document("$setIsSubset", Arrays.asList("$$objectAccess", access)))))
                    , "$$DESCEND", "$$PRUNE")))
        ));

        return null;
    }
}
