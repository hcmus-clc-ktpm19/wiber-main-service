package com.hcmus.wiberback.service;

import org.springframework.amqp.core.Queue;

public interface CarRequestPub {
  void send(Queue queue, Object message);
}
