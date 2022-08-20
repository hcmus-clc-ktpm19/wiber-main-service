package com.hcmus.wiberback.service;

import com.hcmus.wiberback.model.dto.DriverDto;
import com.hcmus.wiberback.model.entity.Driver;
import java.util.List;

public interface DriverService {

  Driver findDriverById(String id);

  Driver findDriverByPhone(String phone);

  List<Driver> findAllDrivers();

  String saveDriver(DriverDto driverDto);

  String updateDriverNameById(String id, DriverDto driverDto);

  Driver saveOrUpdateDriver(DriverDto driverDto);
}
