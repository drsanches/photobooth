package ru.drsanches.photobooth.app.service.validation.annotation;

import ru.drsanches.photobooth.app.service.validation.validator.FriendIdValidator;
import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FriendIdValidator.class)
public @interface FriendId {

    String message() default "some users are not friends";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    boolean mayContainCurrent() default false;
}
