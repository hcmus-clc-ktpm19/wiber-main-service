package com.hcmus.wiberback.service;

import com.hcmus.wiberback.model.dto.CallCenterDto;
import com.hcmus.wiberback.model.entity.CallCenter;
import java.util.List;

public interface CallCenterService {

  List<CallCenter> getAllCallCenter();

  CallCenter saveOrUpdateCallCenter(CallCenterDto callCenterDto);

  CallCenter findCallCenterByPhone(String phone);

  CallCenter findCallCenterById(String id);

  void deleteCallCenter(String id);
}
