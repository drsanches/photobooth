package ru.drsanches.photobooth.app.validation.annotation;

import ru.drsanches.photobooth.app.validation.validator.ValidBase64ImageValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidBase64ImageValidator.class)
public @interface ValidBase64Image {

    String message() default "invalid base64 image";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
