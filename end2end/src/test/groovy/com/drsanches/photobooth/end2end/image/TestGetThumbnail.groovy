package com.drsanches.photobooth.end2end.image

import com.drsanches.photobooth.end2end.utils.DataGenerator
import com.drsanches.photobooth.end2end.utils.RequestUtils
import com.drsanches.photobooth.end2end.utils.TestUser
import com.drsanches.photobooth.end2end.utils.Utils
import org.json.JSONObject
import spock.lang.Specification

class TestGetThumbnail extends Specification {

    def THUMBNAIL_PATH = { String imageId -> "/api/v1/app/image/data/thumbnail/" + imageId }

    def "successful system profile photo thumbnail getting"() {
        when: "request is sent"
        def response = RequestUtils.getRestClient().getBytes(path: path)

        then: "response is correct"
        assert response.status == 200
        assert response.data == data

        where:
        path << [
                Utils.DEFAULT_THUMBNAIL_PATH,
                Utils.NO_PHOTO_THUMBNAIL_PATH,
                Utils.DELETED_THUMBNAIL_PATH
        ]
        data << [
                Utils.toThumbnail(Utils.getImage(Utils.DEFAULT_IMAGE_FILENAME)),
                Utils.toThumbnail(Utils.getImage(Utils.NO_PHOTO_IMAGE_FILENAME)),
                Utils.toThumbnail(Utils.getImage(Utils.DELETED_IMAGE_FILENAME))
        ]
    }

    def "successful profile photo thumbnail getting"() {
        given: "user with photo"
        def image = DataGenerator.createValidImage()
        def user = new TestUser().register().uploadProfilePhoto(image)

        when: "request is sent"
        def response = RequestUtils.getRestClient().getBytes(path: user.thumbnailPath)

        then: "response is correct"
        assert response.status == 200
        assert response.data == Utils.toThumbnail(image)
    }

    def "successful friends image thumbnail getting"() {
        given: "two friends"
        def image = DataGenerator.createValidImage()
        def user1 = new TestUser().register()
        def user2 = new TestUser().register()
        user1.sendFriendRequest(user2.id)
        user2.sendFriendRequest(user1.id).sendPhoto([user1.id], image)
        def imagePath = user1.getAllImagesInfo()[0]["thumbnailPath"]

        when: "request is sent"
        def response = RequestUtils.getRestClient().getBytes(path: imagePath)

        then: "response is correct"
        assert response.status == 200
        assert response.data == Utils.toThumbnail(image)
    }

    def "successful current user image thumbnail getting"() {
        given: "two friends"
        def image = DataGenerator.createValidImage()
        def user1 = new TestUser().register()
        def user2 = new TestUser().register()
        user2.sendFriendRequest(user1.id)
        user1.sendFriendRequest(user2.id).sendPhoto([user2.id], image)
        def imagePath = user1.getAllImagesInfo()[0]["thumbnailPath"]

        when: "request is sent"
        def response = RequestUtils.getRestClient().getBytes(path: imagePath)

        then: "response is correct"
        assert response.status == 200
        assert response.data == Utils.toThumbnail(image)
    }

    def "get nonexistent thumbnail"() {
        given: "nonexistent image id"
        def nonexistentImageId = UUID.randomUUID().toString()

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(path: THUMBNAIL_PATH(nonexistentImageId))

        then: "response is correct"
        assert response.status == 404
        assert Utils.validateErrorResponse(response.data as JSONObject, "image.not.found", null)
    }
}
