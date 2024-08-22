package com.drsanches.photobooth.end2end.image

import com.drsanches.photobooth.end2end.utils.DataGenerator
import com.drsanches.photobooth.end2end.utils.RequestUtils
import com.drsanches.photobooth.end2end.utils.TestUser
import com.drsanches.photobooth.end2end.utils.Utils
import org.json.JSONArray
import org.json.JSONObject
import spock.lang.Specification

class TestGetAllImagesInfo extends Specification {

    String PATH = "/api/v1/app/image/all"

    def IMAGE_PATH = { String imageId -> "/api/v1/app/image/data/$imageId" }

    def THUMBNAIL_PATH = { String imageId -> "/api/v1/app/image/data/thumbnail/$imageId" }

    def "successful empty image list info getting"() {
        given: "user"
        def user = new TestUser().register()

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH,
                headers: [Authorization: "Bearer $user.token"])

        then: "response is correct"
        assert response.status == 200
        assert (response.data as JSONArray).size() == 0
    }

    def "successful all images info getting"() {
        given: "two friends"
        def image1 = DataGenerator.createValidImage()
        def image2 = DataGenerator.createValidImage()
        def user1 = new TestUser().register()
        def user2 = new TestUser().register()
        user1.sendFriendRequest(user2.id)
        user2.sendFriendRequest(user1.id)
        def date1 = new Date()
        user1.sendPhoto([user2.id], image1)
        def date2 = new Date()
        user2.sendPhoto([user1.id], image2)
        def date3 = new Date()

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH,
                headers: [Authorization: "Bearer $user1.token"])

        then: "response is correct"
        assert response.status == 200
        def data = response.data as JSONArray
        assert data.size() == 2
        assert data[0]["id"] != JSONObject.NULL
        assert data[0]["path"] == IMAGE_PATH(data[0]["id"] as String)
        assert data[0]["thumbnailPath"] == THUMBNAIL_PATH(data[0]["id"] as String)
        assert data[0]["ownerId"] == user2.id
        assert Utils.checkTimestamp(date2, data[0]["created"] as String, date3)
        assert data.get(1)["id"] != JSONObject.NULL
        assert data.get(1)["path"] == IMAGE_PATH(data.get(1)["id"] as String)
        assert data.get(1)["thumbnailPath"] == THUMBNAIL_PATH(data.get(1)["id"] as String)
        assert data.get(1)["ownerId"] == user1.id
        assert Utils.checkTimestamp(date1, data.get(1)["created"] as String, date2)

        and: "images are correct"
        assert image2 == RequestUtils.getImage(user1.token, IMAGE_PATH(data[0]["id"] as String))
        assert image1 == RequestUtils.getImage(user1.token, IMAGE_PATH(data.get(1)["id"] as String))
        assert Utils.toThumbnail(image2) == RequestUtils.getImage(user1.token, THUMBNAIL_PATH(data[0]["id"] as String) as String)
        assert Utils.toThumbnail(image1) == RequestUtils.getImage(user1.token, THUMBNAIL_PATH(data.get(1)["id"] as String) as String)
    }

    def "all images info getting with invalid token"() {
        given: "invalid token"
        def token = UUID.randomUUID().toString()

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH,
                headers: [Authorization: "Bearer $token"])

        then: "response is correct"
        assert response.status == 401
        assert Utils.validateErrorResponse(response.data as JSONObject, "wrong.token", null)
    }
}
