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
        assert e.response.data["message"] == "registration.registrationDto.username: User with username '$user.username' already exists"
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
        assert e.response.data["message"] == "registration.registrationDto.email: User with email '$user.email' already exists"
        assert e.response.status == 400
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
                //Invalid username
                null,
                "",
                DataGenerator.createValidUsername() + ".",
                RandomStringUtils.randomAlphabetic(21),

                //Invalid password
                DataGenerator.createValidUsername(),
                DataGenerator.createValidUsername(),
                DataGenerator.createValidUsername(),

                //Invalid email
                DataGenerator.createValidUsername(),
                DataGenerator.createValidUsername(),
                DataGenerator.createValidUsername()
        ]
        password << [
                //Invalid username
                DataGenerator.createValidPassword(),
                DataGenerator.createValidPassword(),
                DataGenerator.createValidPassword(),
                DataGenerator.createValidPassword(),

                //Invalid password
                null,
                "",
                RandomStringUtils.randomAlphabetic(256),

                //Invalid email
                DataGenerator.createValidPassword(),
                DataGenerator.createValidPassword(),
                DataGenerator.createValidPassword()
        ]
        email << [
                //Invalid username
                DataGenerator.createValidEmail(),
                DataGenerator.createValidEmail(),
                DataGenerator.createValidEmail(),
                DataGenerator.createValidEmail(),

                //Invalid password
                DataGenerator.createValidEmail(),
                DataGenerator.createValidEmail(),
                DataGenerator.createValidEmail(),

                //Invalid email
                null,
                "",
                RandomStringUtils.randomAlphabetic(100)
        ]
        message << [
                //Invalid username
                "registration.registrationDto.username: may not be empty",
                "registration.registrationDto.username: may not be empty",
                "registration.registrationDto.username: wrong username format",
                "registration.registrationDto.username: length must be between 0 and 20",

                //Invalid password
                "registration.registrationDto.password: may not be empty",
                "registration.registrationDto.password: may not be empty",
                "registration.registrationDto.password: length must be between 0 and 255",

                //Invalid email
                "registration.registrationDto.email: may not be empty",
                "registration.registrationDto.email: may not be empty",
                "registration.registrationDto.email: not a well-formed email address"
        ]
    }
}
