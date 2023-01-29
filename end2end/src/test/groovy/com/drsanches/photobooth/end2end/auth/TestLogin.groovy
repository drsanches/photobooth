package com.drsanches.photobooth.end2end.auth

import com.drsanches.photobooth.end2end.utils.DataGenerator
import com.drsanches.photobooth.end2end.utils.RequestUtils
import com.drsanches.photobooth.end2end.utils.TestUser
import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import net.sf.json.JSONNull
import spock.lang.Specification

class TestLogin extends Specification {

    String PATH = "/api/v1/auth/login"

    def "successful login"() {
        given: "user"
        TestUser user = new TestUser().register()

        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                body: [username: user.username,
                       password: user.password],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        def token = response.getData()["accessToken"]
        token != JSONNull.getInstance()
        response.getData()["refreshToken"] != JSONNull.getInstance()
        response.getData()["tokenType"] == "Bearer"
        response.getData()["expiresAt"] != JSONNull.getInstance()

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
                       password: password],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200

        and: "token is correct"
        def token = response.getData()["accessToken"]
        assert RequestUtils.getAuthInfo(token as String) != null
    }

    def "successful login with two different valid tokens" () {
        given: "user"
        TestUser user = new TestUser().register()

        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                body: [username: user.username,
                       password: user.password],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200

        and: "token is correct"
        def newToken = response.getData()["accessToken"]
        assert RequestUtils.getAuthInfo(newToken as String) != null

        and: "old token is correct"
        assert RequestUtils.getAuthInfo(user.token as String) != null
    }

    def "login without username"() {
        given: "password"
        def password = DataGenerator.createValidPassword()

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                body: [username: username,
                       password: password],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400

        where:
        username << [null, ""]
    }

    def "login without password"() {
        given: "username"
        def username = DataGenerator.createValidUsername()

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                body: [username: username,
                       password: password],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400

        where:
        password << [null, ""]
    }

    def "login with nonexistent username"() {
        given: "password"
        def nonexistentUsername = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                body: [username: nonexistentUsername,
                       password: password],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 401
    }

    def "login with invalid password"() {
        given: "user"
        TestUser user = new TestUser().register()
        def invalidPassword = DataGenerator.createValidPassword()

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                body: [username: user.username,
                       password: invalidPassword],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 401
    }
}
