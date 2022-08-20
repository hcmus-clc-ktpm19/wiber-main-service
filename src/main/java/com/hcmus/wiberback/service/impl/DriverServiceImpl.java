package com.hcmus.wiberback.service.impl;

import com.hcmus.wiberback.model.dto.DriverDto;
import com.hcmus.wiberback.model.entity.Account;
import com.hcmus.wiberback.model.entity.Driver;
import com.hcmus.wiberback.model.exception.AccountNotFoundException;
import com.hcmus.wiberback.model.exception.UserNotFoundException;
import com.hcmus.wiberback.repository.AccountRepository;
import com.hcmus.wiberback.repository.DriverRepository;
import com.hcmus.wiberback.service.DriverService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DriverServiceImpl implements DriverService {

  private final DriverRepository driverRepository;
  private final AccountRepository accountRepository;

  @Override
  public List<Driver> findAllDrivers() {
    return driverRepository.findAll();
  }

  @Override
  public Driver findDriverById(String id) {
    return driverRepository
        .findById(id)
        .orElseThrow(() -> new AccountNotFoundException("Driver not found", id));
  }

  @Override
  @Cacheable(value = "driver", key = "#phone", unless = "#result == null", cacheManager = "userCacheManager")
  public Driver findDriverByPhone(String phone) {
    Account account = accountRepository.findAccountByPhone(phone).orElseThrow(
        () -> new AccountNotFoundException("Account with phone number not found", phone));
    return driverRepository.findDriverByAccount(account).orElseThrow(
        () -> new UserNotFoundException("Driver with phone number not found", phone));
  }

  @Override
  public String saveDriver(DriverDto driverDto) {
    Account account = accountRepository
        .findAccountByPhone(driverDto.getPhone())
        .orElseThrow(() -> new AccountNotFoundException("Account not found",
            driverDto.getPhone()));
    Driver driver = new Driver();
    driver.setName(driverDto.getName());
    driver.setCarType(driverDto.getCarType());
    driver.setAccount(account);
    return driverRepository.save(driver).getId();
  }

  @Override
  public String updateDriverNameById(String id, DriverDto driverDto) {
    Driver updateDriver = driverRepository
        .findById(id)
        .orElseThrow(() -> new AccountNotFoundException("Driver not found", id));
    // driver request dto include phone and name => allow to update name
    if (driverDto.getName() != null) {
      updateDriver.setName(driverDto.getName());
    }
    return driverRepository.save(updateDriver).getId();
  }

  @Override
  @CachePut(value = "driver", key = "#driverDto.phone", unless = "#result == null", cacheManager = "userCacheManager")
  public Driver saveOrUpdateDriver(DriverDto driverDto) {
    Account account = accountRepository
        .findAccountByPhone(driverDto.getPhone())
        .orElseThrow(() -> new AccountNotFoundException("Account not found",
            driverDto.getPhone()));

    Driver driver;
    if (driverRepository.existsByAccount(account)) {
      driver = findDriverByPhone(driverDto.getPhone());
      driver.setName(driverDto.getName());
      driver.setCarType(driverDto.getCarType());
    } else {
      driver = new Driver();
      driver.setName(driverDto.getName());
      driver.setAccount(account);
      driver.setCarType(driverDto.getCarType());
    }

    return driverRepository.save(driver);
  }
}
