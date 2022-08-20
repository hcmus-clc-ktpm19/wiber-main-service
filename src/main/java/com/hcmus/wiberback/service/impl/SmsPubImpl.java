package com.hcmus.wiberback.service.impl;

import com.hcmus.wiberback.service.SmsPub;
import lombok.AllArgsConstructor;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SmsPubImpl implements SmsPub {

  private final Queue smsQueue;
  private final RabbitTemplate rabbitTemplate;

  @Override
  public void send(Object message) {
    rabbitTemplate.convertAndSend(smsQueue.getName(), message);
  }
}
