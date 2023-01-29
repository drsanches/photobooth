package com.drsanches.photobooth.end2end

import com.drsanches.photobooth.end2end.utils.RequestUtils
import com.drsanches.photobooth.end2end.utils.TestUser
import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import spock.lang.Specification

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

//TODO: Fix scripts
class TestScripts
//        extends Specification //Uncomment before using
{
    String username = "admin"
    String password = "admin"
    String googleAccessToken = "googleAccessToken"

    def "register a user by google"() {
        when:
        RequestUtils.getRestClient().post(
                path: "/api/v1/auth/google/registration",
                body:  [accessToken: googleAccessToken],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then:
        assert true
    }

    def "login a user by google"() {
        when:
        RequestUtils.getRestClient().post(
                path: "/api/v1/auth/google/login",
                body:  [accessToken: googleAccessToken],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then:
        assert true
    }

    def "send incoming friend request to the user"() {
        given:
        TestUser user = new TestUser(username, sha256(password))

        when:
        new TestUser().register().sendFriendRequest(user.id)

        then:
        assert true
    }

    def "send outgoing friend request from the user"() {
        given:
        TestUser user = new TestUser(username, sha256(password))

        when:
        TestUser outgoing = new TestUser().register()
        user.sendFriendRequest(outgoing.id)

        then:
        assert true
    }

    def "add a friend to the user"() {
        given:
        TestUser user = new TestUser(username, sha256(password))

        when:
        TestUser friend = new TestUser().register().sendFriendRequest(user.id)
        user.sendFriendRequest(friend.id)

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
