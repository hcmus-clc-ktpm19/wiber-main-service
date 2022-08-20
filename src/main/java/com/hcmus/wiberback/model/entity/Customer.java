package com.hcmus.wiberback.model.entity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class Customer extends BaseEntity {

  @NotEmpty
  @NotBlank
  @TextIndexed
  private String name;

  private String phone;

  @DBRef
  private Account account;
}
