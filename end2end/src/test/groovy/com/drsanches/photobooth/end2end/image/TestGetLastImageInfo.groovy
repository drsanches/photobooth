package com.drsanches.photobooth.end2end.image

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import net.sf.json.JSONNull
import com.drsanches.photobooth.end2end.utils.DataGenerator
import com.drsanches.photobooth.end2end.utils.RequestUtils
import com.drsanches.photobooth.end2end.utils.TestUser
import com.drsanches.photobooth.end2end.utils.Utils
import org.apache.commons.lang3.StringUtils
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
                headers: [Authorization: "Bearer $user.token"],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        assert response.data["id"] == "no_photo"
        assert response.data["path"] == Utils.NO_PHOTO_IMAGE_PATH
        assert response.data["thumbnailPath"] == Utils.NO_PHOTO_THUMBNAIL_PATH
        assert response.data["createdTime"] == JSONNull.getInstance()
        assert response.data["ownerId"] == JSONNull.getInstance()
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
                headers: [Authorization: "Bearer $user1.token"],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        assert response.data["id"] != JSONNull.getInstance()
        assert response.data["path"] == IMAGE_PATH_PREFIX + response.data["id"]
        assert response.data["thumbnailPath"] == THUMBNAIL_PATH_PREFIX + response.data["id"]
        assert response.data["ownerId"] == user2.id
        assert Utils.checkTimestamp(date1, response.data["createdTime"] as String, date2)

        and: "image is correct"
        assert image == RequestUtils.getImage(user1.token, IMAGE_PATH_PREFIX + response.data["id"])
        assert Utils.toThumbnail(image) == RequestUtils.getImage(user1.token, THUMBNAIL_PATH_PREFIX + response.data["id"])
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
                headers: [Authorization: "Bearer $user1.token"],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        assert response.data["id"] != JSONNull.getInstance()
        assert response.data["path"] == IMAGE_PATH_PREFIX + response.data["id"]
        assert response.data["thumbnailPath"] == THUMBNAIL_PATH_PREFIX + response.data["id"]
        assert response.data["ownerId"] == user2.id
        assert Utils.checkTimestamp(date1, response.data["createdTime"] as String, date2)

        and: "image is correct"
        assert image == RequestUtils.getImage(user1.token, IMAGE_PATH_PREFIX + response.data["id"])
        assert Utils.toThumbnail(image) == RequestUtils.getImage(user1.token, THUMBNAIL_PATH_PREFIX + response.data["id"])
    }

    def "all images info getting with invalid token"() {
        given: "invalid token"
        def token = UUID.randomUUID().toString()

        when: "request is sent"
        RequestUtils.getRestClient().get(
                path: PATH,
                headers: [Authorization: "Bearer $token"],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert StringUtils.isNotEmpty(e.response.data["uuid"] as CharSequence)
        assert e.response.data["message"] == "Wrong token"
        assert e.response.status == 401
    }
}
