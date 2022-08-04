package ru.drsanches.photobooth.utils

import org.apache.http.HttpEntity
import org.apache.http.entity.ContentType
import org.apache.http.entity.mime.MultipartEntityBuilder

class Utils {

    static HttpEntity createTestImageMultipart() {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create()
        File file = new File(getTestImageFilename())
        builder.addBinaryBody("file", new FileInputStream(file), ContentType.APPLICATION_OCTET_STREAM, file.getName())
        return builder.build()
    }

    static boolean checkDefaultImage(Object data) {
        return checkImage(data, getDefaultImageFilename())
    }

    static boolean checkTestImage(Object data) {
        return checkImage(data, getTestImageFilename())
    }

    private static boolean checkImage(Object data, String filename) {
        File file = new File(filename)
        if (data instanceof byte[]) {
            return data == file.getBytes()
        } else if (data instanceof ByteArrayInputStream) {
            return data.getBytes() == file.getBytes()
        }
        return false
    }

    static String getDefaultImagePath() {
        return "/api/v1/image/default"
    }

    static String getDefaultImageFilename() {
        return "src/main/resources/default.jpg"
    }

    static String getTestImageFilename() {
        return "src/test/resources/test.jpg"
    }
}