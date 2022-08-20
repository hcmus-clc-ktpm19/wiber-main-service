package com.hcmus.wiberback.model.exception;

import lombok.Getter;

@Getter
public class CarRequestNotFoundException extends AbstractNotFoundException {

  private final String carRequestId;

  public CarRequestNotFoundException(String message, String identify) {
    super(message, identify);
    this.carRequestId = identify;
  }
}
