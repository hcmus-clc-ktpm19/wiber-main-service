package com.hcmus.wiberback.service.impl;

import com.hcmus.wiberback.model.dto.CallCenterDto;
import com.hcmus.wiberback.model.entity.Account;
import com.hcmus.wiberback.model.entity.CallCenter;
import com.hcmus.wiberback.model.exception.AccountNotFoundException;
import com.hcmus.wiberback.model.exception.UserNotFoundException;
import com.hcmus.wiberback.repository.AccountRepository;
import com.hcmus.wiberback.repository.CallCenterRepository;
import com.hcmus.wiberback.service.CallCenterService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CallCenterServiceImpl implements CallCenterService {

  private final CallCenterRepository callCenterRepository;
  private final AccountRepository accountRepository;

  @Override
  public List<CallCenter> getAllCallCenter() {
    return callCenterRepository.findAll();
  }

  @Override
  public CallCenter saveOrUpdateCallCenter(CallCenterDto callCenterDto) {
    Account account =
        accountRepository
            .findAccountByPhone(callCenterDto.getPhone())
            .orElseThrow(() -> new AccountNotFoundException("Account not found",
                callCenterDto.getPhone()));

    CallCenter callCenter;
    if (callCenterRepository.existsByAccount(account)) {
      callCenter = findCallCenterByPhone(callCenterDto.getPhone());
      callCenter.setName(callCenterDto.getName());
    } else {
      callCenter = new CallCenter();
      callCenter.setName(callCenterDto.getName());
      callCenter.setAccount(account);
    }

    return callCenterRepository.save(callCenter);
  }

  @Override
  public CallCenter findCallCenterById(String id) {
    return callCenterRepository
        .findById(id)
        .orElseThrow(() -> new UserNotFoundException("CallCenter with id not found", id));
  }

  @Override
  public CallCenter findCallCenterByPhone(String phone) {
    Account account = accountRepository.findAccountByPhone(phone).orElseThrow(
        () -> new AccountNotFoundException("Account with phone number not found", phone));

    return callCenterRepository.findCallCenterByAccount(account).orElseThrow(
        () -> new UserNotFoundException("CallCenter with phone number not found", phone));
  }

  @Override
  public void deleteCallCenter(String id) {
    callCenterRepository.deleteById(id);
  }
}
