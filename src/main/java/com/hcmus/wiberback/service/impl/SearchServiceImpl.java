package com.hcmus.wiberback.service.impl;

import com.hcmus.wiberback.model.entity.CarRequest;
import com.hcmus.wiberback.repository.CarRequestRepository;
import com.hcmus.wiberback.service.SearchService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

  private final CarRequestRepository carRequestRepository;

  @Override
  @Cacheable(cacheNames = "search-address", key = "#phone", unless = "#result.empty")
  public List<CarRequest> searchAddress(String phone) {
    return carRequestRepository.searchAddress(phone);
  }
}
