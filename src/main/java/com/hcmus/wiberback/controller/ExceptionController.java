package com.hcmus.wiberback.controller;

import com.hcmus.wiberback.model.exception.AbstractNotFoundException;
import com.hcmus.wiberback.model.exception.AccountExistedException;
import com.hcmus.wiberback.model.exception.AccountNotFoundException;
import com.hcmus.wiberback.model.exception.UserNotFoundException;
import java.util.HashMap;
import java.util.Map;
import javax.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionController {

  private static final String ERROR_MESSAGE = "Error-Message";

  @ExceptionHandler(AccountExistedException.class)
  public ResponseEntity<Map<String, String>> handleAccountExistedException(
      AccountExistedException e) {
    log.error(e.getMessage(), e);
    Map<String, String> response = new HashMap<>();
    response.put(ERROR_MESSAGE, e.getMessage() + ": " + e.getPhone());

    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
  }

  @ExceptionHandler(value = {UserNotFoundException.class, AccountNotFoundException.class})
  public ResponseEntity<Map<String, String>> handleUserNotFoundException(
      AbstractNotFoundException e) {
    log.error(e.getMessage(), e);
    Map<String, String> response = new HashMap<>();
    response.put(ERROR_MESSAGE, e.getMessage() + ": " + e.getIdentify());

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleValidationExceptions(
      MethodArgumentNotValidException ex) {

    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach(error -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });

    return ResponseEntity.badRequest().body(errors);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Map<String, String>> handleValidationExceptions(
      ConstraintViolationException ex) {

    Map<String, String> errors = new HashMap<>();
    ex.getConstraintViolations().forEach(error -> {
      String fieldName = error.getPropertyPath().toString();
      String errorMessage = error.getMessage();
      errors.put(fieldName, errorMessage);
    });

    return ResponseEntity.badRequest().body(errors);
  }

  @ExceptionHandler(Throwable.class)
  public ResponseEntity<Map<String, String>> handleTokenExpiredException(Throwable e) {
    String message = e.getMessage();
    log.error("Unexpected error: {}", message);

    return ResponseEntity
        .internalServerError()
        .body(Map.of(ERROR_MESSAGE, message));
  }
}