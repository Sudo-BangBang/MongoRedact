package com.sbb.mongoredact;

import com.sbb.mongoredact.model.Customer;
import com.sbb.mongoredact.repo.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MongoredactApplication {

	public static void main(String[] args) {
		SpringApplication.run(MongoredactApplication.class, args);
	}


}
