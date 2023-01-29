package com.drsanches.photobooth.app.app.validation.annotation;

import com.drsanches.photobooth.app.app.validation.validator.FriendIdsValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FriendIdsValidator.class)
public @interface FriendIds {

    String message() default "contains non friends";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
