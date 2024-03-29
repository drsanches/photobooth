package com.drsanches.photobooth.end2end.image

import com.drsanches.photobooth.end2end.utils.DataGenerator
import com.drsanches.photobooth.end2end.utils.RequestUtils
import com.drsanches.photobooth.end2end.utils.TestUser
import com.drsanches.photobooth.end2end.utils.Utils
import org.apache.commons.lang3.StringUtils
import org.json.JSONObject
import spock.lang.Specification

class TestGetImageInfo extends Specification {

    String PATH = "/api/v1/image/"

    def "successful default avatar info getting"() {
        given: "user"
        def user = new TestUser().register()

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH + "default/info",
                headers: [Authorization: "Bearer $user.token"])

        then: "response is correct"
        assert response.status == 200
        assert response.data["id"] == "default"
        assert response.data["path"] == Utils.DEFAULT_IMAGE_PATH
        assert response.data["thumbnailPath"] == Utils.DEFAULT_THUMBNAIL_PATH
        assert response.data["createdTime"] == JSONObject.NULL
        assert response.data["ownerId"] == JSONObject.NULL
    }

    def "successful custom avatar info getting"() {
        given: "two users"
        def user1 = new TestUser().register()
        def user2 = new TestUser().register()
        def dateBefore = new Date()
        user2.uploadAvatar(DataGenerator.createValidImage())
        def dateAfter = new Date()

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: user2.imagePath + "/info",
                headers: [Authorization: "Bearer $user1.token"])

        then: "response is correct"
        assert response.status == 200
        assert response.data["id"] == (user2.imagePath as String).substring(PATH.length())
        assert response.data["path"] == user2.imagePath
        assert response.data["thumbnailPath"] == user2.thumbnailPath
        assert response.data["ownerId"] == user2.id
        assert Utils.checkTimestamp(dateBefore, response.data["createdTime"] as String, dateAfter)
    }

    def "get nonexistent image info"() {
        given: "user and nonexistent image id"
        def user = new TestUser().register()
        def nonexistentImageId = UUID.randomUUID().toString()

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH + nonexistentImageId + "/info",
                headers: [Authorization: "Bearer $user.token"])

        then: "response is correct"
        assert response.status == 400
        assert StringUtils.isNotEmpty(response.data["uuid"] as CharSequence)
        assert response.data["message"] == "There is no image with id '$nonexistentImageId'"
    }

    def "get avatar info with invalid token"() {
        given: "invalid token"
        def token = UUID.randomUUID().toString()

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH + "default/info",
                headers: [Authorization: "Bearer $token"])

        then: "response is correct"
        assert response.status == 401
        assert StringUtils.isNotEmpty(response.data["uuid"] as CharSequence)
        assert response.data["message"] == "Wrong token"
    }
}
