package ru.drsanches.photobooth.auth

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import net.sf.json.JSONNull
import ru.drsanches.photobooth.utils.DataGenerator
import ru.drsanches.photobooth.utils.RequestUtils
import ru.drsanches.photobooth.utils.Utils
import spock.lang.Specification

class TestRegistration extends Specification {

    String PATH = "/api/v1/auth/registration"

    def "success user registration"() {
        given: "username and password"
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
        assert response.status == 201
        assert response.getData()['id'] != null
        assert response.getData()['id'] != JSONNull.getInstance()
        assert response.getData()['username'] == username
        assert response.getData()['email'] == email

        and: "correct user was created"
        assert response.getData() == RequestUtils.getAuthInfo(username, password)

        and: "user profile was created"
        def userProfile = RequestUtils.getUserProfile(username, password)
        assert userProfile['id'] == response.getData()['id']
        assert userProfile['username'] == username
        assert userProfile['status'] == JSONNull.getInstance()
        assert userProfile['name'] == JSONNull.getInstance()
        assert userProfile['imagePath'] == Utils.getDefaultImagePath()
    }

    def "registration without username"() {
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

    def "registration without password"() {
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

    def "already existing user registration"() {
        given: "registered user"
        def username = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        def password2 = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username, password1, null)

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                body:  [username: username,
                        password: password2],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400
    }
}
