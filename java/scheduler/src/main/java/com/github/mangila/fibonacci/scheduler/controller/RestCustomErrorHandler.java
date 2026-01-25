package com.github.mangila.fibonacci.scheduler.controller;

import io.github.mangila.ensure4j.EnsureException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

import static com.github.mangila.fibonacci.scheduler.controller.RestGlobalErrorHandler.VALIDATION_ERROR_DETAIL;


@Order(0)
@RestControllerAdvice
public class RestCustomErrorHandler {

    private static final Logger log = LoggerFactory.getLogger(RestCustomErrorHandler.class);

    @ExceptionHandler(ConstraintViolationException.class)
    public ErrorResponse handleConstraintViolationException(ConstraintViolationException e) {
        log.error("ERR", e);
        Map<String, Object> errors = new HashMap<>();
        e.getConstraintViolations().forEach(constraintViolation -> {
            errors.put(constraintViolation.getPropertyPath().toString(), constraintViolation.getMessage());
        });
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, VALIDATION_ERROR_DETAIL);
        problemDetail.setProperty("errors", errors);
        return ErrorResponse.builder(e, problemDetail)
                .build();
    }

    @ExceptionHandler(EnsureException.class)
    public ErrorResponse handleEnsureException(EnsureException e) {
        log.error("ERR", e);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                e.getMessage()
        );
        return ErrorResponse.builder(e, problemDetail)
                .build();
    }
}
