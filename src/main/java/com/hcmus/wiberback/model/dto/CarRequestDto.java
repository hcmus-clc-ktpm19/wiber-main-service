package com.hcmus.wiberback.model.dto;

import com.hcmus.wiberback.model.enums.CarRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CarRequestDto {

  private String id;
  private String customerId;
  private String customerName;
  private String driverId;
  private String driverName;
  private String driverPhone;
  private String callCenterId;
  private String arrivingAddress;
  private Double lngArrivingAddress;
  private Double latArrivingAddress;
  private Double lngPickingAddress;
  private Double latPickingAddress;
  private String customerPhone;
  private String pickingAddress;
  private String carType;
  private CarRequestStatus status;
  private Double price;
  private Double distance;
}
