package ru.drsanches.photobooth.auth

import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import ru.drsanches.photobooth.utils.DataGenerator
import ru.drsanches.photobooth.utils.RequestUtils
import spock.lang.Specification

class TestLogout extends Specification {

    String PATH = "/api/v1/auth/logout"

    def "successful logout"() {
        given: "user after login"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username, password, null)
        def oldToken = RequestUtils.getToken(username, password)
        def token = RequestUtils.getToken(username, password)

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH,
                headers: ["Authorization": "Bearer $token"]) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200

        and: "new token is correct"
        def newToken = RequestUtils.getToken(username, password)
        assert RequestUtils.getAuthInfo(newToken as String) != null

        and: "new token is different"
        assert newToken != oldToken
        assert newToken != token

        and: "previous token is invalid"
        assert RequestUtils.getAuthInfo(token as String) == null

        and: "old token is valid"
        assert RequestUtils.getAuthInfo(oldToken as String) != null
    }

    def "logout with invalid token"() {
        given: "user after login"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username, password, null)
        def token = UUID.randomUUID().toString()

        when: "request is sent"
        RequestUtils.getRestClient().get(
                path: PATH,
                headers: ["Authorization": "Bearer $token"])

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 401
    }
}