package com.hcmus.wiberback.aop;

import com.hcmus.wiberback.annotation.EnableCustomerPromotion;
import com.hcmus.wiberback.model.dto.CarRequestDto;
import com.hcmus.wiberback.model.dto.MessageDto;
import com.hcmus.wiberback.model.entity.Account;
import com.hcmus.wiberback.model.entity.CarRequest;
import com.hcmus.wiberback.model.enums.CarRequestStatus;
import com.hcmus.wiberback.model.enums.Role;
import com.hcmus.wiberback.model.enums.SenderServiceType;
import com.hcmus.wiberback.repository.AccountRepository;
import com.hcmus.wiberback.service.AccountService;
import com.hcmus.wiberback.service.CarRequestService;
import com.hcmus.wiberback.service.SmsPub;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Order(1)
@Aspect
@Component
@RequiredArgsConstructor
public class CustomerAspect {

  private final CarRequestService carRequestService;

  private final AccountService accountService;
  private final AccountRepository accountRepository;

  private final SmsPub smsPub;

  @Pointcut("@annotation(com.hcmus.wiberback.annotation.EnableCustomerPromotion)")
  private void enableCustomerPromotionAnnotation() { /* Pointcut's alias */ }

  @AfterReturning("enableCustomerPromotionAnnotation()")
  public void promoteVip(JoinPoint joinPoint) {
    log.info("After execute {}", joinPoint.getSignature().getName());

    CarRequestDto carRequestDto = (CarRequestDto) joinPoint.getArgs()[0];
    log.info("Check if customer {} can promote vip", carRequestDto.getCustomerId());
    Account account = accountRepository.findAccountByPhone(carRequestDto.getCustomerPhone()).orElse(null);
    if (account != null) {
      if (carRequestDto.getStatus() == CarRequestStatus.FINISHED && account.getRole() == Role.CUSTOMER) {
        double sum = carRequestService
            .getCarRequestsByPhoneAndStatus(carRequestDto.getCustomerPhone(), CarRequestStatus.FINISHED)
            .stream()
            .mapToDouble(CarRequest::getPrice)
            .sum();

        if (sum >= 500_000.0) {
          account.setRole(Role.VIP_CUSTOMER);
          accountService.saveAccount(account);

          MethodSignature signature = (MethodSignature) joinPoint.getSignature();
          SenderServiceType senderType = signature
              .getMethod()
              .getAnnotation(EnableCustomerPromotion.class)
              .value();
          log.info("EnableCustomerPromotion annotation value: {}", senderType);

          MessageDto messageDto = MessageDto.builder()
              .carRequestDto(carRequestDto)
              .content("CONGRATULATIONS! You are now a VIP customer!. "
                  + "From now on, your next service will be discounted by 10% and will receive some extra services.")
              .senderServiceType(senderType)
              .build();

          smsPub.send(messageDto);
        }
      }
    }
  }
}
