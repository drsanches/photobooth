package com.drsanches.photobooth.app.app.validation.annotation;

import com.drsanches.photobooth.app.app.validation.validator.UserIdValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UserIdValidator.class)
public @interface UserId {

    Violation[] violations() default {};

    String message() default "user id validation error";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Getter
    @AllArgsConstructor
    enum Violation {
        ENABLED("user not found"),
        ENABLED_OR_DISABLED("user not found"),
        FRIEND("user is not a friend"),
        NOT_CURRENT("user can not be current");

        final String message;
    }
}
