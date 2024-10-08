package com.drsanches.photobooth.end2end.auth

import com.drsanches.photobooth.end2end.utils.DataGenerator
import com.drsanches.photobooth.end2end.utils.RequestUtils
import com.drsanches.photobooth.end2end.utils.TestUser
import com.drsanches.photobooth.end2end.utils.Utils
import org.json.JSONObject
import spock.lang.Specification

class TestLogin extends Specification {

    String PATH = "/api/v1/auth/token"

    def "successful login"() {
        given: "user"
        TestUser user = new TestUser().register()

        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                body: [username: user.username,
                       password: user.password])

        then: "response is correct"
        assert response.status == 200
        def token = response.data["accessToken"]
        token != JSONObject.NULL
        response.data["refreshToken"] != JSONObject.NULL
        response.data["tokenType"] == "Bearer"
        response.data["expires"] != JSONObject.NULL

        and: "token is correct"
        assert RequestUtils.getAuthInfo(token as String) != null
    }

    def "successful login with different username lower/upper case"() {
        given: "user"
        def uuid = UUID.randomUUID().toString().substring(0, 8)
        def username1 = "user_NAME_" + uuid
        def username2 = "USER_name_" + uuid
        def password = DataGenerator.createValidPassword()
        def email = DataGenerator.createValidEmail()
        RequestUtils.registerUser(username1, password, email)

        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                body: [username: username2,
                       password: password])

        then: "response is correct"
        assert response.status == 200

        and: "token is correct"
        def token = response.data["accessToken"]
        assert RequestUtils.getAuthInfo(token as String) != null
    }

    def "successful login with two different valid tokens" () {
        given: "user"
        TestUser user = new TestUser().register()

        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                body: [username: user.username,
                       password: user.password])

        then: "response is correct"
        assert response.status == 200

        and: "token is correct"
        def newToken = response.data["accessToken"]
        assert RequestUtils.getAuthInfo(newToken as String) != null

        and: "old token is correct"
        assert RequestUtils.getAuthInfo(user.token as String) != null
    }

    def "login with invalid data"() {
        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                body: [username: username,
                       password: password])

        then: "response is correct"
        assert response.status == status
        assert Utils.validateErrorResponse(response.data as JSONObject, message, details)

        where:
        username << [
                //No user
                DataGenerator.createValidUsername(),

                //Invalid username
                null,
                "",

                //Invalid password
                DataGenerator.createValidUsername(),
                DataGenerator.createValidUsername()
        ]
        password << [
                //No user
                DataGenerator.createValidPassword(),

                //Invalid username
                DataGenerator.createValidPassword(),
                DataGenerator.createValidPassword(),

                //Invalid password
                null,
                ""
        ]
        message << [
                //No user
                "wrong.username",

                //Invalid username
                "validation.error",
                "validation.error",

                //Invalid password
                "validation.error",
                "validation.error"
        ]
        details << [
                //No user
                null,

                //Invalid username
                [ Map.of("field", "username", "message", "must not be empty") ],
                [ Map.of("field", "username", "message", "must not be empty") ],

                //Invalid password
                [ Map.of("field", "password", "message", "must not be empty") ],
                [ Map.of("field", "password", "message", "must not be empty") ]
        ]
        status << [
                //No user
                401,

                //Invalid username
                400,
                400,

                //Invalid password
                400,
                400
        ]
    }
}
