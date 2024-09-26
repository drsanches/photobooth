package com.drsanches.photobooth.app.app.validation.annotation;

import com.drsanches.photobooth.app.app.validation.validator.NullableTogetherValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NullableTogetherValidator.class)
public @interface NullableTogether {

    String[] fields() default {};

    Action action() default Action.CONSTRAINT_VIOLATION_ERROR;

    String message() default "fields must all be empty or filled in";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    enum Action {
        CONSTRAINT_VIOLATION_ERROR,
        SET_NULL
    }
}
