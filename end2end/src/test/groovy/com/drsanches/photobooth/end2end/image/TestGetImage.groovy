package com.drsanches.photobooth.end2end.image

import com.drsanches.photobooth.end2end.utils.DataGenerator
import com.drsanches.photobooth.end2end.utils.RequestUtils
import com.drsanches.photobooth.end2end.utils.TestUser
import com.drsanches.photobooth.end2end.utils.Utils
import org.json.JSONObject
import spock.lang.Specification

class TestGetImage extends Specification {

    String PATH = "/api/v1/app/image/data/"

    def "successful system avatar getting"() {
        when: "request is sent"
        def response = RequestUtils.getRestClient().getBytes(path: path)

        then: "response is correct"
        assert response.status == 200
        assert Utils.getBytes(response.data) == data

        where:
        path << [
                Utils.DEFAULT_IMAGE_PATH,
                Utils.NO_PHOTO_IMAGE_PATH,
                Utils.DELETED_IMAGE_PATH
        ]
        data << [
                Utils.getImage(Utils.DEFAULT_IMAGE_FILENAME),
                Utils.getImage(Utils.NO_PHOTO_IMAGE_FILENAME),
                Utils.getImage(Utils.DELETED_IMAGE_FILENAME)
        ]
    }

    def "successful avatar getting"() {
        given: "user with avatar"
        byte[] image = DataGenerator.createValidImage()
        def user = new TestUser().register().uploadAvatar(image)

        when: "request is sent"
        def response = RequestUtils.getRestClient().getBytes(path: user.imagePath)

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
        def imagePath = user1.getAllImagesInfo()[0]["path"]

        when: "request is sent"
        def response = RequestUtils.getRestClient().getBytes(path: imagePath)

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
        def imagePath = user1.getAllImagesInfo()[0]["path"]

        when: "request is sent"
        def response = RequestUtils.getRestClient().getBytes(path: imagePath)

        then: "response is correct"
        assert response.status == 200
        assert Utils.getBytes(response.data) == image
    }

    def "get nonexistent image"() {
        given: "nonexistent image id"
        def nonexistentImageId = UUID.randomUUID().toString()

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(path: PATH + nonexistentImageId)

        then: "response is correct"
        assert response.status == 400
        assert Utils.validateErrorResponse(
                response.data as JSONObject,
                "There is no image with id '$nonexistentImageId'",
                null
        )
    }
}
