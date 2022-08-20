package com.hcmus.wiberback.repository.custom.impl;

import com.hcmus.wiberback.model.entity.Account;
import com.hcmus.wiberback.model.entity.CarRequest;
import com.hcmus.wiberback.model.entity.Customer;
import com.hcmus.wiberback.model.enums.CarRequestStatus;
import com.hcmus.wiberback.model.exception.UserNotFoundException;
import com.hcmus.wiberback.repository.custom.CarRequestRepositoryCustom;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CarRequestRepositoryCustomImpl implements CarRequestRepositoryCustom {

  private static final String CUSTOMER_COLLECTION = "customer";

  private final MongoTemplate mongoTemplate;

  @Override
  public List<CarRequest> getCarRequestsByPhoneAndStatus(String phone, CarRequestStatus status) {
    Customer customer;

    Query findCustomerQuery = Query.query(Criteria.where("phone").is(phone));
    List<Account> accounts = mongoTemplate.find(findCustomerQuery, Account.class, "account");
    if (accounts.isEmpty()) {
      throw new UserNotFoundException("Customer not found", phone);
    }

    Account account = accounts.get(0);

    List<Customer> customers = mongoTemplate
        .find(Query.query(
            Criteria
                .where("account.$id")
                .is(new ObjectId(account.getId()))),
            Customer.class,
            CUSTOMER_COLLECTION);

    if (customers.isEmpty()) {
      throw new UserNotFoundException("Customer not found", phone);
    }
    customer = customers.get(0);

    return mongoTemplate.find(Query.query(
            Criteria
                .where("customer.$id")
                .is(new ObjectId(customer.getId()))
                .and("status")
                .is(status)),
        CarRequest.class,
        "carRequest");
  }

  @Override
  public List<CarRequest> searchAddress(String phone) {
    log.info("Searching address by phone: {}", phone);
    Customer customer;

    Query findCustomerQuery = Query.query(Criteria.where("phone").is(phone));
    List<Account> accounts = mongoTemplate.find(findCustomerQuery, Account.class, "account");
    List<Customer> customers;
    if (accounts.isEmpty()) {
      customers = mongoTemplate.find(findCustomerQuery, Customer.class, CUSTOMER_COLLECTION);
    } else {
      Account account = accounts.get(0);

      customers = mongoTemplate
          .find(Query.query(Criteria.where("account.$id").is(new ObjectId(account.getId()))),
              Customer.class, CUSTOMER_COLLECTION);
    }

    if (customers.isEmpty()) {
      throw new UserNotFoundException("Customer not found", phone);
    }
    customer = customers.get(0);

    Query query = Query.query(
        Criteria.where("customer.$id").is(new ObjectId(customer.getId()))
            .and("status").is(CarRequestStatus.FINISHED))
        .with(Sort.by(Direction.DESC, "createdAt"));

    Set<String> addresses = new HashSet<>();
    return mongoTemplate
        .find(query, CarRequest.class, "carRequest")
        .parallelStream()
        .filter(c -> addresses.add(c.getPickingAddress()))
        .sorted(Comparator.comparing(CarRequest::getCreatedAt).reversed())
        .limit(5)
        .collect(Collectors.toList());
  }
}
