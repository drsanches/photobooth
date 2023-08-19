package com.drsanches.photobooth.app.app.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(FIELD)
@Retention(RUNTIME)
@Pattern(regexp = "[A-Za-z0-9_ .,!?:;-]*", message = "wrong status format")
@Constraint(validatedBy = { })
public @interface Status {

    String message() default "wrong status format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
