package com.example.userAPI.ExeptionHandler;

import com.example.userAPI.exeptions.ApiException;
import com.example.userAPI.exeptions.UserNotExistException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = {UserNotExistException.class})
    protected ResponseEntity<Object> handleApiException(UserNotExistException e) {
        ApiException apiExeption = new ApiException(
                e.getMessage(),
                HttpStatus.BAD_REQUEST,
                ZonedDateTime.now(ZoneId.of("Z")));

        return new ResponseEntity<>(apiExeption, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {ConstraintViolationException.class })
    protected ResponseEntity<Object> handleConstraintViolation(
            ConstraintViolationException ex) {
        List<String> errors = new ArrayList<>();
        ex.getConstraintViolations().forEach(violation -> errors.add(violation.getMessage()));
            ApiException apiExeption = new ApiException(
                    errors.toString(),
                    HttpStatus.BAD_REQUEST,
                    ZonedDateTime.now(ZoneId.of("Z")));

            return new ResponseEntity<>(apiExeption, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {SQLIntegrityConstraintViolationException.class })
    protected ResponseEntity<Object> handleConstraintViolation() {
        ApiException apiExeption = new ApiException(
                "Email already in use",
                HttpStatus.BAD_REQUEST,
                ZonedDateTime.now(ZoneId.of("Z")));

        return new ResponseEntity<>(apiExeption, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {DateTimeException.class })
    protected ResponseEntity<Object> handleConstraintViolation(DateTimeException ex) {
        ApiException apiException = new ApiException(
                ex.getLocalizedMessage(),
                HttpStatus.BAD_REQUEST,
                ZonedDateTime.now(ZoneId.of("Z")));

        return new ResponseEntity<>(apiException, HttpStatus.BAD_REQUEST);
    }

}