package com.hcmus.wiberback.model.dto;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CustomerBookingRequestDto {
  @NotEmpty
  @NotBlank
  private String name;

  @NotEmpty
  @NotBlank
  @Digits(integer = 10, fraction = 0)
  private String phone;

  @NotEmpty
  @NotBlank
  private String customerId;

  @NotEmpty
  @NotBlank
  private String pickingAddress;

  @NotEmpty
  @NotBlank
  private String arrivingAddress;

  @NotNull
  private Object lngPickingAddress;

  @NotNull
  private Object latPickingAddress;

  @NotNull
  private Object lngArrivingAddress;

  @NotNull
  private Object latArrivingAddress;
}
