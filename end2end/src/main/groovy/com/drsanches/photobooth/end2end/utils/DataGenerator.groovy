package com.drsanches.photobooth.end2end.utils

import javax.imageio.ImageIO
import java.awt.image.BufferedImage

class DataGenerator {

    static String createValidUsername() {
        return "username-" + UUID.randomUUID().toString().substring(0, 8)
    }

    static String createValidPassword() {
        return "password-" + UUID.randomUUID().toString()
    }

    static String createValidEmail() {
        return "email-" + UUID.randomUUID().toString() + "@example.com"
    }

    static String createValidName() {
        return "name-" + UUID.randomUUID().toString()
    }

    static String createValidStatus() {
        return "status-" + UUID.randomUUID().toString()
    }

    static byte[] createValidImage() {
        int size = 100
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB)
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                image.setRGB(x, y, new Random().nextInt())
            }
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream()
        ImageIO.write(image, "jpg", stream)
        return stream.toByteArray()
    }
}
