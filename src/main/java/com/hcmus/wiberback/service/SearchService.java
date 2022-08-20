package com.hcmus.wiberback.service;

import com.hcmus.wiberback.model.entity.CarRequest;
import java.util.List;

public interface SearchService {

  List<CarRequest> searchAddress(String phone);
}
