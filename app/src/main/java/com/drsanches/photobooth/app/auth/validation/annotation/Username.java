package com.drsanches.photobooth.app.auth.validation.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.Pattern;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(FIELD)
@Retention(RUNTIME)
@Pattern(regexp = "[A-Za-z0-9_-]*", message = "wrong username format")
@Constraint(validatedBy = { })
public @interface Username {

    String message() default "wrong username format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
