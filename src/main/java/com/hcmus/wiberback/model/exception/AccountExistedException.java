package com.hcmus.wiberback.model.exception;

import com.hcmus.wiberback.model.enums.Role;
import lombok.Getter;

@Getter
public class AccountExistedException extends RuntimeException {

  private final String phone;
  private final Role role;

  public AccountExistedException(String message, String phone, Role role) {
    super(message);
    this.phone = phone;
    this.role = role;
  }
}
