package com.drsanches.photobooth.end2end.auth

import com.drsanches.photobooth.end2end.utils.DataGenerator
import com.drsanches.photobooth.end2end.utils.RequestUtils
import com.drsanches.photobooth.end2end.utils.TestUser
import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import org.apache.commons.lang3.RandomStringUtils
import org.apache.commons.lang3.StringUtils
import spock.lang.Specification

class TestChangeEmail extends Specification {

    String PATH = "/api/v1/auth/changeEmail"

    def "success email change"() {
        given: "user and new email"
        def user = new TestUser().register()
        def newEmail = DataGenerator.createValidEmail()

        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: [Authorization: "Bearer $user.token"],
                body:  [newEmail: newEmail],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200

        and: "user was updated"
        assert user.getAuthInfo()["email"] == newEmail
    }

    def "email change with old email"() {
        given: "user"
        def user = new TestUser().register()

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                headers: [Authorization: "Bearer $user.token"],
                body:  [newEmail: user.email],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert StringUtils.isNotEmpty(e.response.data["uuid"] as CharSequence)
        assert e.response.data["message"] == "changeEmail.changeEmailDTO.newEmail: User with email '$user.email' already exists"
        assert e.response.status == 400
    }

    def "email change with existent email"() {
        given: "user"
        def user1 = new TestUser().register()
        def user2 = new TestUser().register()

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                headers: [Authorization: "Bearer $user1.token"],
                body:  [newEmail: user2.email],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert StringUtils.isNotEmpty(e.response.data["uuid"] as CharSequence)
        assert e.response.data["message"] == "changeEmail.changeEmailDTO.newEmail: User with email '$user2.email' already exists"
        assert e.response.status == 400
    }

    def "email change without email"() {
        given: "user"
        def user = new TestUser().register()

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                headers: [Authorization: "Bearer $user.token"],
                body:  [newUsername: empty],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert StringUtils.isNotEmpty(e.response.data["uuid"] as CharSequence)
        assert e.response.data["message"] == "changeEmail.changeEmailDTO.newEmail: may not be empty"
        assert e.response.status == 400

        where:
        empty << [null, ""]
    }

    def "email change with invalid email"() {
        given: "user"
        def user = new TestUser().register()

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                headers: [Authorization: "Bearer $user.token"],
                body:  [newEmail: invalidEmail],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert StringUtils.isNotEmpty(e.response.data["uuid"] as CharSequence)
        assert e.response.data["message"] == message
        assert e.response.status == 400

        where:
        invalidEmail << [RandomStringUtils.randomAlphabetic(256)]
        message << ["changeEmail.changeEmailDTO.newEmail: length must be between 0 and 255"]
    }

    def "email change with invalid token"() {
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
