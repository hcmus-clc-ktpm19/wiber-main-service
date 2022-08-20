package com.hcmus.wiberback.model.dto;

import com.hcmus.wiberback.model.enums.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DriverDto {
    private String id;
    private String phone;
    private String name;
    private String carType;
    private Role role;
}
