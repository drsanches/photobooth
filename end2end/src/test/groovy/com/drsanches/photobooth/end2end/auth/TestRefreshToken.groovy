package com.drsanches.photobooth.end2end.auth

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import com.drsanches.photobooth.end2end.utils.RequestUtils
import com.drsanches.photobooth.end2end.utils.TestUser
import org.apache.commons.lang3.StringUtils
import spock.lang.Specification

class TestRefreshToken extends Specification {

    String PATH = "/api/v1/auth/refreshToken"

    def "successful token refresh"() {
        given: "user"
        def user = new TestUser().register()
        def oldToken = RequestUtils.getToken(user.username, user.password)
        def tokenInfo = RequestUtils.getTokenInfo(user.username, user.password)
        def token = tokenInfo["accessToken"]
        def refreshToken = tokenInfo["refreshToken"]

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH,
                headers: [Authorization: "Bearer $refreshToken"],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200

        and: "new token is correct"
        def newtToken = response.data["accessToken"]
        assert RequestUtils.getAuthInfo(newtToken as String) != null

        and: "previous token is invalid"
        assert RequestUtils.getAuthInfo(token as String) == null

        and: "old token is correct"
        assert RequestUtils.getAuthInfo(oldToken as String) != null
    }

    def "refresh token with invalid refreshToken"() {
        when: "request is sent"
        RequestUtils.getRestClient().get(
                path: PATH,
                headers: [Authorization: "Bearer $refreshToken"],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert StringUtils.isNotEmpty(e.response.data["uuid"] as CharSequence)
        assert e.response.data["message"] == "Wrong token"
        assert e.response.status == 401

        where:
        refreshToken << [
                null,
                "",
                UUID.randomUUID().toString()
        ]
    }
}
