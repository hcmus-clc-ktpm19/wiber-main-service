package com.hcmus.wiberback.model.dto;

import com.hcmus.wiberback.model.enums.Role;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountDto {

  private String id;

  @NotEmpty(message = "Phone number is required")
  @NotBlank(message = "Phone number must not be blank")
  @Size(min = 10, message = "Phone number must be 10 digits")
  private String phone;

  @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "Password must be at least 8 characters and contain at least one letter, one number and must not contain spaces")
  private String password;

  @NotNull(message = "Role is required")
  private Role role;
}
