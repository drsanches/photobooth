package ru.drsanches.photobooth.image

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import ru.drsanches.photobooth.utils.DataGenerator
import ru.drsanches.photobooth.utils.RequestUtils
import ru.drsanches.photobooth.utils.TestUser
import ru.drsanches.photobooth.utils.Utils
import spock.lang.Specification

class TestGetThumbnail extends Specification {

    String PATH = "/api/v1/image/thumbnail/"

    def "successful default avatar thumbnail getting"() {
        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: Utils.DEFAULT_THUMBNAIL_PATH,
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        assert Utils.getBytes(response.data) == Utils.toThumbnail(Utils.getImage(Utils.DEFAULT_IMAGE_FILENAME))
    }

    def "successful no photo thumbnail getting"() {
        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: Utils.NO_PHOTO_THUMBNAIL_PATH,
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        assert Utils.getBytes(response.data) == Utils.toThumbnail(Utils.getImage(Utils.NO_PHOTO_IMAGE_FILENAME))
    }

    def "successful deleted thumbnail getting"() {
        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: Utils.DELETED_THUMBNAIL_PATH,
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        assert Utils.getBytes(response.data) == Utils.toThumbnail(Utils.getImage(Utils.DELETED_IMAGE_FILENAME))
    }

    def "successful avatar thumbnail getting"() {
        given: "user with avatar"
        def image = DataGenerator.createValidImage()
        def user = new TestUser().register().uploadAvatar(image)

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: user.thumbnailPath,
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        assert Utils.getBytes(response.data) == Utils.toThumbnail(image)
    }

    def "successful friends image thumbnail getting"() {
        given: "two friends"
        def image = DataGenerator.createValidImage()
        def user1 = new TestUser().register()
        def user2 = new TestUser().register()
        user1.sendFriendRequest(user2.id)
        user2.sendFriendRequest(user1.id).sendPhoto([user1.id], image)
        def imagePath = user1.getAllImagesInfo().get(0)["thumbnailPath"]

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: imagePath,
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        assert Utils.getBytes(response.data) == Utils.toThumbnail(image)
    }

    def "successful current user image thumbnail getting"() {
        given: "two friends"
        def image = DataGenerator.createValidImage()
        def user1 = new TestUser().register()
        def user2 = new TestUser().register()
        user2.sendFriendRequest(user1.id)
        user1.sendFriendRequest(user2.id).sendPhoto([user2.id], image)
        def imagePath = user1.getAllImagesInfo().get(0)["thumbnailPath"]

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: imagePath,
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        assert Utils.getBytes(response.data) == Utils.toThumbnail(image)
    }

    def "get nonexistent thumbnail"() {
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
