package com.drsanches.photobooth.end2end.auth

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import org.apache.commons.lang3.RandomStringUtils
import com.drsanches.photobooth.end2end.utils.DataGenerator
import com.drsanches.photobooth.end2end.utils.RequestUtils
import com.drsanches.photobooth.end2end.utils.TestUser
import org.apache.commons.lang3.StringUtils
import spock.lang.Specification

class TestChangeUsername extends Specification {

    String PATH = "/api/v1/auth/changeUsername"

    def "success username change"() {
        given: "user, two tokens and new username"
        def user = new TestUser().register()
        def oldToken = RequestUtils.getToken(user.username, user.password)
        def token = RequestUtils.getToken(user.username, user.password)
        def newUsername = DataGenerator.createValidUsername()

        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: [Authorization: "Bearer $token"],
                body:  [newUsername: newUsername],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200

        and: "previous token is invalid"
        assert RequestUtils.getAuthInfo(token) == null

        and: "old token is invalid"
        assert RequestUtils.getAuthInfo(oldToken) == null

        and: "user auth was updated"
        assert RequestUtils.getAuthInfo(newUsername, user.password)["username"] == newUsername

        and: "user profile was updated"
        assert RequestUtils.getUserProfile(newUsername, user.password)["username"] == newUsername

        and: "new token is different"
        assert RequestUtils.getToken(newUsername, user.password) != token
    }

    def "username change with old username"() {
        given: "user"
        def user = new TestUser().register()

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                headers: [Authorization: "Bearer $user.token"],
                body:  [newUsername: user.username],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert StringUtils.isNotEmpty(e.response.data["uuid"] as CharSequence)
        assert e.response.data["message"] == "changeUsername.changeUsernameDto.newUsername: User with username '$user.username' already exists"
        assert e.response.status == 400
    }

    def "username change with existent username"() {
        given: "user"
        def user1 = new TestUser().register()
        def user2 = new TestUser().register()

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                headers: [Authorization: "Bearer $user1.token"],
                body:  [newUsername: user2.username],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert StringUtils.isNotEmpty(e.response.data["uuid"] as CharSequence)
        assert e.response.data["message"] == "changeUsername.changeUsernameDto.newUsername: User with username '$user2.username' already exists"
        assert e.response.status == 400
    }

    def "username change with invalid username"() {
        given: "user"
        def user = new TestUser().register()

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                headers: [Authorization: "Bearer $user.token"],
                body:  [newUsername: invalidUsername],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert StringUtils.isNotEmpty(e.response.data["uuid"] as CharSequence)
        assert e.response.data["message"] == message
        assert e.response.status == 400

        where:
        invalidUsername << [
                null,
                "",
                DataGenerator.createValidUsername() + ".",
                RandomStringUtils.randomAlphabetic(21)
        ]
        message << [
                "changeUsername.changeUsernameDto.newUsername: may not be empty",
                "changeUsername.changeUsernameDto.newUsername: may not be empty",
                "changeUsername.changeUsernameDto.newUsername: wrong username format",
                "changeUsername.changeUsernameDto.newUsername: length must be between 0 and 20"
        ]
    }

    def "username change with invalid token"() {
        given: "invalid token"
        def token = UUID.randomUUID().toString()

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                headers: [Authorization: "Bearer $token"],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert StringUtils.isNotEmpty(e.response.data["uuid"] as CharSequence)
        assert e.response.data["message"] == "Wrong token"
        assert e.response.status == 401
    }
}
