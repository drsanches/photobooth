package com.drsanches.photobooth.app.common.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(FIELD)
@Retention(RUNTIME)
@Pattern(regexp = Username.PATTERN, message = "wrong username format")
@Constraint(validatedBy = { })
public @interface Username {

    String PATTERN = "[A-Za-z0-9_-]*";

    String message() default "wrong username format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
