package ru.drsanches.photobooth.auth.service.validation.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.Pattern;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//TODO: Refactor
@Constraint(validatedBy = { })
@Pattern(regexp = "ya29\\.[a-zA-Z0-9\\-_]*", message = "wrong google token format")
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface GoogleAccessToken {

    String message() default "wrong google token format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
