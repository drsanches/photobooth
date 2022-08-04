package ru.drsanches.photobooth.image

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import ru.drsanches.photobooth.utils.DataGenerator
import ru.drsanches.photobooth.utils.RequestUtils
import ru.drsanches.photobooth.utils.Utils
import spock.lang.Specification

class TestGetImage extends Specification {

    def "successful default avatar getting"() {
        given: "two users"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        def username2 = DataGenerator.createValidUsername()
        def password2 = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username1, password1, null)
        RequestUtils.registerUser(username2, password2, null)
        def token1 = RequestUtils.getToken(username1, password1)
        def imagePath = RequestUtils.getUserProfile(username2, password2)["imagePath"]

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: imagePath,
                headers: ["Authorization": "Bearer $token1"],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        assert Utils.checkDefaultImage(response.data)
    }

    def "successful avatar getting"() {
        given: "two users"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        def username2 = DataGenerator.createValidUsername()
        def password2 = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username1, password1, null)
        RequestUtils.registerUser(username2, password2, null)
        def token1 = RequestUtils.getToken(username1, password1)
        def token2 = RequestUtils.getToken(username2, password2)
        RequestUtils.uploadTestAvatar(token2)
        def imagePath = RequestUtils.getUserProfile(username2, password2)["imagePath"]

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: imagePath,
                headers: ["Authorization": "Bearer $token1"],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        assert Utils.checkTestImage(response.data)
    }

    def "successful deleted avatar getting"() {
        given: "two users"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        def username2 = DataGenerator.createValidUsername()
        def password2 = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username1, password1, null)
        RequestUtils.registerUser(username2, password2, null)
        def token1 = RequestUtils.getToken(username1, password1)
        def token2 = RequestUtils.getToken(username2, password2)
        RequestUtils.uploadTestAvatar(token2)
        def imagePath = RequestUtils.getUserProfile(username2, password2)["imagePath"]
        RequestUtils.deleteAvatar(token2)

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: imagePath,
                headers: ["Authorization": "Bearer $token1"],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        assert Utils.checkTestImage(response.data)
    }

    def "get avatar with invalid token"() {
        given: "user and invalid token"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username, password, null)
        def token = UUID.randomUUID().toString()

        when: "request is sent"
        RequestUtils.getRestClient().get(
                path: Utils.getDefaultImagePath(),
                headers: ["Authorization": "Bearer $token"],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 401
    }
}