package com.hcmus.wiberback.repository.custom;

import com.hcmus.wiberback.model.entity.CarRequest;
import com.hcmus.wiberback.model.enums.CarRequestStatus;
import java.util.List;

public interface CarRequestRepositoryCustom {

  List<CarRequest> getCarRequestsByPhoneAndStatus(String phone, CarRequestStatus status);

  List<CarRequest> searchAddress(String phone);
}
