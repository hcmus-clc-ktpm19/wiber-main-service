package com.hcmus.wiberback.model.enums;

public enum WiberUrl {
  LOGIN_URL("/api/v1/auth/login"),
  LOGIN_URL_V2("/api/v1/auth/signin"),

  REGISTER_URL("/api/v1/auth/register"),
  CUSTOMER_URL("/api/v1/customer/**"),
  DRIVER_URL("/api/v1/driver/**"),
  CALLCENTER_URL("/api/v1/callcenter/**");

  public final String url;

  WiberUrl(String url) {
    this.url = url;
  }
}
