package com.drsanches.photobooth.end2end.image

import com.drsanches.photobooth.end2end.utils.DataGenerator
import com.drsanches.photobooth.end2end.utils.RequestUtils
import com.drsanches.photobooth.end2end.utils.TestUser
import com.drsanches.photobooth.end2end.utils.Utils
import org.json.JSONObject
import spock.lang.Specification

class TestGetImageInfo extends Specification {

    String PATH = "/api/v1/app/image/info/"

    def "successful default profile photo info getting"() {
        given: "user"
        def user = new TestUser().register()

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH + "default",
                headers: [Authorization: "Bearer $user.token"])

        then: "response is correct"
        assert response.status == 200
        assert response.data["id"] == "default"
        assert response.data["path"] == Utils.DEFAULT_IMAGE_PATH
        assert response.data["thumbnailPath"] == Utils.DEFAULT_THUMBNAIL_PATH
        assert response.data["created"] == JSONObject.NULL
        assert response.data["ownerId"] == JSONObject.NULL
    }

    def "successful custom profile photo info getting"() {
        given: "two users"
        def user1 = new TestUser().register()
        def user2 = new TestUser().register()
        def dateBefore = new Date()
        user2.uploadProfilePhoto(DataGenerator.createValidImage())
        def dateAfter = new Date()

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH + user2.imagePath.split("/").last(),
                headers: [Authorization: "Bearer $user1.token"])

        then: "response is correct"
        assert response.status == 200
        assert response.data["id"] == (user2.imagePath as String).substring(PATH.length())
        assert response.data["path"] == user2.imagePath
        assert response.data["thumbnailPath"] == user2.thumbnailPath
        assert response.data["ownerId"] == user2.id
        assert Utils.checkTimestamp(dateBefore, response.data["created"] as String, dateAfter)
    }

    def "get nonexistent image info"() {
        given: "user and nonexistent image id"
        def user = new TestUser().register()
        def nonexistentImageId = UUID.randomUUID().toString()

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH + nonexistentImageId,
                headers: [Authorization: "Bearer $user.token"])

        then: "response is correct"
        assert response.status == 404
        assert Utils.validateErrorResponse(response.data as JSONObject, "image.not.found", null)
    }

    def "get profile photo info with invalid token"() {
        given: "invalid token"
        def token = UUID.randomUUID().toString()

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH + "default",
                headers: [Authorization: "Bearer $token"])

        then: "response is correct"
        assert response.status == 401
        assert Utils.validateErrorResponse(response.data as JSONObject, "wrong.token", null)
    }
}
