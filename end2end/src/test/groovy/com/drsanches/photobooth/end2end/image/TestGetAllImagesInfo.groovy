package com.drsanches.photobooth.end2end.image

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import net.sf.json.JSONArray
import net.sf.json.JSONNull
import com.drsanches.photobooth.end2end.utils.DataGenerator
import com.drsanches.photobooth.end2end.utils.RequestUtils
import com.drsanches.photobooth.end2end.utils.TestUser
import com.drsanches.photobooth.end2end.utils.Utils
import org.apache.commons.lang3.StringUtils
import spock.lang.Specification

class TestGetAllImagesInfo extends Specification {

    String PATH = "/api/v1/image/all"

    def IMAGE_PATH = { String imageId -> "/api/v1/image/$imageId" }

    def THUMBNAIL_PATH = { String imageId -> IMAGE_PATH(imageId) + "/thumbnail" }

    def "successful empty image list info getting"() {
        given: "user"
        def user = new TestUser().register()

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH,
                headers: [Authorization: "Bearer $user.token"],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

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
                headers: [Authorization: "Bearer $user1.token"],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        def data = response.data as JSONArray
        assert data.size() == 2
        assert data.get(0)["id"] != JSONNull.getInstance()
        assert data.get(0)["path"] == IMAGE_PATH(data.get(0)["id"] as String)
        assert data.get(0)["thumbnailPath"] == THUMBNAIL_PATH(data.get(0)["id"] as String)
        assert data.get(0)["ownerId"] == user2.id
        assert Utils.checkTimestamp(date2, data.get(0)["createdTime"] as String, date3)
        assert data.get(1)["id"] != JSONNull.getInstance()
        assert data.get(1)["path"] == IMAGE_PATH(data.get(1)["id"] as String)
        assert data.get(1)["thumbnailPath"] == THUMBNAIL_PATH(data.get(1)["id"] as String)
        assert data.get(1)["ownerId"] == user1.id
        assert Utils.checkTimestamp(date1, data.get(1)["createdTime"] as String, date2)

        and: "images are correct"
        assert image2 == RequestUtils.getImage(user1.token, IMAGE_PATH(data.get(0)["id"] as String))
        assert image1 == RequestUtils.getImage(user1.token, IMAGE_PATH(data.get(1)["id"] as String))
        assert Utils.toThumbnail(image2) == RequestUtils.getImage(user1.token, THUMBNAIL_PATH(data.get(0)["id"] as String) as String)
        assert Utils.toThumbnail(image1) == RequestUtils.getImage(user1.token, THUMBNAIL_PATH(data.get(1)["id"] as String) as String)
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
