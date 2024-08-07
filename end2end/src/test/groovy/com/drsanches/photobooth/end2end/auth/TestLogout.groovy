package com.drsanches.photobooth.end2end.auth

import com.drsanches.photobooth.end2end.utils.RequestUtils
import com.drsanches.photobooth.end2end.utils.TestUser
import com.drsanches.photobooth.end2end.utils.Utils
import org.json.JSONObject
import spock.lang.Specification

class TestLogout extends Specification {

    String PATH = "/api/v1/auth/token"

    def "successful logout"() {
        given: "user"
        def user = new TestUser().register()
        def oldToken = RequestUtils.getToken(user.username, user.password)
        def token = RequestUtils.getToken(user.username, user.password)

        when: "request is sent"
        def response = RequestUtils.getRestClient().delete(
                path: PATH,
                headers: [Authorization: "Bearer $token"])

        then: "response is correct"
        assert response.status == 200

        and: "new token is correct"
        def newToken = RequestUtils.getToken(user.username, user.password)
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
        given: "invalid token"
        def token = UUID.randomUUID().toString()

        when: "request is sent"
        def response = RequestUtils.getRestClient().delete(
                path: PATH,
                headers: [Authorization: "Bearer $token"])

        then: "response is correct"
        assert response.status == 401
        assert Utils.validateErrorResponse(response.data as JSONObject, "Wrong token", null)
    }
}
