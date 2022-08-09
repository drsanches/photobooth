package ru.drsanches.photobooth.auth

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import ru.drsanches.photobooth.utils.DataGenerator
import ru.drsanches.photobooth.utils.RequestUtils
import ru.drsanches.photobooth.utils.TestUser
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
        def response = RequestUtils.getRestClient().put(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                body:  [newUsername: newUsername,
                        password: user.password],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200

        and: "previous token is invalid"
        assert RequestUtils.getAuthInfo(token) == null

        and: "old token is invalid"
        assert RequestUtils.getAuthInfo(oldToken) == null

        and: "user auth was updated"
        assert RequestUtils.getAuthInfo(newUsername, user.password)['username'] == newUsername

        and: "user profile was updated"
        assert RequestUtils.getUserProfile(newUsername, user.password)['username'] == newUsername

        and: "new token is different"
        assert RequestUtils.getToken(newUsername, user.password) != token
    }

    def "username change with old username"() {
        given: "user"
        def user = new TestUser().register()

        when: "request is sent"
        RequestUtils.getRestClient().put(
                path: PATH,
                headers: ["Authorization": "Bearer $user.token"],
                body:  [newUsername: user.username,
                        password: user.password],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400
    }

    def "username change without username"() {
        given: "user"
        def user = new TestUser().register()

        when: "request is sent"
        RequestUtils.getRestClient().put(
                path: PATH,
                headers: ["Authorization": "Bearer $user.token"],
                body:  [newUsername: empty,
                        password: user.password],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400

        where:
        empty << [null, ""]
    }

    def "username change without password"() {
        given: "user and new username"
        def user = new TestUser().register()
        def newUsername = DataGenerator.createValidUsername()

        when: "request is sent"
        RequestUtils.getRestClient().put(
                path: PATH,
                headers: ["Authorization": "Bearer $user.token"],
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
        given: "user, new username and invalid password"
        def user = new TestUser().register()
        def newUsername = DataGenerator.createValidUsername()
        def invalidPassword = DataGenerator.createValidPassword()

        when: "request is sent"
        RequestUtils.getRestClient().put(
                path: PATH,
                headers: ["Authorization": "Bearer $user.token"],
                body:  [newUsername: newUsername,
                        password: invalidPassword],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 401
    }

    def "username change with invalid token"() {
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
