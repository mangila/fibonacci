package com.github.mangila.fibonacci.web.sse;

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class RestGlobalErrorHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(RestGlobalErrorHandler.class);

    public static final String VALIDATION_ERROR_DETAIL = "Validation Failed";
    public static final String INTERNAL_SERVER_ERROR_DETAIL = "Internal Server Error";

    @GetMapping("favicon.ico")
    @ResponseBody
    void doNothing() {
        // do nothing
    }

    @Override
    protected @Nullable ResponseEntity<Object> handleHandlerMethodValidationException(HandlerMethodValidationException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.error("ERR", ex);
        Map<String, Object> errors = new HashMap<>();
        ex.getParameterValidationResults().forEach(result -> {
            String parameterName = result.getMethodParameter().getParameterName();
            List<String> messages = result.getResolvableErrors().stream()
                    .map(MessageSourceResolvable::getDefaultMessage)
                    .toList();
            errors.put(parameterName, messages);
        });
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, VALIDATION_ERROR_DETAIL);
        problemDetail.setProperty("errors", errors);
        return createResponseEntity(ErrorResponse.builder(ex, problemDetail)
                .build(), headers, status, request);
    }

    @Override
    protected @Nullable ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                            HttpHeaders headers,
                                                                            HttpStatusCode status,
                                                                            WebRequest request) {
        log.error("ERR", ex);
        Map<String, List<String>> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        FieldError::getField,
                        java.util.stream.Collectors.mapping(FieldError::getDefaultMessage, java.util.stream.Collectors.toList())
                ));
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, VALIDATION_ERROR_DETAIL);
        problemDetail.setProperty("errors", errors);
        return createResponseEntity(ErrorResponse.builder(ex, problemDetail)
                .build(), headers, status, request);
    }

    @ExceptionHandler(RuntimeException.class)
    public ErrorResponse handleRuntimeException(Exception e) {
        log.error("ERR", e);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                INTERNAL_SERVER_ERROR_DETAIL
        );
        return ErrorResponse.builder(e, problemDetail)
                .build();
    }

    @ExceptionHandler(Exception.class)
    public ErrorResponse handleException(Exception e) {
        log.error("ERR", e);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                INTERNAL_SERVER_ERROR_DETAIL
        );
        return ErrorResponse.builder(e, problemDetail)
                .build();
    }
}
