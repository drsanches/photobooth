package com.drsanches.photobooth.end2end.utils

import org.apache.commons.lang3.StringUtils
import org.json.JSONObject

import javax.imageio.ImageIO
import java.awt.Image
import java.awt.image.BufferedImage
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat

class Utils {

    static final String DEFAULT_IMAGE_PATH = "/api/v1/app/image/data/default"
    static final String NO_PHOTO_IMAGE_PATH = "/api/v1/app/image/data/no_photo"
    static final String DELETED_IMAGE_PATH = "/api/v1/app/image/data/deleted"

    static final String DEFAULT_THUMBNAIL_PATH = "/api/v1/app/image/data/thumbnail/default"
    static final String NO_PHOTO_THUMBNAIL_PATH = "/api/v1/app/image/data/thumbnail/no_photo"
    static final String DELETED_THUMBNAIL_PATH = "/api/v1/app/image/data/thumbnail/deleted"

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
        def df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS z")
        def date = df.parse(timestamp)
        return dateBefore.before(date) && dateAfter.after(date)
    }

    static String toBase64(byte[] source) {
        return Base64.getEncoder().encodeToString(source)
    }

    static byte[] toThumbnail(byte[] image) {
        def size = 60
        def inputStream = new ByteArrayInputStream(image)
        def bufferedImage = ImageIO.read(inputStream)
        def scaledImage = bufferedImage.getScaledInstance(size, size, Image.SCALE_DEFAULT)
        def outputImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB)
        outputImage.getGraphics().drawImage(scaledImage, 0, 0, null)
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
        ImageIO.write(outputImage, "jpg", outputStream)
        return outputStream.toByteArray()
    }

    static String sha256(String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256")
        byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8))
        StringBuilder hexString = new StringBuilder(2 * encodedHash.length)
        for (byte b : encodedHash) {
            String hex = Integer.toHexString(0xff & b)
            if (hex.length() == 1) {
                hexString.append('0')
            }
            hexString.append(hex)
        }
        return hexString.toString()
    }

    static validateErrorResponse(JSONObject response, String code, List<Map<String, String>> expectedDetails) {
        if (expectedDetails != null && !expectedDetails.isEmpty()) {
            var responseDetails = response.getJSONArray("details")
            if (expectedDetails.size() != responseDetails.size()) {
                return false
            }
            for (Object responseDetail: responseDetails) {
                if (!validateDetail(expectedDetails, (responseDetail as JSONObject))) {
                    return false
                }
            }
        }
        return StringUtils.isNotEmpty(response["uuid"] as CharSequence)
                && response["code"] == code
                && expectedDetails == null ? response.keySet().size() == 2 : response.keySet().size() == 3
    }

    private static boolean validateDetail(List<Map<String, String>> expectedDetails, JSONObject responseDetail) {
        if (responseDetail.keySet().size() != 2) {
            return false
        }
        for (Map<String, String> expectedDetail: expectedDetails) {
            if (expectedDetail.get("field") == responseDetail.getString("field")
                    && expectedDetail.get("message") == responseDetail.getString("message")) {
                return true
            }
        }
        return false
    }
}
