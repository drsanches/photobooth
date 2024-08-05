package com.drsanches.photobooth.end2end.auth

import com.drsanches.photobooth.end2end.utils.RequestUtils
import com.drsanches.photobooth.end2end.utils.TestUser
import org.apache.commons.lang3.StringUtils
import spock.lang.Specification

class TestRefreshToken extends Specification {

    String PATH = "/api/v1/auth/token/refresh"

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
                headers: [Authorization: "Bearer $refreshToken"])

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
        def response = RequestUtils.getRestClient().get(
                path: PATH,
                headers: [Authorization: "Bearer $refreshToken"])

        then: "response is correct"
        assert response.status == 401
        assert StringUtils.isNotEmpty(response.data["uuid"] as CharSequence)
        assert response.data["message"] == "Wrong token"

        where:
        refreshToken << [
                null,
                "",
                UUID.randomUUID().toString()
        ]
    }
}
