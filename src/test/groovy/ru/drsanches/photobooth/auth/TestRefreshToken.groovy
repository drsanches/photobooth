package ru.drsanches.photobooth.auth

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import ru.drsanches.photobooth.utils.RequestUtils
import ru.drsanches.photobooth.utils.TestUser
import spock.lang.Specification

class TestRefreshToken extends Specification {

    String PATH = "/api/v1/auth/refreshToken"

    def "successful token refresh"() {
        given: "user"
        def user = new TestUser().register()
        def oldToken = RequestUtils.getToken(user.username, user.password)
        def refreshToken = RequestUtils.getRefreshToken(user.username, user.password)

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH,
                headers: ["Authorization": "Bearer $refreshToken"],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200

        and: "token is correct"
        def token = response.getData()["accessToken"]
        assert RequestUtils.getAuthInfo(token as String) != null

        and: "old token is invalid"
        assert RequestUtils.getAuthInfo(oldToken as String) == null
    }

    def "refresh token with invalid refreshToken"() {
        given: "invalid refresh token"
        def invalidRefreshToken = UUID.randomUUID().toString()

        when: "request is sent"
        RequestUtils.getRestClient().get(
                path: PATH,
                headers: ["Authorization": "Bearer $invalidRefreshToken"],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 401
    }
}
