package ru.drsanches.photobooth.utils

class DataGenerator {

    static String createValidUsername() {
        return "username-" + UUID.randomUUID().toString()
    }

    static String createValidPassword() {
        return "password-" + UUID.randomUUID().toString()
    }

    static String createValidEmail() {
        return "email-" + UUID.randomUUID().toString()
    }

    static String createValidName() {
        return "name-" + UUID.randomUUID().toString()
    }

    static String createValidStatus() {
        return "status-" + UUID.randomUUID().toString()
    }
}
