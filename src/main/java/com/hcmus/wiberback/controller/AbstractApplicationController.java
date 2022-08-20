package com.hcmus.wiberback.controller;

import com.hcmus.wiberback.util.ApplicationMapper;
import org.springframework.beans.factory.annotation.Autowired;


public abstract class AbstractApplicationController {

  @Autowired
  protected ApplicationMapper mapper;
}
