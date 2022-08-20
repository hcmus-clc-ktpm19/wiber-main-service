package com.hcmus.wiberback.service.impl;

import com.hcmus.wiberback.service.CarRequestPub;
import lombok.AllArgsConstructor;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CarRequestPubImpl implements CarRequestPub {

  private final RabbitTemplate rabbitTemplate;

  @Override
  public void send(Queue queue, Object message) {
    rabbitTemplate.convertAndSend(queue.getName(), message);
  }
}
