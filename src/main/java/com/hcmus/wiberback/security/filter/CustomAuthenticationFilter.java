package com.hcmus.wiberback.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcmus.wiberback.util.JwtUtil;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Slf4j
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  private final AuthenticationManager authenticationManager;
  private final JwtUtil jwtUtil;


  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
      HttpServletResponse response) throws AuthenticationException {
    String phone = request.getParameter("phone");
    String password = request.getParameter("password");

    UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(phone,
        password);
    return authenticationManager.authenticate(token);
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain, Authentication authentication) throws IOException {

    User user = (User) authentication.getPrincipal();

    // Access token expires in 8 hour
    String accessToken = jwtUtil.generateAccessToken(user, request.getRequestURL().toString());

    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    new ObjectMapper().writeValue(response.getOutputStream(), Map.of("Access-Token", accessToken));
  }

  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request,
      HttpServletResponse response, AuthenticationException failed) throws IOException {
    log.error("Authentication failed: {}", failed.getMessage());

    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    new ObjectMapper().writeValue(response.getOutputStream(),
        Map.of("Timestamp", LocalDateTime.now().toString(),
            "Error-Message", failed.getMessage()));
  }
}