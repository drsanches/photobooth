package ru.drsanches.photobooth.image

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import net.sf.json.JSONNull
import ru.drsanches.photobooth.utils.RequestUtils
import ru.drsanches.photobooth.utils.TestUser
import ru.drsanches.photobooth.utils.Utils
import spock.lang.Specification

class TestGetLastImageInfo extends Specification {

    String PATH = "/api/v1/image/last"

    String IMAGE_PATH_PREFIX = "/api/v1/image/"

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
        assert response.getData()["path"] == Utils.getNoPhotoPath()
        assert response.getData()["createdTime"] == JSONNull.getInstance()
        assert response.getData()["ownerId"] == JSONNull.getInstance()
    }

    def "successful last image info getting with last send by friend"() {
        given: "two friends"
        def user1 = new TestUser().register()
        def user2 = new TestUser().register()
        user1.sendFriendRequest(user2.id)
        user2.sendFriendRequest(user1.id)
        user1.sendTestPhoto([user2.id])
        def date1 = new Date()
        user2.sendTestPhoto([user1.id])
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
        assert response.getData()["ownerId"] == user2.id
        assert Utils.checkTimestamp(date1, response.getData()["createdTime"] as String, date2)

        and: "image is correct"
        assert Utils.checkTestImage(RequestUtils.getImage(user1.username, user1.password, IMAGE_PATH_PREFIX + response.getData()["id"]))
    }

    def "successful last image info getting with last send by yourself"() {
        given: "two friends"
        def user1 = new TestUser().register()
        def user2 = new TestUser().register()
        user1.sendFriendRequest(user2.id)
        user2.sendFriendRequest(user1.id)
        def date1 = new Date()
        user2.sendTestPhoto([user1.id])
        def date2 = new Date()
        user1.sendTestPhoto([user2.id])

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH,
                headers: ["Authorization": "Bearer $user1.token"],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        assert response.getData()["id"] != JSONNull.getInstance()
        assert response.getData()["path"] == IMAGE_PATH_PREFIX + response.getData()["id"]
        assert response.getData()["ownerId"] == user2.id
        assert Utils.checkTimestamp(date1, response.getData()["createdTime"] as String, date2)

        and: "image is correct"
        assert Utils.checkTestImage(RequestUtils.getImage(user1.username, user1.password, IMAGE_PATH_PREFIX + response.getData()["id"]))
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
