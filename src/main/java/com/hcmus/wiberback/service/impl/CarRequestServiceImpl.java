package com.hcmus.wiberback.service.impl;

import static com.hcmus.wiberback.model.exception.UserNotFoundException.*;

import com.hcmus.wiberback.annotation.EnableCustomerPromotion;
import com.hcmus.wiberback.model.dto.CarRequestDto;
import com.hcmus.wiberback.model.entity.CallCenter;
import com.hcmus.wiberback.model.entity.CarRequest;
import com.hcmus.wiberback.model.entity.Customer;
import com.hcmus.wiberback.model.entity.Driver;
import com.hcmus.wiberback.model.enums.CarRequestStatus;
import com.hcmus.wiberback.model.enums.CarType;
import com.hcmus.wiberback.model.exception.AccountNotFoundException;
import com.hcmus.wiberback.model.exception.CarRequestNotFoundException;
import com.hcmus.wiberback.model.exception.UserNotFoundException;
import com.hcmus.wiberback.repository.AccountRepository;
import com.hcmus.wiberback.repository.CallCenterRepository;
import com.hcmus.wiberback.repository.CarRequestRepository;
import com.hcmus.wiberback.repository.CustomerRepository;
import com.hcmus.wiberback.repository.DriverRepository;
import com.hcmus.wiberback.service.CarRequestPub;
import com.hcmus.wiberback.service.CarRequestService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarRequestServiceImpl implements CarRequestService {

  private final CarRequestRepository carRequestRepository;
  private final CustomerRepository customerRepository;
  private final CallCenterRepository callCenterRepository;
  private final DriverRepository driverRepository;
  private final AccountRepository accountRepository;
  private final CarRequestPub carRequestPub;
  @Qualifier("carRequestQueue")
  private final Queue carRequestQueue;
  @Qualifier("carRequestStatusQueue")
  private final Queue carRequestStatusQueue;
  @Qualifier("locateRequestQueue")
  private final Queue locateRequestQueue;

  @Override
  public List<CarRequest> findAllCarRequests() {
    return carRequestRepository.findAll();
  }

  @Override
  public List<CarRequest> findLocatingCarRequests() {
    return carRequestRepository.findCarRequestByStatus(CarRequestStatus.LOCATING);
  }

  @Override
  public CarRequest findCarRequestById(String id) {
    return carRequestRepository.findById(id)
        .orElseThrow(() -> new CarRequestNotFoundException("Car request not found", id));
  }

  @Override
  public List<CarRequest> findHistoryByCustomerPhone(String customerPhone) {
    return carRequestRepository.findCarRequestsByCustomerAndStatus(
        customerRepository
            .findCustomerByPhone(customerPhone)
            .orElseGet(() -> customerRepository
                .findCustomerByAccount(accountRepository
                    .findAccountByPhone(customerPhone)
                    .orElseThrow(
                        () -> new AccountNotFoundException("Account not found", customerPhone))
                ).orElseThrow(
                    () -> new UserNotFoundException(CUSTOMER_NOT_FOUND_MESSAGE, customerPhone))),
        CarRequestStatus.FINISHED);
  }

  @Override
  public String updateCarRequestArrivingAddress(CarRequestDto carRequestDto) {
    CarRequest carRequest = carRequestRepository
        .findById(carRequestDto.getId())
        .orElseThrow(() -> new CarRequestNotFoundException("Car request not found", carRequestDto.getId()));
    carRequest.setArrivingAddress(carRequestDto.getArrivingAddress());
    carRequest.setLngArrivingAddress(carRequestDto.getLngArrivingAddress());
    carRequest.setLatArrivingAddress(carRequestDto.getLatArrivingAddress());
    carRequest.setPrice(carRequestDto.getPrice());
    carRequest.setDistance(carRequestDto.getDistance());
    return carRequestRepository.save(carRequest).getId();
  }

  @Override
  public List<CarRequest> getCarRequestsByPhoneAndStatus(String phone, CarRequestStatus status) {
    return carRequestRepository.getCarRequestsByPhoneAndStatus(phone, status);
  }

  @Override
  @EnableCustomerPromotion
  public String saveOrUpdateCarRequest(CarRequestDto carRequestDto) {
    Customer customer;
    if (carRequestDto.getCustomerId() != null) {
      customer = customerRepository
          .findById(carRequestDto.getCustomerId())
          .orElseThrow(
              () -> new UserNotFoundException(CUSTOMER_NOT_FOUND_MESSAGE, carRequestDto.getCustomerId()));
    } else {
      customer = customerRepository.findCustomerByPhone(carRequestDto.getCustomerPhone()).orElseGet(
          () -> customerRepository
              .findCustomerByAccount(
                  accountRepository
                      .findAccountByPhone(carRequestDto.getCustomerPhone())
                      .orElseThrow(() -> new AccountNotFoundException("Account not found",
                          carRequestDto.getCustomerPhone())))
              .orElseThrow(() -> new UserNotFoundException(CUSTOMER_NOT_FOUND_MESSAGE,
                  carRequestDto.getCustomerPhone()))
      );
    }

    CarRequest carRequest;
    if (carRequestDto.getId() == null) {
      carRequest = CarRequest.builder()
          .customer(customer)
          .pickingAddress(carRequestDto.getPickingAddress())
          .arrivingAddress(carRequestDto.getArrivingAddress())
          .lngPickingAddress(carRequestDto.getLngPickingAddress())
          .latPickingAddress(carRequestDto.getLatPickingAddress())
          .lngArrivingAddress(carRequestDto.getLngArrivingAddress())
          .latArrivingAddress(carRequestDto.getLatArrivingAddress())
          .status(carRequestDto.getStatus())
          .price(carRequestDto.getPrice())
          .distance(carRequestDto.getDistance())
          .carType(CarType.valueOf(carRequestDto.getCarType())).build();
    } else {
      carRequest = carRequestRepository.findById(carRequestDto.getId())
          .orElseThrow(() -> new CarRequestNotFoundException("Car request not found",
              carRequestDto.getId()));
      Driver driver = driverRepository.findById(carRequestDto.getDriverId())
          .orElseThrow(
              () -> new UserNotFoundException("Driver not found", carRequestDto.getDriverId()));
      carRequest.setDriver(driver);
      carRequest.setStatus(carRequestDto.getStatus());
    }
    String carRequestId = carRequestRepository.save(carRequest).getId();

    // create a new car request to car-request-queue
    carRequestDto.setId(carRequestId);
    if (carRequestDto.getStatus() == CarRequestStatus.WAITING) {
      carRequestPub.send(carRequestQueue, carRequestDto);
    } else if (carRequestDto.getStatus() == CarRequestStatus.ACCEPTED) {
      carRequestPub.send(carRequestStatusQueue, carRequestDto);
    }

    return carRequestId;
  }

  @Override
  public String saveOrUpdateCarRequestCallCenter(CarRequestDto carRequestDto) {
    CallCenter callCenter = callCenterRepository
        .findById(carRequestDto.getCallCenterId())
        .orElseThrow(
            () -> new UserNotFoundException("Call-center not found",
                carRequestDto.getCallCenterId()));

    Customer customer = customerRepository.findCustomerByPhone(carRequestDto.getCustomerPhone())
        .orElseGet(() -> {
          Customer newCustomer = new Customer();
          newCustomer.setName(carRequestDto.getCustomerName());
          newCustomer.setPhone(carRequestDto.getCustomerPhone());
          return customerRepository.save(newCustomer);
        });

    List<CarRequest> oldCarRequests = carRequestRepository.findCarRequestByPickingAddress(carRequestDto.getPickingAddress());
    for (CarRequest carRequest : oldCarRequests) {
      if (carRequest.getLngPickingAddress() != null && carRequest.getLatPickingAddress() != null) {
        carRequestDto.setLngPickingAddress(carRequest.getLngPickingAddress());
        carRequestDto.setLatPickingAddress(carRequest.getLatPickingAddress());
        break;
      }
    }

    CarRequest carRequest;
    if (carRequestDto.getId() == null) {
      if (carRequestDto.getLngPickingAddress() != null
          && carRequestDto.getLatPickingAddress() != null) {
        carRequest = CarRequest.builder()
            .customer(customer)
            .callCenter(callCenter)
            .pickingAddress(carRequestDto.getPickingAddress())
            .lngPickingAddress(carRequestDto.getLngPickingAddress())
            .latPickingAddress(carRequestDto.getLatPickingAddress())
            .status(CarRequestStatus.WAITING)
            .carType(CarType.valueOf(carRequestDto.getCarType())).build();

        carRequestRepository.save(carRequest);
        carRequestDto.setId(carRequest.getId());
        carRequestPub.send(carRequestQueue, carRequestDto);
      } else {
        carRequest = CarRequest.builder()
            .customer(customer)
            .callCenter(callCenter)
            .pickingAddress(carRequestDto.getPickingAddress())
            .status(carRequestDto.getStatus())
            .carType(CarType.valueOf(carRequestDto.getCarType())).build();
        carRequestRepository.save(carRequest);
        carRequestPub.send(locateRequestQueue, carRequestDto);
      }
    } else {
      carRequest = carRequestRepository.findById(carRequestDto.getId())
          .orElseThrow(() -> new CarRequestNotFoundException("Car request not found",
              carRequestDto.getId()));
      carRequest.setLngPickingAddress(carRequestDto.getLngPickingAddress());
      carRequest.setLatPickingAddress(carRequestDto.getLatPickingAddress());
      carRequest.setStatus(CarRequestStatus.WAITING);
      carRequestRepository.save(carRequest);
      carRequestPub.send(carRequestQueue, carRequestDto);
    }
    return carRequest.getId();
  }
}
