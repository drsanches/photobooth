package com.drsanches.photobooth.end2end.auth

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import net.sf.json.JSONNull
import org.apache.commons.lang3.RandomStringUtils
import com.drsanches.photobooth.end2end.utils.DataGenerator
import com.drsanches.photobooth.end2end.utils.RequestUtils
import com.drsanches.photobooth.end2end.utils.TestUser
import com.drsanches.photobooth.end2end.utils.Utils
import org.apache.commons.lang3.StringUtils
import spock.lang.Specification

class TestRegistration extends Specification {

    String PATH = "/api/v1/auth/registration"

    def "success user registration"() {
        given: "username, password and email"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        def email = DataGenerator.createValidEmail()

        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                body:  [username: username,
                        password: password,
                        email: email],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        def token = response.data["accessToken"]
        assert token != JSONNull.getInstance()
        assert response.data["refreshToken"] != JSONNull.getInstance()
        assert response.data["tokenType"] == "Bearer"
        assert response.data["expiresAt"] != JSONNull.getInstance()

        and: "token is valid"
        def authInfo = RequestUtils.getAuthInfo(token as String)
        assert authInfo != null

        and: "correct user was created"
        assert authInfo["id"] != JSONNull.getInstance()
        assert authInfo["username"] == username
        assert authInfo["email"] == email

        and: "user profile was created"
        def userProfile = RequestUtils.getUserProfile(username, password)
        assert userProfile["id"] == authInfo["id"]
        assert userProfile["username"] == username
        assert userProfile["status"] == JSONNull.getInstance()
        assert userProfile["name"] == JSONNull.getInstance()
        assert userProfile["imagePath"] == Utils.DEFAULT_IMAGE_PATH
    }

    def "registration without username"() {
        given: "password and email"
        def password = DataGenerator.createValidPassword()
        def email = DataGenerator.createValidEmail()

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                body: [username: username,
                       password: password,
                       email: email],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert StringUtils.isNotEmpty(e.response.data["uuid"] as CharSequence)
        assert e.response.data["message"] == "registration.registrationDTO.username: may not be empty"
        assert e.response.status == 400

        where:
        username << [null, ""]
    }

    def "registration without password"() {
        given: "username and email"
        def username = DataGenerator.createValidUsername()
        def email = DataGenerator.createValidEmail()

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                body: [username: username,
                       password: password,
                       email: email],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert StringUtils.isNotEmpty(e.response.data["uuid"] as CharSequence)
        assert e.response.data["message"] == "registration.registrationDTO.password: may not be empty"
        assert e.response.status == 400

        where:
        password << [null, ""]
    }

    def "registration without email"() {
        given: "username and password"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                body: [username: username,
                       password: password,
                       email: email],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert StringUtils.isNotEmpty(e.response.data["uuid"] as CharSequence)
        assert e.response.data["message"] == "registration.registrationDTO.email: may not be empty"
        assert e.response.status == 400

        where:
        email << [null, ""]
    }

    def "registration with invalid data"() {
        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                body: [username: username,
                       password: password,
                       email: email],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert StringUtils.isNotEmpty(e.response.data["uuid"] as CharSequence)
        assert e.response.data["message"] == message
        assert e.response.status == 400

        where:
        username << [
                RandomStringUtils.randomAlphabetic(21),
                DataGenerator.createValidUsername(),
                DataGenerator.createValidUsername()
        ]
        password << [
                DataGenerator.createValidPassword(),
                RandomStringUtils.randomAlphabetic(256),
                DataGenerator.createValidPassword()
        ]
        email << [
                DataGenerator.createValidEmail(),
                DataGenerator.createValidEmail(),
                RandomStringUtils.randomAlphabetic(256)
        ]
        message << [
                "registration.registrationDTO.username: length must be between 0 and 20",
                "registration.registrationDTO.password: length must be between 0 and 255",
                "registration.registrationDTO.email: length must be between 0 and 255"
        ]
    }

    def "registration with existing username"() {
        given: "user"
        def user = new TestUser().register()
        def password = DataGenerator.createValidPassword()
        def email = DataGenerator.createValidEmail()

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                body:  [username: user.username,
                        password: password,
                        email: email],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert StringUtils.isNotEmpty(e.response.data["uuid"] as CharSequence)
        assert e.response.data["message"] == "registration.registrationDTO.username: User with username '$user.username' already exists"
        assert e.response.status == 400
    }

    def "registration with existing email"() {
        given: "user"
        def user = new TestUser().register()
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                body:  [username: username,
                        password: password,
                        email: user.email],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert StringUtils.isNotEmpty(e.response.data["uuid"] as CharSequence)
        assert e.response.data["message"] == "registration.registrationDTO.email: User with email '$user.email' already exists"
        assert e.response.status == 400
    }
}
