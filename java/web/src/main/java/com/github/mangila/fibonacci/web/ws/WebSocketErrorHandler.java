package com.github.mangila.fibonacci.web.ws;

import io.github.mangila.ensure4j.EnsureException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@ControllerAdvice
public class WebSocketErrorHandler {

    private static final Logger log = LoggerFactory.getLogger(WebSocketErrorHandler.class);

    @MessageExceptionHandler(MethodArgumentNotValidException.class)
    @SendToUser("/queue/errors")
    public ProblemDetail handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error("ERR", ex);
        Map<String, List<String>> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())
                ));
        return buildValidationProblem(errors);
    }

    @MessageExceptionHandler(ConstraintViolationException.class)
    @SendToUser("/queue/errors")
    public ProblemDetail handleConstraintViolationException(ConstraintViolationException ex) {
        log.error("ERR", ex);
        Map<String, List<String>> errors = ex.getConstraintViolations().stream()
                .collect(Collectors.groupingBy(
                        violation -> violation.getPropertyPath().toString(),
                        Collectors.mapping(ConstraintViolation::getMessage, Collectors.toList())
                ));
        return buildValidationProblem(errors);
    }

    @MessageExceptionHandler(EnsureException.class)
    @SendToUser("/queue/errors")
    public ProblemDetail handleEnsureException(EnsureException ex) {
        log.error("ERR", ex);
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @MessageExceptionHandler(NoSuchElementException.class)
    @SendToUser("/queue/errors")
    public ProblemDetail handleNoSuchElementException(NoSuchElementException ex) {
        log.error("ERR", ex);
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "not found");
    }

    @MessageExceptionHandler(Exception.class)
    @SendToUser("/queue/errors")
    public ProblemDetail handleException(Exception ex) {
        log.error("ERR", ex);
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "something went wrong");
    }

    private static ProblemDetail buildValidationProblem(Map<String, List<String>> errors) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Invalid request");
        detail.setProperty("errors", errors);
        return detail;
    }
}
