package com.hcmus.wiberback.model.entity;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.hcmus.wiberback.model.enums.CarRequestStatus;
import com.hcmus.wiberback.model.enums.CarType;
import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class CarRequest extends BaseEntity {

  @NotNull
  @DBRef
  private Customer customer;

  @DBRef
  private CallCenter callCenter;

  @DBRef
  private Driver driver;

  @NotEmpty
  @NotBlank
  @TextIndexed
  private String pickingAddress;

  private String arrivingAddress;

  private Double distance;

  private Double lngPickingAddress;

  private Double latPickingAddress;

  private Double lngArrivingAddress;

  private Double latArrivingAddress;

  private Double price;

  @NotNull
  private CarRequestStatus status;

  @NotNull
  private CarType carType;

  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @CreatedDate
  private LocalDateTime createdAt;

  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @LastModifiedDate
  private LocalDateTime updatedAt;
}
