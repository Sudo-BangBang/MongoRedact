package com.sbb.mongoredact.repo;

import com.sbb.mongoredact.model.Customer;

import java.util.List;

public interface CustomerRedactRepo {

    public Customer findByFirstNameRedacted(String firstName, List<String> access);
}
