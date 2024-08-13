package com.drsanches.photobooth.end2end.image

import com.drsanches.photobooth.end2end.utils.DataGenerator
import com.drsanches.photobooth.end2end.utils.RequestUtils
import com.drsanches.photobooth.end2end.utils.TestUser
import com.drsanches.photobooth.end2end.utils.Utils
import org.json.JSONObject
import spock.lang.Specification

class TestSendPhoto extends Specification {

    String PATH = "/api/v1/app/image/photo"

    def IMAGE_PATH = { String imageId -> "/api/v1/app/image/data/$imageId" }

    def THUMBNAIL_PATH = { String imageId -> "/api/v1/app/image/data/thumbnail/$imageId" }

    def "successful photo send"() {
        given: "user with friends"
        def user = new TestUser().register()
        def friend1 = new TestUser().register()
        def friend2 = new TestUser().register()
        user.sendFriendRequest(friend1.id)
        user.sendFriendRequest(friend2.id)
        friend1.sendFriendRequest(user.id)
        friend2.sendFriendRequest(user.id)
        def image = DataGenerator.createValidImage()

        when: "request is sent"
        def dateBefore = new Date()
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: [Authorization: "Bearer $user.token"],
                body: [imageData: Utils.toBase64(image),
                       userIds: [friend1.id]])
        def dateAfter = new Date()

        then: "response is correct"
        assert response.status == 201

        and: "user has a new photo"
        def userImages = user.getAllImagesInfo()
        assert userImages.size() == 1
        assert userImages[0]["id"] != JSONObject.NULL
        assert userImages[0]["path"] == IMAGE_PATH(userImages[0]["id"] as String)
        assert userImages[0]["thumbnailPath"] == THUMBNAIL_PATH(userImages[0]["id"] as String)
        assert userImages[0]["ownerId"] == user.id
        assert Utils.checkTimestamp(dateBefore, userImages[0]["created"] as String, dateAfter)

        and: "one friend has similar photo"
        def friendImages = friend1.getAllImagesInfo()
        assert friendImages.size() == 1
        assert friendImages.getJSONObject(0).similar(userImages.getJSONObject(0))

        and: "image data is correct"
        assert image == RequestUtils.getImage(user.token, IMAGE_PATH(userImages[0]["id"] as String))
        assert Utils.toThumbnail(image) == RequestUtils.getImage(user.token, THUMBNAIL_PATH(userImages[0]["id"] as String) as String)

        and: "another friend doesn't have a new photo"
        assert friend2.getAllImagesInfo().size() == 0
    }

    def "successful photo send to all friends"() {
        given: "user with friends and friend requests"
        def user = new TestUser().register()
        def friend1 = new TestUser().register()
        def friend2 = new TestUser().register()
        def incoming = new TestUser().register()
        def outgoing = new TestUser().register()
        user.sendFriendRequest(friend1.id)
        user.sendFriendRequest(friend2.id)
        user.sendFriendRequest(outgoing.id)
        friend1.sendFriendRequest(user.id)
        friend2.sendFriendRequest(user.id)
        incoming.sendFriendRequest(user.id)
        def image = DataGenerator.createValidImage()

        when: "request is sent"
        def dateBefore = new Date()
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: [Authorization: "Bearer $user.token"],
                body: [imageData: Utils.toBase64(image),
                       userIds: all])
        def dateAfter = new Date()

        then: "response is correct"
        assert response.status == 201

        and: "user has a new photo"
        def userImages = user.getAllImagesInfo()
        assert userImages.size() == 1
        assert userImages[0]["id"] != JSONObject.NULL
        assert userImages[0]["path"] == IMAGE_PATH(userImages[0]["id"] as String)
        assert userImages[0]["thumbnailPath"] == THUMBNAIL_PATH(userImages[0]["id"] as String)
        assert userImages[0]["ownerId"] == user.id
        assert Utils.checkTimestamp(dateBefore, userImages[0]["created"] as String, dateAfter)

        and: "image data is correct"
        assert image == RequestUtils.getImage(user.token, IMAGE_PATH(userImages[0]["id"] as String))
        assert Utils.toThumbnail(image) == RequestUtils.getImage(user.token, THUMBNAIL_PATH(userImages[0]["id"] as String) as String)

        and: "the first friend has similar photo"
        def friendImages1 = friend1.getAllImagesInfo()
        assert friendImages1.size() == 1
        assert friendImages1.getJSONObject(0).similar(userImages.getJSONObject(0))

        and: "the second friend has similar photo"
        def friendImages2 = friend2.getAllImagesInfo()
        assert friendImages2.size() == 1
        assert friendImages2.getJSONObject(0).similar(userImages.getJSONObject(0))

        and: "incoming and outgoing don't have any photo"
        assert incoming.getAllImagesInfo().size() == 0
        assert outgoing.getAllImagesInfo().size() == 0

        where:
        all << [[], null]
    }

    def "successful photo send to all friends without friends"() {
        given: "user without friends"
        def user = new TestUser().register()
        def image = DataGenerator.createValidImage()

        when: "request is sent"
        def dateBefore = new Date()
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: [Authorization: "Bearer $user.token"],
                body: [imageData: Utils.toBase64(image),
                       userIds: all])
        def dateAfter = new Date()

        then: "response is correct"
        assert response.status == 201

        and: "user has a new photo"
        def userImages = user.getAllImagesInfo()
        assert userImages.size() == 1
        assert userImages[0]["id"] != JSONObject.NULL
        assert userImages[0]["path"] == IMAGE_PATH(userImages[0]["id"] as String)
        assert userImages[0]["thumbnailPath"] == THUMBNAIL_PATH(userImages[0]["id"] as String)
        assert userImages[0]["ownerId"] == user.id
        assert Utils.checkTimestamp(dateBefore, userImages[0]["created"] as String, dateAfter)

        and: "image data is correct"
        assert image == RequestUtils.getImage(user.token, IMAGE_PATH(userImages[0]["id"] as String))
        assert Utils.toThumbnail(image) == RequestUtils.getImage(user.token, THUMBNAIL_PATH(userImages[0]["id"] as String) as String)

        where:
        all << [[], null]
    }

    def "send a photo to yourself"() {
        given: "user"
        def user = new TestUser().register()
        def base64Image = Utils.toBase64(DataGenerator.createValidImage())

        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: [Authorization: "Bearer $user.token"],
                body: [imageData: base64Image,
                       userIds: [user.id]])

        then: "response is correct"
        assert response.status == 400
        assert Utils.validateErrorResponse(response.data as JSONObject, "validation.error", [
                Map.of("field", "userIds", "message", "user is not a friend")
        ])
    }

    def "photo send to friend with deleted profile"() {
        given: "user and friend with deleted profile"
        def user = new TestUser().register()
        def friend = new TestUser().register()
        user.sendFriendRequest(friend.id)
        friend.sendFriendRequest(user.id).delete()
        def base64Image = Utils.toBase64(DataGenerator.createValidImage())

        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: [Authorization: "Bearer $user.token"],
                body: [imageData: base64Image,
                       userIds: [friend.id]])

        then: "response is correct"
        assert response.status == 400
        assert Utils.validateErrorResponse(response.data as JSONObject, "validation.error", [
                Map.of("field", "userIds", "message", "user not found")
        ])
    }

    def "photo send to not friend"() {
        given: "two users"
        def user1 = new TestUser().register()
        def user2 = new TestUser().register()
        def base64Image = Utils.toBase64(DataGenerator.createValidImage())

        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: [Authorization: "Bearer $user1.token"],
                body: [imageData: base64Image,
                       userIds: [user2.id]])

        then: "response is correct"
        assert response.status == 400
        assert Utils.validateErrorResponse(response.data as JSONObject, "validation.error", [
                Map.of("field", "userIds", "message", "user is not a friend")
        ])
    }

    def "photo send to user with incoming friend request"() {
        given: "user with outgoing friend request"
        def user1 = new TestUser().register()
        def user2 = new TestUser().register()
        user1.sendFriendRequest(user2.id)
        def base64Image = Utils.toBase64(DataGenerator.createValidImage())

        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: [Authorization: "Bearer $user1.token"],
                body: [imageData: base64Image,
                       userIds: [user2.id]])

        then: "response is correct"
        assert response.status == 400
        assert Utils.validateErrorResponse(response.data as JSONObject, "validation.error", [
                Map.of("field", "userIds", "message", "user is not a friend")
        ])
    }

    def "photo send to user with outgoing friend request"() {
        given: "user with incoming friend request"
        def user1 = new TestUser().register()
        def user2 = new TestUser().register()
        user2.sendFriendRequest(user1.id)
        def base64Image = Utils.toBase64(DataGenerator.createValidImage())

        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: [Authorization: "Bearer $user1.token"],
                body: [imageData: base64Image,
                       userIds: [user2.id]])

        then: "response is correct"
        assert response.status == 400
        assert Utils.validateErrorResponse(response.data as JSONObject, "validation.error", [
                Map.of("field", "userIds", "message", "user is not a friend")
        ])
    }

    def "photo send with invalid data"() {
        given: "two friends and invalid data"
        def user1 = new TestUser().register()
        def user2 = new TestUser().register()
        user1.sendFriendRequest(user2.id)
        user2.sendFriendRequest(user1.id)

        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: [Authorization: "Bearer $user1.token"],
                body: [imageData: invalidData,
                       userIds: [user2.id]])

        then: "response is correct"
        assert response.status == 400
        assert Utils.validateErrorResponse(response.data as JSONObject, "validation.error", [
                Map.of("field", "imageData", "message", message)
        ])

        where:
        invalidData << [
                null,
                "",
                ";",
                Base64.getEncoder().encodeToString("test".getBytes()),
                Base64.getEncoder().encodeToString(new byte[300 * 1000 + 1])
        ]
        message << [
                "must not be empty",
                "must not be empty",
                "invalid base64 image",
                "invalid image data",
                "base64 string is too long, max image size is 300000 bytes"
        ]
    }

    def "photo send with invalid token"() {
        given: "invalid token"
        def token = UUID.randomUUID().toString()
        def base64Image = Utils.toBase64(DataGenerator.createValidImage())

        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: [Authorization: "Bearer $token"],
                body: [imageData: base64Image])

        then: "response is correct"
        assert response.status == 401
        assert Utils.validateErrorResponse(response.data as JSONObject, "Wrong token", null)
    }
}
