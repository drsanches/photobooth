package ru.drsanches.photobooth.auth

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import ru.drsanches.photobooth.utils.DataGenerator
import ru.drsanches.photobooth.utils.RequestUtils
import spock.lang.Specification

class TestChangeUsername extends Specification {

    String PATH = "/api/v1/auth/changeUsername"

    def "success username change"() {
        given: "registered user, password, token and new username"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username, password, null)
        def oldToken = RequestUtils.getToken(username, password)
        def token = RequestUtils.getToken(username, password)
        def newUsername = DataGenerator.createValidUsername()

        when: "request is sent"
        def response = RequestUtils.getRestClient().put(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                body:  [newUsername: newUsername,
                        password: password],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200

        and: "previous token is invalid"
        assert RequestUtils.getAuthInfo(token) == null

        and: "old token is invalid"
        assert RequestUtils.getAuthInfo(oldToken) == null

        and: "user auth was updated"
        assert RequestUtils.getAuthInfo(newUsername, password)['username'] == newUsername

        and: "user profile was updated"
        assert RequestUtils.getUserProfile(newUsername, password)['username'] == newUsername

        and: "new token is different"
        assert RequestUtils.getToken(newUsername, password) != token
    }

    def "username change with old username"() {
        given: "registered user, password, token"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username, password, null)
        def token = RequestUtils.getToken(username, password)

        when: "request is sent"
        RequestUtils.getRestClient().put(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                body:  [newUsername: username,
                        password: password],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400
    }

    def "username change without username"() {
        given: "registered user"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username, password, null)
        def token = RequestUtils.getToken(username, password)

        when: "request is sent"
        RequestUtils.getRestClient().put(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                body:  [newUsername: empty,
                        password: password],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400

        where:
        empty << [null, ""]
    }

    def "username change without password"() {
        given: "registered user"
        def username = DataGenerator.createValidUsername()
        def newUsername = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username, password, null)
        def token = RequestUtils.getToken(username, password)

        when: "request is sent"
        RequestUtils.getRestClient().put(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                body:  [newUsername: newUsername,
                        password: empty],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400

        where:
        empty << [null, ""]
    }

    def "username change with invalid password"() {
        given: "registered user, new username, token and invalid password"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        def newUsername = DataGenerator.createValidUsername()
        def invalidPassword = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username, password, null)
        def token = RequestUtils.getToken(username, password)

        when: "request is sent"
        RequestUtils.getRestClient().put(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                body:  [newUsername: newUsername,
                        password: invalidPassword],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 401
    }

    def "username change with invalid token"() {
        given: "registered user, new username and invalid token"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username, password,  null)
        def token = UUID.randomUUID().toString()
        def newUsername = DataGenerator.createValidUsername()

        when: "request is sent"
        RequestUtils.getRestClient().put(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                body:  [newUsername: newUsername,
                        password: password],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 401
    }
}
