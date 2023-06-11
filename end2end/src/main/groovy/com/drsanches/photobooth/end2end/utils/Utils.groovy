package com.drsanches.photobooth.end2end.utils

import javax.imageio.ImageIO
import java.awt.Image
import java.awt.image.BufferedImage
import java.text.DateFormat
import java.text.SimpleDateFormat

class Utils {

    static final String DEFAULT_IMAGE_PATH = "/api/v1/image/default"
    static final String NO_PHOTO_IMAGE_PATH = "/api/v1/image/no_photo"
    static final String DELETED_IMAGE_PATH = "/api/v1/image/deleted"

    static final String DEFAULT_THUMBNAIL_PATH = "/api/v1/image/default/thumbnail"
    static final String NO_PHOTO_THUMBNAIL_PATH = "/api/v1/image/no_photo/thumbnail"
    static final String DELETED_THUMBNAIL_PATH = "/api/v1/image/deleted/thumbnail"

    static final String DEFAULT_IMAGE_FILENAME = "src/test/resources/default.jpg"
    static final String NO_PHOTO_IMAGE_FILENAME = "src/test/resources/no_photo.jpg"
    static final String DELETED_IMAGE_FILENAME = "src/test/resources/deleted.jpg"

    static byte[] getBytes(Object data) {
        if (data instanceof byte[]) {
            return data
        } else if (data instanceof ByteArrayInputStream) {
            return data.getBytes()
        }
        return null
    }

    static byte[] getImage(String filename) {
        return new File(filename).getBytes()
    }

    static boolean checkTimestamp(Date dateBefore, String timestamp, Date dateAfter) {
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS z")
        Date date = df.parse(timestamp)
        return dateBefore.before(date) && dateAfter.after(date)
    }

    static String toBase64(byte[] source) {
        return Base64.getEncoder().encodeToString(source)
    }

    static byte[] toThumbnail(byte[] image) {
        int size = 60
        ByteArrayInputStream inputStream = new ByteArrayInputStream(image)
        BufferedImage bufferedImage = ImageIO.read(inputStream)
        Image scaledImage = bufferedImage.getScaledInstance(size, size, Image.SCALE_DEFAULT)
        BufferedImage outputImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB)
        outputImage.getGraphics().drawImage(scaledImage, 0, 0, null)
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
        ImageIO.write(outputImage, "jpg", outputStream)
        return outputStream.toByteArray()
    }
}
