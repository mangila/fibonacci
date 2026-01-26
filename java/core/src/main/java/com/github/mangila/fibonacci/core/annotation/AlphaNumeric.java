package com.github.mangila.fibonacci.core.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@NotBlank
@Size(min = 2, max = 255)
@Pattern(regexp = "^[a-zA-Z0-9-]+$")
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {})
public @interface AlphaNumeric {

    String message() default "Not valid alphanumeric";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
