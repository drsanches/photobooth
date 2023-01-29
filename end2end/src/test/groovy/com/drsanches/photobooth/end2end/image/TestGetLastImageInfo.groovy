package com.drsanches.photobooth.end2end.image

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import net.sf.json.JSONNull
import com.drsanches.photobooth.end2end.utils.DataGenerator
import com.drsanches.photobooth.end2end.utils.RequestUtils
import com.drsanches.photobooth.end2end.utils.TestUser
import com.drsanches.photobooth.end2end.utils.Utils
import spock.lang.Specification

class TestGetLastImageInfo extends Specification {

    String PATH = "/api/v1/image/last"

    String IMAGE_PATH_PREFIX = "/api/v1/image/"

    String THUMBNAIL_PATH_PREFIX = "/api/v1/image/thumbnail/"

    def "successful last image getting for no photo"() {
        given: "user"
        def user = new TestUser().register()

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH,
                headers: ["Authorization": "Bearer $user.token"],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        assert response.getData()["id"] == "no_photo"
        assert response.getData()["path"] == Utils.NO_PHOTO_IMAGE_PATH
        assert response.getData()["thumbnailPath"] == Utils.NO_PHOTO_THUMBNAIL_PATH
        assert response.getData()["createdTime"] == JSONNull.getInstance()
        assert response.getData()["ownerId"] == JSONNull.getInstance()
    }

    def "successful last image info getting with last send by friend"() {
        given: "two friends"
        def image = DataGenerator.createValidImage()
        def user1 = new TestUser().register()
        def user2 = new TestUser().register()
        user2.sendFriendRequest(user1.id)
        user1.sendFriendRequest(user2.id).sendPhoto([user2.id], DataGenerator.createValidImage())
        def date1 = new Date()
        user2.sendPhoto([user1.id], image)
        def date2 = new Date()

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH,
                headers: ["Authorization": "Bearer $user1.token"],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        assert response.getData()["id"] != JSONNull.getInstance()
        assert response.getData()["path"] == IMAGE_PATH_PREFIX + response.getData()["id"]
        assert response.getData()["thumbnailPath"] == THUMBNAIL_PATH_PREFIX + response.getData()["id"]
        assert response.getData()["ownerId"] == user2.id
        assert Utils.checkTimestamp(date1, response.getData()["createdTime"] as String, date2)

        and: "image is correct"
        assert image == RequestUtils.getImage(user1.token, IMAGE_PATH_PREFIX + response.getData()["id"])
        assert Utils.toThumbnail(image) == RequestUtils.getImage(user1.token, THUMBNAIL_PATH_PREFIX + response.getData()["id"])
    }

    def "successful last image info getting with last send by yourself"() {
        given: "two friends"
        def image = DataGenerator.createValidImage()
        def user1 = new TestUser().register()
        def user2 = new TestUser().register()
        user1.sendFriendRequest(user2.id)
        user2.sendFriendRequest(user1.id)
        def date1 = new Date()
        user2.sendPhoto([user1.id], image)
        def date2 = new Date()
        user1.sendPhoto([user2.id], DataGenerator.createValidImage())

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH,
                headers: ["Authorization": "Bearer $user1.token"],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        assert response.getData()["id"] != JSONNull.getInstance()
        assert response.getData()["path"] == IMAGE_PATH_PREFIX + response.getData()["id"]
        assert response.getData()["thumbnailPath"] == THUMBNAIL_PATH_PREFIX + response.getData()["id"]
        assert response.getData()["ownerId"] == user2.id
        assert Utils.checkTimestamp(date1, response.getData()["createdTime"] as String, date2)

        and: "image is correct"
        assert image == RequestUtils.getImage(user1.token, IMAGE_PATH_PREFIX + response.getData()["id"])
        assert Utils.toThumbnail(image) == RequestUtils.getImage(user1.token, THUMBNAIL_PATH_PREFIX + response.getData()["id"])
    }

    def "all images info getting with invalid token"() {
        given: "invalid token"
        def token = UUID.randomUUID().toString()

        when: "request is sent"
        RequestUtils.getRestClient().get(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 401
    }
}
