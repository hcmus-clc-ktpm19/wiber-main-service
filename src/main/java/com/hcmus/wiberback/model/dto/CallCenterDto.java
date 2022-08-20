package com.hcmus.wiberback.model.dto;

import com.hcmus.wiberback.model.enums.Role;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CallCenterDto {
  private String id;
  private String name;

  @NotEmpty
  @NotBlank
  @Size(min = 10, max = 10, message = "Phone number must be 10 digits")
  private String phone;

  private Role role;

}
