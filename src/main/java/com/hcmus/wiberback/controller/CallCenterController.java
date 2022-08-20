package com.hcmus.wiberback.controller;

import com.hcmus.wiberback.model.dto.CallCenterDto;
import com.hcmus.wiberback.model.dto.SearchAddressDto;
import com.hcmus.wiberback.model.entity.CallCenter;
import com.hcmus.wiberback.service.CallCenterService;
import com.hcmus.wiberback.service.SearchService;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/callcenter")
public class CallCenterController extends AbstractApplicationController {

  private final CallCenterService callCenterService;
  private final SearchService searchService;

  @GetMapping("/all")
  public ResponseEntity<List<CallCenterDto>> getAllCallCenter() {
    return ResponseEntity.ok(callCenterService.getAllCallCenter().stream()
        .map(mapper::toCallCenterDto)
        .collect(Collectors.toList()));
  }

  @GetMapping("/user/{id}")
  public ResponseEntity<CallCenterDto> getCallCenterById(@PathVariable String id) {
    return ResponseEntity.ok(mapper.toCallCenterDto(callCenterService.findCallCenterById(id)));
  }

  @GetMapping("/user/phone/{phone}")
  public ResponseEntity<CallCenterDto> getCallCenterByPhone(@PathVariable String phone) {
    return ResponseEntity.ok(
        mapper.toCallCenterDto(callCenterService.findCallCenterByPhone(phone)));
  }

  @GetMapping("/search-address")
  public ResponseEntity<SearchAddressDto> searchAddress(@RequestParam String phone) {
    return ResponseEntity.ok(mapper.toSearchAddressDto(phone, searchService.searchAddress(phone)));
  }

  @PostMapping
  public ResponseEntity<CallCenter> saveAccount(
      @Valid @RequestBody CallCenterDto callCenterDto) {
    return ResponseEntity.ok(callCenterService.saveOrUpdateCallCenter(callCenterDto));
  }

  @DeleteMapping("/user/{id}")
  public ResponseEntity<String> deleteCallCenter(@PathVariable String id) {
    callCenterService.deleteCallCenter(id);
    return ResponseEntity.ok("Delete call center success");
  }
}
