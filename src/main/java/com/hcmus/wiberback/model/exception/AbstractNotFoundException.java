package com.hcmus.wiberback.model.exception;

import lombok.Getter;

@Getter
public abstract class AbstractNotFoundException extends RuntimeException {

  private final String identify;

  AbstractNotFoundException(String message, String identify) {
    super(message);
    this.identify = identify;
  }
}
