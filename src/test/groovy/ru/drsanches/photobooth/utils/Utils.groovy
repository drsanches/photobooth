package ru.drsanches.photobooth.utils

import ru.drsanches.photobooth.common.utils.GregorianCalendarConvertor

import java.text.DateFormat
import java.text.SimpleDateFormat

class Utils {

    static String createTestBase64Image() {
        File file = new File(getTestImageFilename())
        return Base64.getEncoder().encodeToString(file.getBytes())
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

    static boolean checkTimestamp(Date dateBefore, String timestamp, Date dateAfter) {
        DateFormat df = new SimpleDateFormat(GregorianCalendarConvertor.PATTERN)
        Date date = df.parse(timestamp)
        return dateBefore.before(date) && dateAfter.after(date)
    }
}