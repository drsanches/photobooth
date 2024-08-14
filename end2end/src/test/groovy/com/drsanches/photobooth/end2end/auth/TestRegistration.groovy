package com.drsanches.photobooth.end2end.auth

import org.apache.commons.lang3.RandomStringUtils
import com.drsanches.photobooth.end2end.utils.DataGenerator
import com.drsanches.photobooth.end2end.utils.RequestUtils
import com.drsanches.photobooth.end2end.utils.TestUser
import com.drsanches.photobooth.end2end.utils.Utils
import org.json.JSONObject
import spock.lang.Specification

class TestRegistration extends Specification {

    String PATH = "/api/v1/auth/account"

    def "success user registration"() {
        given: "username, password and email"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        def email = DataGenerator.createValidEmail()

        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                body: [username: username,
                       password: password,
                       email: email])

        then: "response is correct"
        assert response.status == 200
        def token = response.data["result"]["accessToken"]
        assert token != JSONObject.NULL
        assert response.data["result"]["refreshToken"] != JSONObject.NULL
        assert response.data["result"]["tokenType"] == "Bearer"
        assert response.data["result"]["expires"] != JSONObject.NULL
        assert response.data["with2FA"] == false

        and: "token is valid"
        def authInfo = RequestUtils.getAuthInfo(token as String)
        assert authInfo != null

        and: "correct user was created"
        assert authInfo["id"] != JSONObject.NULL
        assert authInfo["username"] == username
        assert authInfo["email"] == email
        assert authInfo["passwordExists"] == true
        assert authInfo["googleAuth"] == JSONObject.NULL

        and: "user profile was created"
        def userProfile = RequestUtils.getUserProfile(username, password)
        assert userProfile["id"] == authInfo["id"]
        assert userProfile["username"] == username
        assert userProfile["status"] == JSONObject.NULL
        assert userProfile["name"] == JSONObject.NULL
        assert userProfile["imagePath"] == Utils.DEFAULT_IMAGE_PATH
    }

    def "registration with existing username"() {
        given: "user"
        def user = new TestUser().register()
        def password = DataGenerator.createValidPassword()
        def email = DataGenerator.createValidEmail()

        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                body:  [username: user.username,
                        password: password,
                        email: email])

        then: "response is correct"
        assert response.status == 400
        assert Utils.validateErrorResponse(response.data as JSONObject, "Username already exists", null)
    }

    def "registration with existing email"() {
        given: "user"
        def user = new TestUser().register()
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()

        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                body:  [username: username,
                        password: password,
                        email: user.email])

        then: "response is correct"
        assert response.status == 400
        assert Utils.validateErrorResponse(response.data as JSONObject, "Email already exists", null)
    }

    def "registration with invalid data"() {
        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                body: [username: username,
                       password: password,
                       email: email])

        then: "response is correct"
        assert response.status == 400
        assert Utils.validateErrorResponse(response.data as JSONObject, "validation.error", details)

        where:
        username << [
                null,
                "",
                RandomStringUtils.randomAlphabetic(21),
                DataGenerator.createValidUsername() + "."
        ]
        password << [
                null,
                "",
                RandomStringUtils.randomAlphabetic(256),
                DataGenerator.createValidPassword()
        ]
        email << [
                null,
                "",
                RandomStringUtils.randomAlphabetic(100),
                DataGenerator.createValidEmail()
        ]
        details << [
                [
                        Map.of("field", "username", "message", "must not be empty"),
                        Map.of("field", "password", "message", "must not be empty"),
                        Map.of("field", "email", "message", "must not be empty")
                ],
                [
                        Map.of("field", "username", "message", "must not be empty"),
                        Map.of("field", "password", "message", "must not be empty"),
                        Map.of("field", "email", "message", "must not be empty")
                ],
                [
                        Map.of("field", "username", "message", "length must be between 0 and 20"),
                        Map.of("field", "password", "message", "length must be between 0 and 255"),
                        Map.of("field", "email", "message", "must be a well-formed email address")
                ],
                [
                        Map.of("field", "username", "message", "wrong username format")
                ]
        ]
    }
}
