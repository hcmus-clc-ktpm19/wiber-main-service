package com.hcmus.wiberback.model.exception;

import lombok.Getter;

@Getter
public class UserNotFoundException extends AbstractNotFoundException {

  public static final String CUSTOMER_NOT_FOUND_MESSAGE = "Customer not found";

  public UserNotFoundException(String message, String id) {
    super(message, id);
  }
}
