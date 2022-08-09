package ru.drsanches.photobooth.image

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import net.sf.json.JSONArray
import net.sf.json.JSONNull
import ru.drsanches.photobooth.utils.RequestUtils
import ru.drsanches.photobooth.utils.TestUser
import ru.drsanches.photobooth.utils.Utils
import spock.lang.Specification

class TestGetAllImagesInfo extends Specification {

    String PATH = "/api/v1/image/all"

    String IMAGE_PATH_PREFIX = "/api/v1/image/"

    def "successful empty image list info getting"() {
        given: "user"
        def user = new TestUser().register()

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH,
                headers: ["Authorization": "Bearer $user.token"],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        assert (response.getData() as JSONArray).size() == 0
    }

    def "successful all images info getting"() {
        given: "two friends"
        def user1 = new TestUser().register()
        def user2 = new TestUser().register()
        user1.sendFriendRequest(user2.id)
        user2.sendFriendRequest(user1.id)
        def date1 = new Date()
        user1.sendTestPhoto([user2.id])
        def date2 = new Date()
        user2.sendTestPhoto([user1.id])
        def date3 = new Date()

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH,
                headers: ["Authorization": "Bearer $user1.token"],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        def data = response.getData() as JSONArray
        assert data.size() == 2
        assert data.get(0)["id"] != JSONNull.getInstance()
        assert data.get(0)["path"] == IMAGE_PATH_PREFIX + data.get(0)["id"]
        assert data.get(0)["ownerId"] == user1.id
        assert Utils.checkTimestamp(date1, data.get(0)["createdTime"] as String, date2)
        assert data.get(1)["id"] != JSONNull.getInstance()
        assert data.get(1)["path"] == IMAGE_PATH_PREFIX + data.get(1)["id"]
        assert data.get(1)["ownerId"] == user2.id
        assert Utils.checkTimestamp(date2, data.get(1)["createdTime"] as String, date3)

        and: "images are correct"
        assert Utils.checkTestImage(RequestUtils.getImage(user1.username, user1.password, IMAGE_PATH_PREFIX + data.get(0)["id"]))
        assert Utils.checkTestImage(RequestUtils.getImage(user1.username, user1.password, IMAGE_PATH_PREFIX + data.get(1)["id"]))
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
