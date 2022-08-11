package ru.drsanches.photobooth.app.service.validation.validator;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import ru.drsanches.photobooth.app.service.validation.annotation.ValidBase64Image;

import javax.imageio.ImageIO;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

@Slf4j
@Component
public class ValidBase64ImageValidator implements ConstraintValidator<ValidBase64Image, String> {

    //TODO: Move to yaml
    private static final int MAX_BYTES = 300 * 1000; //300kB

    @Override
    public boolean isValid(String base64Image, ConstraintValidatorContext context) {
        if (StringUtils.isEmpty(base64Image)) {
            return true;
        }

        //The original data is less than base64 in 3/4
        if (base64Image.getBytes().length * 3/4 > MAX_BYTES) {
            customMessage(context, "base64 string is too long: max=" + MAX_BYTES);
            log.error("Base64 string is too long: length={}", base64Image.length());
            return false;
        }

        byte[] imageData;
        try {
            imageData = Base64.getDecoder().decode(base64Image);
        } catch (IllegalArgumentException e) {
            log.error("Invalid Base64 string", e);
            return false;
        }

        BufferedImage bufferedImage;
        try (ByteArrayInputStream stream = new ByteArrayInputStream(imageData)) {
            bufferedImage = ImageIO.read(stream);
        } catch (IOException e) {
            customMessage(context, "invalid image data");
            log.error("Invalid image data", e);
            return false;
        }

        if (bufferedImage == null) {
            customMessage(context, "invalid image data");
            log.error("Invalid image data");
            return false;
        }
        if (bufferedImage.getHeight() != bufferedImage.getWidth()) {
            customMessage(context, "image is not square");
            log.error("Image is not square: width={}, height={}", bufferedImage.getWidth(), bufferedImage.getHeight());
            return false;
        }
        return true;
    }

    private void customMessage(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }
}
