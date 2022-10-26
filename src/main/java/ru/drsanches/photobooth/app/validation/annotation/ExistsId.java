package ru.drsanches.photobooth.app.validation.annotation;

import ru.drsanches.photobooth.app.validation.validator.ExistsIdValidator;
import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ExistsIdValidator.class)
public @interface ExistsId {

    String message() default "the user does not exist";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
