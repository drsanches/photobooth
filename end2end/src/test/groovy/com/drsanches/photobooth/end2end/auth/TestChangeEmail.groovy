package com.drsanches.photobooth.end2end.auth

import com.drsanches.photobooth.end2end.utils.DataGenerator
import com.drsanches.photobooth.end2end.utils.RequestUtils
import com.drsanches.photobooth.end2end.utils.TestUser
import com.drsanches.photobooth.end2end.utils.Utils
import org.apache.commons.lang3.RandomStringUtils
import org.json.JSONObject
import spock.lang.Specification

class TestChangeEmail extends Specification {

    String PATH = "/api/v1/auth/account/email"

    def "success email change"() {
        given: "user and new email"
        def user = new TestUser().register()
        def newEmail = DataGenerator.createValidEmail()

        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: [Authorization: "Bearer $user.token"],
                body: [newEmail: newEmail])

        then: "response is correct"
        assert response.status == 200
        assert response.data["result"] == JSONObject.NULL
        assert response.data["with2FA"] == false

        and: "user was updated"
        assert user.getAuthInfo()["email"] == newEmail
    }

    def "email change with old email"() {
        given: "user"
        def user = new TestUser().register()

        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: [Authorization: "Bearer $user.token"],
                body: [newEmail: user.email])

        then: "response is correct"
        assert response.status == 400
        assert Utils.validateErrorResponse(response.data as JSONObject, "email.already.in.use", null)
    }

    def "email change with existent email"() {
        given: "user"
        def user1 = new TestUser().register()
        def user2 = new TestUser().register()

        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: [Authorization: "Bearer $user1.token"],
                body: [newEmail: user2.email])

        then: "response is correct"
        assert response.status == 400
        assert Utils.validateErrorResponse(response.data as JSONObject, "email.already.in.use", null)
    }

    def "email change with invalid email"() {
        given: "user"
        def user = new TestUser().register()

        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: [Authorization: "Bearer $user.token"],
                body: [newEmail: invalidEmail])

        then: "response is correct"
        assert response.status == 400
        assert Utils.validateErrorResponse(response.data as JSONObject, "validation.error", [
                Map.of("field", "newEmail", "message", message)
        ])

        where:
        invalidEmail << [
                null,
                "",
                RandomStringUtils.randomAlphabetic(300)
        ]
        message << [
                "must not be empty",
                "must not be empty",
                "must be a well-formed email address"
        ]
    }

    def "email change with invalid token"() {
        given: "invalid token"
        def token = UUID.randomUUID().toString()

        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: [Authorization: "Bearer $token"])

        then: "response is correct"
        assert response.status == 401
        assert Utils.validateErrorResponse(response.data as JSONObject, "wrong.token", null)
    }
}
