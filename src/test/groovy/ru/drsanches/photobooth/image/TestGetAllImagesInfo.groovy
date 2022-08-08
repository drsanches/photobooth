package ru.drsanches.photobooth.image

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import net.sf.json.JSONArray
import net.sf.json.JSONNull
import ru.drsanches.photobooth.utils.DataGenerator
import ru.drsanches.photobooth.utils.RequestUtils
import ru.drsanches.photobooth.utils.Utils
import spock.lang.Specification

class TestGetAllImagesInfo extends Specification {

    String PATH = "/api/v1/image/all"

    String IMAGE_PATH_PREFIX = "/api/v1/image/"

    def "successful empty image list info getting"() {
        given: "user with friend"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username, password, null)
        def token = RequestUtils.getToken(username, password)

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        assert (response.getData() as JSONArray).size() == 0
    }

    def "successful all images info getting"() {
        given: "user with friend"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        def friendUsername = DataGenerator.createValidUsername()
        def friendPassword = DataGenerator.createValidPassword()
        def userId = RequestUtils.registerUser(username, password, null)
        def friendId = RequestUtils.registerUser(friendUsername, friendPassword, null)
        RequestUtils.sendFriendRequest(username, password, friendId)
        RequestUtils.sendFriendRequest(friendUsername, friendPassword, userId)
        def date1 = new Date()
        RequestUtils.sendTestPhoto(username, password, [friendId])
        def date2 = new Date()
        RequestUtils.sendTestPhoto(friendUsername, friendPassword, [userId])
        def date3 = new Date()
        def token = RequestUtils.getToken(username, password)

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        def data = response.getData() as JSONArray
        assert data.size() == 2
        assert data.get(0)["id"] != JSONNull.getInstance()
        assert data.get(0)["path"] == IMAGE_PATH_PREFIX + data.get(0)["id"]
        assert data.get(0)["ownerId"] == userId
        assert Utils.checkTimestamp(date1, data.get(0)["createdTime"] as String, date2)
        assert data.get(1)["id"] != JSONNull.getInstance()
        assert data.get(1)["path"] == IMAGE_PATH_PREFIX + data.get(1)["id"]
        assert data.get(1)["ownerId"] == friendId
        assert Utils.checkTimestamp(date2, data.get(1)["createdTime"] as String, date3)

        and: "images are correct"
        assert Utils.checkTestImage(RequestUtils.getImage(username, password, IMAGE_PATH_PREFIX + data.get(0)["id"]))
        assert Utils.checkTestImage(RequestUtils.getImage(username, password, IMAGE_PATH_PREFIX + data.get(1)["id"]))
    }

    def "all images info getting with invalid token"() {
        given: "user and invalid token"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username, password, null)
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
