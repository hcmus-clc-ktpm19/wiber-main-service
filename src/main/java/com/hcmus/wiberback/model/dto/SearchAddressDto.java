package com.hcmus.wiberback.model.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SearchAddressDto {
  private String customerId;
  private List<String> addresses;
}
