package com.drsanches.photobooth.app.auth.validation.annotation;

import com.drsanches.photobooth.app.auth.validation.validator.NonexistentEmailValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NonexistentEmailValidator.class)
public @interface NonexistentEmail {

    String message() default "user with this email already exists";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
