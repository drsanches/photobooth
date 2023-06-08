package com.drsanches.photobooth.end2end.auth

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import org.apache.commons.lang3.RandomStringUtils
import com.drsanches.photobooth.end2end.utils.DataGenerator
import com.drsanches.photobooth.end2end.utils.RequestUtils
import com.drsanches.photobooth.end2end.utils.TestUser
import org.apache.commons.lang3.StringUtils
import spock.lang.Specification

class TestChangePassword extends Specification {

    String PATH = "/api/v1/auth/changePassword"

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
                body:  [newPassword: newPassword],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200

        and: "previous token is invalid"
        assert RequestUtils.getAuthInfo(token) == null

        and: "old token is invalid"
        assert RequestUtils.getAuthInfo(oldToken) == null

        and: "password was updated"
        assert RequestUtils.getAuthInfo(user.username, newPassword) != null

        and: "new token is different"
        assert RequestUtils.getToken(user.username, newPassword) != token
    }

    def "password change without newPassword"() {
        given: "user"
        def user = new TestUser().register()

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                headers: [Authorization: "Bearer $user.token"],
                body:  [newPassword: empty],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert StringUtils.isNotEmpty(e.response.data["uuid"] as CharSequence)
        assert e.response.data["message"] == "changePassword.changePasswordDto.newPassword: may not be empty"
        assert e.response.status == 400

        where:
        empty << [null, ""]
    }

    def "password change with invalid password"() {
        given: "user"
        def user = new TestUser().register()

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                headers: [Authorization: "Bearer $user.token"],
                body:  [newPassword: invalidPassword],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert StringUtils.isNotEmpty(e.response.data["uuid"] as CharSequence)
        assert e.response.data["message"] == message
        assert e.response.status == 400

        where:
        invalidPassword << [RandomStringUtils.randomAlphabetic(256)]
        message << ["changePassword.changePasswordDto.newPassword: length must be between 0 and 255"]
    }

    def "password change with invalid token"() {
        given: "invalid token"
        def token = UUID.randomUUID().toString()

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                headers: [Authorization: "Bearer $token"],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert StringUtils.isNotEmpty(e.response.data["uuid"] as CharSequence)
        assert e.response.data["message"] == "Wrong token"
        assert e.response.status == 401
    }
}
