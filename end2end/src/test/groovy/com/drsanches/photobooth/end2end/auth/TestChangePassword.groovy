package com.drsanches.photobooth.end2end.auth

import com.drsanches.photobooth.end2end.utils.Utils
import org.apache.commons.lang3.RandomStringUtils
import com.drsanches.photobooth.end2end.utils.DataGenerator
import com.drsanches.photobooth.end2end.utils.RequestUtils
import com.drsanches.photobooth.end2end.utils.TestUser
import org.json.JSONObject
import spock.lang.Specification

class TestChangePassword extends Specification {

    String PATH = "/api/v1/auth/account/password"

    def "success password change"() {
        given: "user, two tokens and new password"
        def user = new TestUser().register()
        def oldToken = RequestUtils.getToken(user.username, user.password)
        def token = RequestUtils.getToken(user.username, user.password)
        def newPassword = DataGenerator.createValidPassword()

        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: [Authorization: "Bearer $token"],
                body: [newPassword: newPassword])

        then: "response is correct"
        assert response.status == 200
        assert response.data["result"] == JSONObject.NULL
        assert response.data["with2FA"] == false

        and: "previous token is invalid"
        assert RequestUtils.getAuthInfo(token) == null

        and: "old token is invalid"
        assert RequestUtils.getAuthInfo(oldToken) == null

        and: "password was updated"
        assert RequestUtils.getAuthInfo(user.username, newPassword) != null

        and: "new token is different"
        assert RequestUtils.getToken(user.username, newPassword) != token
    }

    def "password change with invalid password"() {
        given: "user"
        def user = new TestUser().register()

        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: [Authorization: "Bearer $user.token"],
                body: [newPassword: invalidPassword])

        then: "response is correct"
        assert response.status == 400
        assert Utils.validateErrorResponse(response.data as JSONObject, "validation.error", [
                Map.of("field", "newPassword", "message", message)
        ])

        where:
        invalidPassword << [
                null,
                "",
                RandomStringUtils.randomAlphabetic(256)
        ]
        message << [
                "must not be empty",
                "must not be empty",
                "length must be between 0 and 255"
        ]
    }

    def "password change with invalid token"() {
        given: "invalid token"
        def token = UUID.randomUUID().toString()

        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: [Authorization: "Bearer $token"])

        then: "response is correct"
        assert response.status == 401
        assert Utils.validateErrorResponse(response.data as JSONObject, "Wrong token", null)
    }
}
