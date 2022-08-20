package com.hcmus.wiberback.repository;

import com.hcmus.wiberback.model.entity.Account;
import com.hcmus.wiberback.model.entity.Customer;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends MongoRepository<Customer, String> {

  Optional<Customer> findCustomerByAccount(Account account);

  Optional<Customer> findCustomerByPhone(String phone);

  boolean existsByAccount(Account account);
}
