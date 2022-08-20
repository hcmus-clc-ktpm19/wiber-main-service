package com.hcmus.wiberback.controller;

import com.hcmus.wiberback.model.dto.DriverDto;
import com.hcmus.wiberback.service.DriverService;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/driver")
public class DriverController extends AbstractApplicationController {

  private final DriverService driverService;

  @GetMapping("/all")
  public ResponseEntity<List<DriverDto>> findAllDrivers() {
    return ResponseEntity.ok(driverService.findAllDrivers().stream()
        .map(mapper::toDriverDto)
        .collect(Collectors.toList()));
  }

  @GetMapping
  public ResponseEntity<DriverDto> findDriverById(@RequestParam String q) {
    return ResponseEntity.ok(mapper.toDriverDto(driverService.findDriverByPhone(q)));
  }

  @GetMapping("/{phone}")
  public ResponseEntity<DriverDto> findCustomerByPhone(@PathVariable String phone) {
    return ResponseEntity.ok(mapper.toDriverDto(driverService.findDriverByPhone(phone)));
  }

  @PostMapping("/create-or-update")
  public ResponseEntity<String> saveOrUpdateAccount(
          @Valid @RequestBody DriverDto driverDto) {
    return ResponseEntity.ok(driverService.saveOrUpdateDriver(driverDto).getId());
  }

  @PostMapping
  public ResponseEntity<String> saveAccount(@RequestBody DriverDto driverDto) {
    return ResponseEntity.ok(driverService.saveDriver(driverDto));
  }

  @PutMapping("/{id}")
  public ResponseEntity<String> updateDriverNameById(@PathVariable String id, @RequestBody DriverDto driverDto) {
    return ResponseEntity.ok(driverService.updateDriverNameById(id, driverDto));
  }
}
