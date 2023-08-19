package com.drsanches.photobooth.app.app.validation.validator;

import com.drsanches.photobooth.app.app.validation.annotation.ValidBase64Image;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

@Slf4j
@Component
public class ValidBase64ImageValidator implements ConstraintValidator<ValidBase64Image, String> {

    @Value("${application.image.max-bytes}")
    private int maxPhotoBytes;

    @Override
    public boolean isValid(String base64Image, ConstraintValidatorContext context) {
        if (StringUtils.isEmpty(base64Image)) {
            return true;
        }

        //The original data is less than base64 in 3/4
        if (base64Image.getBytes().length * 3/4 > maxPhotoBytes) {
            customMessage(context, "base64 string is too long, max image size is " + maxPhotoBytes + " bytes");
            log.error("Base64 string is too long. Length: {}", base64Image.length());
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
            log.error("Image is not square. Width: {}, height: {}", bufferedImage.getWidth(), bufferedImage.getHeight());
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
