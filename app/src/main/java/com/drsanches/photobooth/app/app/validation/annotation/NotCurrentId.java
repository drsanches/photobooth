package com.drsanches.photobooth.app.app.validation.annotation;

import com.drsanches.photobooth.app.app.validation.validator.NotCurrentIdValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotCurrentIdValidator.class)
public @interface NotCurrentId {

    String message() default "the user can not be current";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
