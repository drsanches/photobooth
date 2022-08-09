package ru.drsanches.photobooth.image

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import ru.drsanches.photobooth.utils.RequestUtils
import ru.drsanches.photobooth.utils.TestUser
import ru.drsanches.photobooth.utils.Utils
import spock.lang.Specification

class TestGetImage extends Specification {

    String PATH = "/api/v1/image/"

    def "successful default avatar getting"() {
        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: Utils.getDefaultImagePath(),
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        assert Utils.checkDefaultImage(response.data)
    }

    def "successful no photo image getting"() {
        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: Utils.getNoPhotoPath(),
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        assert Utils.checkNoPhotoImage(response.data)
    }

    def "successful avatar getting"() {
        given: "user with avatar"
        def user = new TestUser().register().uploadTestAvatar()

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: user.imagePath,
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        assert Utils.checkTestImage(response.data)
    }

    def "get nonexistent image"() {
        given: "nonexistent image id"
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
