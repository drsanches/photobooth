package ru.drsanches.photobooth.app.service.validation.validator;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import ru.drsanches.photobooth.app.service.validation.annotation.ValidBase64Image;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Base64;

@Slf4j
@Component
public class ValidBase64ImageValidator implements ConstraintValidator<ValidBase64Image, String> {

    //TODO: Add more validations
    @Override
    public boolean isValid(String base64Image, ConstraintValidatorContext context) {
        if (StringUtils.isEmpty(base64Image)) {
            return true;
        }
        try {
            Base64.getDecoder().decode(base64Image);
            return true;
        } catch (IllegalArgumentException e) {
            log.error("Invalid Base64 string", e);
            return false;
        }
    }
}
