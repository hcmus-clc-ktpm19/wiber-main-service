package com.hcmus.wiberback.model.dto;

import com.hcmus.wiberback.model.enums.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerDto {
  private String id;
  private String name;
  private String phone;
  private Role role;
}
