package ru.drsanches.photobooth.image

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import ru.drsanches.photobooth.utils.DataGenerator
import ru.drsanches.photobooth.utils.RequestUtils
import ru.drsanches.photobooth.utils.Utils
import spock.lang.Specification

class TestGetImage extends Specification {

    String PATH = "/api/v1/image/"

    def "successful default avatar getting"() {
        given: "two users"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username, password, null)

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: Utils.getDefaultImagePath(),
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
        RequestUtils.uploadTestAvatar(username2, password2)
        def imagePath = RequestUtils.getUserProfile(username2, password2)["imagePath"]

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: imagePath,
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        assert Utils.checkTestImage(response.data)
    }

    def "get nonexistent image"() {
        given: "user and nonexistent image id"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username, password, null)
        def nonexistentImageId = UUID.randomUUID().toString()

        when: "request is sent"
        RequestUtils.getRestClient().get(
                path: PATH + nonexistentImageId,
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400
    }
}
