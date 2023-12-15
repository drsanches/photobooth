package com.drsanches.photobooth.app.app.data.image;

import com.drsanches.photobooth.app.common.exception.server.ServerError;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class ImageConverter {

    @Value("${application.image.thumbnail-size}")
    private int thumbnailSize;

    public byte[] toThumbnail(byte[] image) {
        var bufferedImage = getBufferedImage(image);
        if (bufferedImage.getWidth() <= thumbnailSize && bufferedImage.getHeight() <= thumbnailSize) {
            return image;
        }
        var scaledImage = bufferedImage.getScaledInstance(thumbnailSize, thumbnailSize, Image.SCALE_DEFAULT);
        var outputImage = new BufferedImage(thumbnailSize, thumbnailSize, BufferedImage.TYPE_INT_RGB);
        outputImage.getGraphics().drawImage(scaledImage, 0, 0, null);
        return getByteArray(outputImage);
    }

    private BufferedImage getBufferedImage(byte[] source) {
        try (var stream = new ByteArrayInputStream(source)) {
            return ImageIO.read(stream);
        } catch (IOException e) {
            throw new ServerError("Invalid image data", e);
        }
    }

    private byte[] getByteArray(BufferedImage bufferedImage) {
        try (var stream = new ByteArrayOutputStream()) {
            ImageIO.write(bufferedImage, "jpg", stream);
            return stream.toByteArray();
        } catch (IOException e) {
            throw new ServerError("Error getting thumbnail byte array", e);
        }
    }
}
