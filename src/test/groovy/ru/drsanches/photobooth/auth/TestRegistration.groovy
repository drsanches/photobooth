package ru.drsanches.photobooth.auth

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import net.sf.json.JSONNull
import org.apache.commons.lang3.RandomStringUtils
import ru.drsanches.photobooth.utils.DataGenerator
import ru.drsanches.photobooth.utils.RequestUtils
import ru.drsanches.photobooth.utils.TestUser
import ru.drsanches.photobooth.utils.Utils
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
        def token = response.getData()["accessToken"]
        assert token != JSONNull.getInstance()
        assert response.getData()["refreshToken"] != JSONNull.getInstance()
        assert response.getData()["tokenType"] == "Bearer"
        assert response.getData()["expiresAt"] != JSONNull.getInstance()

        and: "token is valid"
        def authInfo = RequestUtils.getAuthInfo(token as String)
        assert authInfo != null

        and: "correct user was created"
        assert authInfo["id"] != JSONNull.getInstance()
        assert authInfo["username"] == username
        assert authInfo["email"] == email

        and: "user profile was created"
        def userProfile = RequestUtils.getUserProfile(username, password)
        assert userProfile['id'] == authInfo["id"]
        assert userProfile['username'] == username
        assert userProfile['status'] == JSONNull.getInstance()
        assert userProfile['name'] == JSONNull.getInstance()
        assert userProfile['imagePath'] == Utils.DEFAULT_IMAGE_PATH
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
        assert e.response.status == 400

        where:
        email << [null, ""]
    }

    def "registration with invalid data"() {
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
        username << [DataGenerator.createValidUsername(), RandomStringUtils.randomAlphabetic(21)]
        password << [RandomStringUtils.randomAlphabetic(256), DataGenerator.createValidPassword()]
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
        assert e.response.status == 400
    }
}
