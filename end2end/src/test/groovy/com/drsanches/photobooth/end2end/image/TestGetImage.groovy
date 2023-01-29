package com.drsanches.photobooth.end2end.image

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import com.drsanches.photobooth.end2end.utils.DataGenerator
import com.drsanches.photobooth.end2end.utils.RequestUtils
import com.drsanches.photobooth.end2end.utils.TestUser
import com.drsanches.photobooth.end2end.utils.Utils
import org.apache.commons.lang3.StringUtils
import spock.lang.Specification

class TestGetImage extends Specification {

    String PATH = "/api/v1/image/"

    def "successful default avatar getting"() {
        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: Utils.DEFAULT_IMAGE_PATH,
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        assert Utils.getBytes(response.data) == Utils.getImage(Utils.DEFAULT_IMAGE_FILENAME)
    }

    def "successful no photo image getting"() {
        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: Utils.NO_PHOTO_IMAGE_PATH,
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        assert Utils.getBytes(response.data) == Utils.getImage(Utils.NO_PHOTO_IMAGE_FILENAME)
    }

    def "successful deleted image getting"() {
        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: Utils.DELETED_IMAGE_PATH,
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        assert Utils.getBytes(response.data) == Utils.getImage(Utils.DELETED_IMAGE_FILENAME)
    }

    def "successful avatar getting"() {
        given: "user with avatar"
        byte[] image = DataGenerator.createValidImage()
        def user = new TestUser().register().uploadAvatar(image)

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: user.imagePath,
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        assert Utils.getBytes(response.data) == image
    }

    def "successful friends image getting"() {
        given: "two friends"
        def image = DataGenerator.createValidImage()
        def user1 = new TestUser().register()
        def user2 = new TestUser().register()
        user1.sendFriendRequest(user2.id)
        user2.sendFriendRequest(user1.id).sendPhoto([user1.id], image)
        def imagePath = user1.getAllImagesInfo().get(0)["path"]

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: imagePath,
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        assert Utils.getBytes(response.data) == image
    }

    def "successful current user image getting"() {
        given: "two friends"
        def image = DataGenerator.createValidImage()
        def user1 = new TestUser().register()
        def user2 = new TestUser().register()
        user2.sendFriendRequest(user1.id)
        user1.sendFriendRequest(user2.id).sendPhoto([user2.id], image)
        def imagePath = user1.getAllImagesInfo().get(0)["path"]

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: imagePath,
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        assert Utils.getBytes(response.data) == image
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
        assert StringUtils.isNotEmpty(e.response.data["uuid"] as CharSequence)
        assert e.response.data["message"] == "There is no image with id '$nonexistentImageId'"
        assert e.response.status == 400
    }
}
