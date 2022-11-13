package ru.drsanches.photobooth.auth

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import net.sf.json.JSONNull
import org.apache.commons.lang3.RandomStringUtils
import ru.drsanches.photobooth.utils.DataGenerator
import ru.drsanches.photobooth.utils.RequestUtils
import ru.drsanches.photobooth.utils.TestUser
import spock.lang.Specification

class TestChangeEmail extends Specification {

    String PATH = "/api/v1/auth/changeEmail"

    def "success email change"() {
        given: "user and new email"
        def user = new TestUser().register()
        def newEmail = DataGenerator.createValidEmail()

        when: "request is sent"
        def response = RequestUtils.getRestClient().put(
                path: PATH,
                headers: ["Authorization": "Bearer $user.token"],
                body:  [newEmail: newEmail],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200

        and: "user was updated"
        assert user.getAuthInfo()['email'] == newEmail
    }

    def "success email clean"() {
        given: "user"
        def user = new TestUser().register()

        when: "request is sent"
        def response = RequestUtils.getRestClient().put(
                path: PATH,
                headers: ["Authorization": "Bearer $user.token"],
                body:  [newEmail: null],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200

        and: "user was updated"
        assert user.getAuthInfo()['email'] == JSONNull.getInstance()
    }

    def "email change with old email"() {
        given: "user"
        def user = new TestUser().register()

        when: "request is sent"
        RequestUtils.getRestClient().put(
                path: PATH,
                headers: ["Authorization": "Bearer $user.token"],
                body:  [newEmail: user.email],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400
    }

    def "email change with invalid email"() {
        given: "user"
        def user = new TestUser().register()

        when: "request is sent"
        RequestUtils.getRestClient().put(
                path: PATH,
                headers: ["Authorization": "Bearer $user.token"],
                body:  [newEmail: invalidEmail],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400

        where:
        invalidEmail << [RandomStringUtils.randomAlphabetic(256)]
    }

    def "email change with invalid token"() {
        given: "invalid token"
        def token = UUID.randomUUID().toString()

        when: "request is sent"
        RequestUtils.getRestClient().put(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 401
    }
}
