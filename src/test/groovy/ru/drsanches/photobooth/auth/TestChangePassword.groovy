package ru.drsanches.photobooth.auth

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import ru.drsanches.photobooth.utils.DataGenerator
import ru.drsanches.photobooth.utils.RequestUtils
import spock.lang.Specification

class TestChangePassword extends Specification {

    String PATH = "/api/v1/auth/changePassword"

    def "success password change"() {
        given: "registered user, old and new passwords and token"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username, password, null)
        def oldToken = RequestUtils.getToken(username, password)
        def token = RequestUtils.getToken(username, password)
        def newPassword = DataGenerator.createValidPassword()

        when: "request is sent"
        def response = RequestUtils.getRestClient().put(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                body:  [oldPassword: password,
                        newPassword: newPassword],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200

        and: "previous token is invalid"
        assert RequestUtils.getAuthInfo(token) == null

        and: "old token is invalid"
        assert RequestUtils.getAuthInfo(oldToken) == null

        and: "password was updated"
        assert RequestUtils.getAuthInfo(username, newPassword) != null

        and: "new token is different"
        assert RequestUtils.getToken(username, password) != token
    }

    def "password change with equal password"() {
        given: "registered user, password, token"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username, password, null)
        def token = RequestUtils.getToken(username, password)

        when: "request is sent"
        RequestUtils.getRestClient().put(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                body:  [oldPassword: password,
                        newPassword: password],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400
    }

    def "password change with invalid odlPassword"() {
        given: "registered user, new password, token and invalid password"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        def invalidPassword = DataGenerator.createValidPassword()
        def newPassword = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username, password, null)
        def token = RequestUtils.getToken(username, password)

        when: "request is sent"
        RequestUtils.getRestClient().put(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                body:  [oldPassword: invalidPassword,
                        newPassword: newPassword],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 401
    }

    def "password change without odlPassword"() {
        given: "registered user"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        def newPassword = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username, password, null)
        def token = RequestUtils.getToken(username, password)

        when: "request is sent"
        RequestUtils.getRestClient().put(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                body:  [oldPassword: empty,
                        newPassword: newPassword],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400

        where:
        empty << [null, ""]
    }

    def "password change without newPassword"() {
        given: "registered user"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username, password, null)
        def token = RequestUtils.getToken(username, password)

        when: "request is sent"
        RequestUtils.getRestClient().put(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                body:  [oldPassword: password,
                        newPassword: empty],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400

        where:
        empty << [null, ""]
    }

    def "password change with invalid token"() {
        given: "registered user, old and new password and invalid token"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username, password,  null)
        def token = UUID.randomUUID().toString()
        def newPassword = DataGenerator.createValidPassword()

        when: "request is sent"
        RequestUtils.getRestClient().put(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                body:  [oldPassword: password,
                        newPassword: newPassword],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 401
    }
}