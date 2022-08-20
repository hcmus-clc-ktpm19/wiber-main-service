package com.hcmus.wiberback.model.exception;

import lombok.Getter;

@Getter
public class AccountNotFoundException extends AbstractNotFoundException {

  public AccountNotFoundException(String message, String phone) {
    super(message, phone);
  }
}
