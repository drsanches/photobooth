package ru.drsanches.photobooth.image

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import net.sf.json.JSONNull
import ru.drsanches.photobooth.utils.DataGenerator
import ru.drsanches.photobooth.utils.RequestUtils
import ru.drsanches.photobooth.utils.Utils
import spock.lang.Specification

//TODO: Add validation tests (different file formats and sizes)
class TestSendPhoto extends Specification {

    String PATH = "/api/v1/image/photo"

    String IMAGE_PATH_PREFIX = "/api/v1/image/"

    def "successful photo send"() {
        given: "user with friends"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        def friendUsername1 = DataGenerator.createValidUsername()
        def friendPassword1 = DataGenerator.createValidPassword()
        def friendUsername2 = DataGenerator.createValidUsername()
        def friendPassword2 = DataGenerator.createValidPassword()
        def userId = RequestUtils.registerUser(username, password, null)
        def friendId1 = RequestUtils.registerUser(friendUsername1, friendPassword1, null)
        def friendId2 = RequestUtils.registerUser(friendUsername2, friendPassword2, null)
        RequestUtils.sendFriendRequest(username, password, friendId1)
        RequestUtils.sendFriendRequest(username, password, friendId2)
        RequestUtils.sendFriendRequest(friendUsername1, friendPassword1, userId)
        RequestUtils.sendFriendRequest(friendUsername2, friendPassword2, userId)
        def token = RequestUtils.getToken(username, password)
        def base64Image = Utils.createTestBase64Image()

        when: "request is sent"
        def dateBefore = new Date()
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                body:  [file: base64Image,
                        userIds: [friendId1]],
                requestContentType : ContentType.JSON) as HttpResponseDecorator
        def dateAfter = new Date()

        then: "response is correct"
        assert response.status == 201

        and: "user has a new photo"
        def userImages = RequestUtils.getAllImagesInfo(username, password)
        assert userImages.size() == 1
        assert userImages.get(0)["id"] != JSONNull.getInstance()
        assert userImages.get(0)["path"] == IMAGE_PATH_PREFIX + userImages.get(0)["id"]
        assert userImages.get(0)["ownerId"] == userId
        assert Utils.checkTimestamp(dateBefore, userImages.get(0)["createdTime"] as String, dateAfter)

        and: "one friend has similar photo"
        def friendImages = RequestUtils.getAllImagesInfo(friendUsername1, friendPassword1)
        assert friendImages.size() == 1
        assert friendImages.get(0) == userImages.get(0)

        and: "image data is correct"
        assert Utils.checkTestImage(RequestUtils.getImage(username, password, IMAGE_PATH_PREFIX + userImages.get(0)["id"]))

        and: "another friend doesn't have a new photo"
        assert RequestUtils.getAllImagesInfo(friendUsername2, friendPassword2).size() == 0
    }

    def "send a photo to yourself"() {
        given: "user"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        def userId = RequestUtils.registerUser(username, password, null)
        def token = RequestUtils.getToken(username, password)
        def base64Image = Utils.createTestBase64Image()

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                body:  [file: base64Image,
                        userIds: [userId]],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400
    }

    def "photo send to friend with deleted profile"() {
        given: "user with friend with deleted profile"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        def friendUsername = DataGenerator.createValidUsername()
        def friendPassword = DataGenerator.createValidPassword()
        def userId = RequestUtils.registerUser(username, password, null)
        def friendId = RequestUtils.registerUser(friendUsername, friendPassword, null)
        RequestUtils.sendFriendRequest(username, password, friendId)
        RequestUtils.sendFriendRequest(friendUsername, friendPassword, userId)
        RequestUtils.deleteUser(friendUsername, friendPassword)
        def token = RequestUtils.getToken(username, password)
        def base64Image = Utils.createTestBase64Image()

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                body:  [file: base64Image,
                        userIds: [friendId]],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400
    }

    def "photo send to not friend"() {
        given: "two users"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        def otherUsername = DataGenerator.createValidUsername()
        def otherPassword = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username, password, null)
        def otherId = RequestUtils.registerUser(otherUsername, otherPassword, null)
        def token = RequestUtils.getToken(username, password)
        def base64Image = Utils.createTestBase64Image()

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                body:  [file: base64Image,
                        userIds: [otherId]],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400
    }

    def "photo send to user with incoming friend request"() {
        given: "user with outgoing friend request"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        def otherUsername = DataGenerator.createValidUsername()
        def otherPassword = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username, password, null)
        def otherId = RequestUtils.registerUser(otherUsername, otherPassword, null)
        RequestUtils.sendFriendRequest(username, password, otherId)
        def token = RequestUtils.getToken(username, password)
        def base64Image = Utils.createTestBase64Image()

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                body:  [file: base64Image,
                        userIds: [otherId]],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400
    }

    def "photo send to user with outgoing friend request"() {
        given: "user with incoming friend request"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        def otherUsername = DataGenerator.createValidUsername()
        def otherPassword = DataGenerator.createValidPassword()
        def userId = RequestUtils.registerUser(username, password, null)
        def otherId = RequestUtils.registerUser(otherUsername, otherPassword, null)
        RequestUtils.sendFriendRequest(otherUsername, otherPassword, userId)
        def token = RequestUtils.getToken(username, password)
        def base64Image = Utils.createTestBase64Image()

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                body:  [file: base64Image,
                        userIds: [otherId]],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400
    }

    def "photo send with invalid data"() {
        given: "user with invalid data"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        def friendUsername = DataGenerator.createValidUsername()
        def friendPassword = DataGenerator.createValidPassword()
        def userId = RequestUtils.registerUser(username, password, null)
        def friendId = RequestUtils.registerUser(friendUsername, friendPassword, null)
        RequestUtils.sendFriendRequest(username, password, friendId)
        RequestUtils.sendFriendRequest(friendUsername, friendPassword, userId)
        def token = RequestUtils.getToken(username, password)

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                body:  [file: invalidData,
                        userIds: [friendId]],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400

        where:
        invalidData << [null, "", ";"]
    }

    def "photo send with invalid token"() {
        given: "user and invalid token"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username, password, null)
        def token = UUID.randomUUID().toString()
        def base64Image = Utils.createTestBase64Image()

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                body:  [file: base64Image],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 401
    }
}
