package ru.drsanches.photobooth

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import ru.drsanches.photobooth.utils.RequestUtils
import ru.drsanches.photobooth.utils.TestUser
import spock.lang.Specification

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class TestScripts extends Specification {

    final String PATH = "http://localhost:8080/api/v1"

    String userId = "userId"
    String username = "username"
    String password = "password"
    String googleAccessToken = "googleAccessToken"

    def "register a user by google"() {
        when:
        RequestUtils.getRestClient().post(
                path: PATH + "/auth/google/registration",
                body:  [accessToken: googleAccessToken],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then:
        assert true
    }

    def "login a user by google"() {
        when:
        RequestUtils.getRestClient().post(
                path: PATH + "/auth/google/login",
                body:  [accessToken: googleAccessToken],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then:
        assert true
    }

    def "add a friend to the user"() {
        when:
        TestUser user = new TestUser().register().sendFriendRequest(userId)
        String token = RequestUtils.getToken(username, sha256(password))
        RequestUtils.sendFriendRequest(token, user.id)

        then:
        assert true
    }

    private static String sha256(String password) throws NoSuchAlgorithmException {
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
}
