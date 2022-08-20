package com.hcmus.wiberback.controller;

import com.hcmus.wiberback.model.dto.CarRequestDto;
import com.hcmus.wiberback.model.entity.CarRequest;
import com.hcmus.wiberback.service.CarRequestService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/car-request")
public class CarRequestController extends AbstractApplicationController {

  private final CarRequestService carRequestService;

  @PreAuthorize("hasAnyRole('ADMIN', 'CALLCENTER')")
  @GetMapping("/all")
  public ResponseEntity<List<CarRequestDto>> findAllCarRequests() {
    return ResponseEntity.ok(carRequestService.findAllCarRequests().stream()
        .map(mapper::toCarRequestDto)
        .collect(Collectors.toList()));
  }

  @GetMapping("/callcenter/locating")
  public ResponseEntity<List<CarRequestDto>> findLocatingCarRequests() {
    return ResponseEntity.ok(carRequestService.findLocatingCarRequests().stream()
        .map(mapper::toCarRequestDto)
        .collect(Collectors.toList()));
  }

  @GetMapping("/{id}")
  public ResponseEntity<CarRequestDto> findCarRequestById(@PathVariable String id) {
    return ResponseEntity.ok(mapper.toCarRequestDto(carRequestService.findCarRequestById(id)));
  }

  @GetMapping("/{customer-phone}/history")
  public ResponseEntity<List<CarRequestDto>> findHistoryByCustomerPhone(
      @PathVariable("customer-phone") String customerPhone) {
    return ResponseEntity.ok(
        carRequestService.findHistoryByCustomerPhone(customerPhone).stream()
            .map(mapper::toCarRequestDto)
            .collect(Collectors.toList()));
  }

  @PostMapping("/create-or-update")
  public ResponseEntity<String> saveOrUpdateCarRequest(@RequestBody CarRequestDto carRequestDto) {
    String id = carRequestService.saveOrUpdateCarRequest(carRequestDto);
    return ResponseEntity.ok(id);
  }

  @PostMapping("/update-arriving-address")
  public ResponseEntity<String> updateCarRequestArrivingAddress(@RequestBody CarRequestDto carRequestDto) {
    String id = carRequestService.updateCarRequestArrivingAddress(carRequestDto);
    return ResponseEntity.ok(id);
  }

  @PostMapping("/callcenter/create-or-update")
  public ResponseEntity<CarRequest> saveOrUpdateCarRequestCallCenter(@RequestBody CarRequestDto carRequestDto) {
    String id = carRequestService.saveOrUpdateCarRequestCallCenter(carRequestDto);
    CarRequest result = carRequestService.findCarRequestById(id);
    return ResponseEntity.ok(result);
  }
}
