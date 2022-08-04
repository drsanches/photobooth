package ru.drsanches.photobooth.auth

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import net.sf.json.JSONNull
import ru.drsanches.photobooth.utils.DataGenerator
import ru.drsanches.photobooth.utils.RequestUtils
import spock.lang.Specification

class TestChangeEmail extends Specification {

    String PATH = "/api/v1/auth/changeEmail"

    def "success email change"() {
        given: "registered user, password, token and new email"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        def email = DataGenerator.createValidEmail()
        RequestUtils.registerUser(username, password, email)
        def token = RequestUtils.getToken(username, password)
        def newEmail = DataGenerator.createValidEmail()

        when: "request is sent"
        def response = RequestUtils.getRestClient().put(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                body:  [newEmail: newEmail,
                        password: password],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200

        and: "user was updated"
        assert RequestUtils.getAuthInfo(username, password)['email'] == newEmail
    }

    def "success email change without newEmail"() {
        given: "registered user, password, token"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        def email = DataGenerator.createValidEmail()
        RequestUtils.registerUser(username, password, email)
        def token = RequestUtils.getToken(username, password)

        when: "request is sent"
        def response = RequestUtils.getRestClient().put(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                body:  [newEmail: empty,
                        password: password],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200

        and: "user was updated"
        assert RequestUtils.getAuthInfo(username, password)['email'] == result

        where:
        empty << [null, ""]
        result << [JSONNull.getInstance(), ""]
    }

    def "success email change without password"() {
        given: "registered user, password, token"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        def email = DataGenerator.createValidEmail()
        def newEmail = DataGenerator.createValidEmail()
        RequestUtils.registerUser(username, password, email)
        def token = RequestUtils.getToken(username, password)

        when: "request is sent"
        RequestUtils.getRestClient().put(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                body:  [newEmail: newEmail,
                        password: empty],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400

        where:
        empty << [null, ""]
    }

    def "email change with old email"() {
        given: "registered user, password, token"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        def email = DataGenerator.createValidEmail()
        RequestUtils.registerUser(username, password, email)
        def token = RequestUtils.getToken(username, password)

        when: "request is sent"
        RequestUtils.getRestClient().put(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                body:  [newEmail: email,
                        password: password],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400
    }

    def "email change with invalid password"() {
        given: "registered user, new email, token and invalid password"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        def email = DataGenerator.createValidEmail()
        def newEmail = DataGenerator.createValidEmail()
        def invalidPassword = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username, password, email)
        def token = RequestUtils.getToken(username, password)

        when: "request is sent"
        RequestUtils.getRestClient().put(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                body:  [newEmail: newEmail,
                        password: invalidPassword],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 401
    }

    def "email change with invalid token"() {
        given: "registered user, new email and invalid token"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        def email = DataGenerator.createValidEmail()
        RequestUtils.registerUser(username, password,  email)
        def token = UUID.randomUUID().toString()
        def newEmail = DataGenerator.createValidEmail()

        when: "request is sent"
        RequestUtils.getRestClient().put(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                body:  [newEmail: newEmail,
                        password: password],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 401
    }
}