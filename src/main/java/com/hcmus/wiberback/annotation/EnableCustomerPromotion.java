package com.hcmus.wiberback.annotation;

import static com.hcmus.wiberback.model.enums.SenderServiceType.TWILIO;

import com.hcmus.wiberback.model.enums.SenderServiceType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface EnableCustomerPromotion {
  SenderServiceType value() default TWILIO;
}
