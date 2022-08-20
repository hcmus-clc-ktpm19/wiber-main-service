package com.hcmus.wiberback.service.impl;

import com.hcmus.wiberback.model.dto.CustomerDto;
import com.hcmus.wiberback.model.entity.Account;
import com.hcmus.wiberback.model.entity.Customer;
import com.hcmus.wiberback.model.exception.AccountNotFoundException;
import com.hcmus.wiberback.model.exception.UserNotFoundException;
import com.hcmus.wiberback.repository.AccountRepository;
import com.hcmus.wiberback.repository.CustomerRepository;
import com.hcmus.wiberback.service.CustomerService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomerServiceImpl implements CustomerService {

  private final CustomerRepository customerRepository;
  private final AccountRepository accountRepository;

  @Override
  public List<Customer> getAllCustomers() {
    return customerRepository.findAll();
  }

  @Override
  public Customer findCustomerById(String id) {
    return customerRepository
        .findById(id)
        .orElseThrow(() -> new UserNotFoundException("Customer with id not found", id));
  }

  @Override
  @Cacheable(value = "customer", key = "#phone", unless = "#result == null", cacheManager = "userCacheManager")
  public Customer findCustomerByPhone(String phone) {
    Account account = accountRepository.findAccountByPhone(phone).orElseThrow(
        () -> new AccountNotFoundException("Account with phone number not found", phone));

    return customerRepository.findCustomerByAccount(account).orElseThrow(
        () -> new UserNotFoundException("Customer with phone number not found", phone));
  }

  @Override
  @CachePut(value = "customer", key = "#customerDto.phone", unless = "#result == null", cacheManager = "userCacheManager")
  public Customer saveOrUpdateCustomer(CustomerDto customerDto) {
    Account account = accountRepository
        .findAccountByPhone(customerDto.getPhone())
        .orElseThrow(() -> new AccountNotFoundException("Account not found",
            customerDto.getPhone()));

    Customer customer;
    if (customerRepository.existsByAccount(account)) {
      customer = findCustomerByPhone(customerDto.getPhone());
      customer.setName(customerDto.getName());
    } else {
      customer = new Customer();
      customer.setName(customerDto.getName());
      customer.setAccount(account);
    }

    return customerRepository.save(customer);
  }
}
