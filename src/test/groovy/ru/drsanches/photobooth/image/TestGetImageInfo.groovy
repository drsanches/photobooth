package ru.drsanches.photobooth.image

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import net.sf.json.JSONNull
import ru.drsanches.photobooth.utils.DataGenerator
import ru.drsanches.photobooth.utils.RequestUtils
import ru.drsanches.photobooth.utils.Utils
import spock.lang.Specification

class TestGetImageInfo extends Specification {

    String PATH = "/api/v1/image/"

    def "successful default avatar info getting"() {
        given: "user without avatar"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username, password, null)
        def token = RequestUtils.getToken(username, password)

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH + "default/info",
                headers: ["Authorization": "Bearer $token"],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        assert response.getData()["id"] == "default"
        assert response.getData()["path"] == Utils.getDefaultImagePath()
        assert response.getData()["createdTime"] == JSONNull.getInstance()
        assert response.getData()["ownerId"] == JSONNull.getInstance()
    }

    def "successful custom avatar info getting"() {
        given: "user with avatar"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        def username2 = DataGenerator.createValidUsername()
        def password2 = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username1, password1, null)
        def userId2 = RequestUtils.registerUser(username2, password2, null)
        def token1 = RequestUtils.getToken(username1, password1)
        def dateBefore = new Date()
        RequestUtils.uploadTestAvatar(username2, password2)
        def dateAfter = new Date()
        def imagePath = RequestUtils.getUserProfile(username2, password2)["imagePath"]
        def imageId = (imagePath as String).substring(PATH.length())

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: imagePath + "/info",
                headers: ["Authorization": "Bearer $token1"],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        assert response.getData()["id"] == imageId
        assert response.getData()["path"] == imagePath
        assert response.getData()["ownerId"] == userId2
        assert Utils.checkTimestamp(dateBefore, response.getData()["createdTime"] as String, dateAfter)
    }

    def "get nonexistent image info"() {
        given: "user and nonexistent image id"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username, password, null)
        def token1 = RequestUtils.getToken(username, password)
        def nonexistentImageId = UUID.randomUUID().toString()

        when: "request is sent"
        RequestUtils.getRestClient().get(
                path: PATH + nonexistentImageId + "/info",
                headers: ["Authorization": "Bearer $token1"],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400
    }

    def "get avatar info with invalid token"() {
        given: "user and invalid token"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username, password, null)
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
