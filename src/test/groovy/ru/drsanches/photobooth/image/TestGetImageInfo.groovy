package ru.drsanches.photobooth.image

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import net.sf.json.JSONNull
import ru.drsanches.photobooth.utils.RequestUtils
import ru.drsanches.photobooth.utils.TestUser
import ru.drsanches.photobooth.utils.Utils
import spock.lang.Specification

class TestGetImageInfo extends Specification {

    String PATH = "/api/v1/image/"

    def "successful default avatar info getting"() {
        given: "user"
        def user = new TestUser().register()

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH + "default/info",
                headers: ["Authorization": "Bearer $user.token"],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        assert response.getData()["id"] == "default"
        assert response.getData()["path"] == Utils.getDefaultImagePath()
        assert response.getData()["createdTime"] == JSONNull.getInstance()
        assert response.getData()["ownerId"] == JSONNull.getInstance()
    }

    def "successful custom avatar info getting"() {
        given: "two users"
        def user1 = new TestUser().register()
        def user2 = new TestUser().register()
        def dateBefore = new Date()
        user2.uploadTestAvatar()
        def dateAfter = new Date()

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: user2.imagePath + "/info",
                headers: ["Authorization": "Bearer $user1.token"],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        assert response.getData()["id"] == (user2.imagePath as String).substring(PATH.length())
        assert response.getData()["path"] == user2.imagePath
        assert response.getData()["ownerId"] == user2.id
        assert Utils.checkTimestamp(dateBefore, response.getData()["createdTime"] as String, dateAfter)
    }

    def "get nonexistent image info"() {
        given: "user and nonexistent image id"
        def user = new TestUser().register()
        def nonexistentImageId = UUID.randomUUID().toString()

        when: "request is sent"
        RequestUtils.getRestClient().get(
                path: PATH + nonexistentImageId + "/info",
                headers: ["Authorization": "Bearer $user.token"],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400
    }

    def "get avatar info with invalid token"() {
        given: "invalid token"
        def token = UUID.randomUUID().toString()

        when: "request is sent"
        RequestUtils.getRestClient().get(
                path: PATH + "default/info",
                headers: ["Authorization": "Bearer $token"],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 401
    }
}
