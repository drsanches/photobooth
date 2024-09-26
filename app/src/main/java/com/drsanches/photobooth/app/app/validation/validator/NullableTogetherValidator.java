package com.drsanches.photobooth.app.app.validation.validator;

import com.drsanches.photobooth.app.app.validation.annotation.NullableTogether;
import com.drsanches.photobooth.app.common.exception.ServerError;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.List;

@Slf4j
@Component
public class NullableTogetherValidator implements ConstraintValidator<NullableTogether, Object> {

    private List<String> fields;
    private NullableTogether.Action action;

    @Override
    public void initialize(NullableTogether constraintAnnotation) {
        this.fields = List.of(constraintAnnotation.fields());
        this.action = constraintAnnotation.action();
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        Class<?> clazz = object.getClass();
        Boolean nullable = null;
        try {
            for (String fieldName: fields) {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                Object fieldValue = field.get(object);
                if (nullable == null) {
                    nullable = fieldValue == null;
                } else {
                    if (nullable != (fieldValue == null)) {
                        return postProcessingAction(object, context);
                    }
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new ServerError("Validation error for " + clazz, e);
        }
        return true;
    }

    private boolean postProcessingAction(Object object, ConstraintValidatorContext context) {
        switch (action) {
            case CONSTRAINT_VIOLATION_ERROR -> {
                setConstraintViolation(context);
                return false;
            }
            case SET_NULL -> {
                setNull(object);
                return true;
            }
            default -> throw new ServerError("Illegal action");
        }
    }

    private void setConstraintViolation(ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        fields.forEach(field -> context
                .buildConstraintViolationWithTemplate(
                        "fields " + fields + " must all be empty or filled in"
                )
                .addPropertyNode(field)
                .addConstraintViolation()
        );
    }

    private void setNull(Object object) {
        Class<?> clazz = object.getClass();
        try {
            for (String fieldName: fields) {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                if (field.get(object) != null) {
                    field.set(object, null);
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new ServerError("Setting null fields error for " + clazz, e);
        }
    }
}
