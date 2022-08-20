package com.hcmus.wiberback.service;

import com.hcmus.wiberback.model.dto.CarRequestDto;
import com.hcmus.wiberback.model.entity.CarRequest;
import com.hcmus.wiberback.model.enums.CarRequestStatus;
import java.util.List;

public interface CarRequestService {

  List<CarRequest> findAllCarRequests();

  List<CarRequest> findLocatingCarRequests();

  CarRequest findCarRequestById(String id);

  List<CarRequest> findHistoryByCustomerPhone(String customerPhone);

  List<CarRequest> getCarRequestsByPhoneAndStatus(String phone, CarRequestStatus status);

  String saveOrUpdateCarRequest(CarRequestDto carRequestDto);

  String saveOrUpdateCarRequestCallCenter(CarRequestDto carRequestDto);

  String updateCarRequestArrivingAddress(CarRequestDto carRequestDto);
}
