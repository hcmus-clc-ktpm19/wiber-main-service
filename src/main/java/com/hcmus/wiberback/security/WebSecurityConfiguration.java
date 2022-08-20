package com.hcmus.wiberback.security;

import com.hcmus.wiberback.model.enums.Role;
import com.hcmus.wiberback.model.enums.WiberUrl;
import com.hcmus.wiberback.security.filter.CustomAuthenticationFilter;
import com.hcmus.wiberback.security.filter.CustomAuthorizationFilter;
import com.hcmus.wiberback.util.AuthEntryPointJwt;
import com.hcmus.wiberback.util.JwtUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

  private static final String CUSTOMER_URL = WiberUrl.CUSTOMER_URL.url;
  private static final String DRIVER_URL = WiberUrl.DRIVER_URL.url;
  private static final String LOGIN_URL = WiberUrl.LOGIN_URL.url;
  private static final String LOGIN_URL_V2 = WiberUrl.LOGIN_URL_V2.url;
  private static final String REGISTER_URL = WiberUrl.REGISTER_URL.url;
  private static final String CALLCENTER_URL = WiberUrl.CALLCENTER_URL.url;

  @Qualifier("AccountServiceImpl")
  private final UserDetailsService userDetailsService;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;
  private final AuthEntryPointJwt unauthorizedHandler;
  public WebSecurityConfiguration(UserDetailsService userDetailsService,
      PasswordEncoder passwordEncoder, JwtUtil jwtUtil, AuthEntryPointJwt unauthorizedHandler) {
    this.userDetailsService = userDetailsService;
    this.passwordEncoder = passwordEncoder;
    this.jwtUtil = jwtUtil;
    this.unauthorizedHandler = unauthorizedHandler;
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
  }

  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    CustomAuthenticationFilter authenticationFilter = new CustomAuthenticationFilter(
        authenticationManager(),
        jwtUtil);
    authenticationFilter.setFilterProcessesUrl("/api/v1/auth/login");

    http.cors().and().csrf()
        .disable()
        .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    http.authorizeRequests()
        .antMatchers(LOGIN_URL, LOGIN_URL_V2, REGISTER_URL)
        .permitAll();

    // customer url config
    http.authorizeRequests().antMatchers(HttpMethod.GET, CUSTOMER_URL)
        .hasAnyAuthority(Role.CUSTOMER.name());
    http.authorizeRequests().antMatchers(HttpMethod.POST, CUSTOMER_URL)
        .hasAnyAuthority(Role.CUSTOMER.name());
    http.authorizeRequests().antMatchers(HttpMethod.PUT, CUSTOMER_URL)
        .hasAnyAuthority(Role.CUSTOMER.name());
    http.authorizeRequests().antMatchers(HttpMethod.PATCH, CUSTOMER_URL)
        .hasAnyAuthority(Role.CUSTOMER.name());

    // driver url config
    http.authorizeRequests().antMatchers(HttpMethod.GET, DRIVER_URL)
        .hasAnyAuthority(Role.DRIVER.name());
    http.authorizeRequests().antMatchers(HttpMethod.POST, DRIVER_URL)
        .hasAnyAuthority(Role.DRIVER.name());
    http.authorizeRequests().antMatchers(HttpMethod.PUT, DRIVER_URL)
        .hasAnyAuthority(Role.DRIVER.name());
    http.authorizeRequests().antMatchers(HttpMethod.PATCH, DRIVER_URL)
        .hasAnyAuthority(Role.DRIVER.name());

    // callcenter url config
    http.authorizeRequests().antMatchers(HttpMethod.GET, CALLCENTER_URL)
        .hasAnyAuthority(Role.CALLCENTER.name(), Role.LOCATORSTAFF.name(), Role.ADMIN.name());
    http.authorizeRequests().antMatchers(HttpMethod.POST, CALLCENTER_URL)
        .hasAnyAuthority(Role.CALLCENTER.name(), Role.LOCATORSTAFF.name(), Role.ADMIN.name());
    http.authorizeRequests().antMatchers(HttpMethod.PUT, CALLCENTER_URL)
        .hasAnyAuthority(Role.CALLCENTER.name(), Role.LOCATORSTAFF.name(), Role.ADMIN.name());
    http.authorizeRequests().antMatchers(HttpMethod.PATCH, CALLCENTER_URL)
        .hasAnyAuthority(Role.CALLCENTER.name(), Role.LOCATORSTAFF.name(), Role.ADMIN.name());

    http.authorizeRequests().anyRequest().authenticated();
    http.addFilter(authenticationFilter)
        .addFilterBefore(new CustomAuthorizationFilter(jwtUtil),
            UsernamePasswordAuthenticationFilter.class);
  }

}
