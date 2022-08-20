package com.hcmus.wiberback.model.dto;

import com.hcmus.wiberback.model.enums.SenderServiceType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageDto {
  private CarRequestDto carRequestDto;
  private String content;
  private SenderServiceType senderServiceType;
}
