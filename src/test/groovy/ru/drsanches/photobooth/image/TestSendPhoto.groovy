package ru.drsanches.photobooth.image

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import net.sf.json.JSONNull
import ru.drsanches.photobooth.utils.DataGenerator
import ru.drsanches.photobooth.utils.RequestUtils
import ru.drsanches.photobooth.utils.TestUser
import ru.drsanches.photobooth.utils.Utils
import spock.lang.Specification

class TestSendPhoto extends Specification {

    String PATH = "/api/v1/image/photo"

    String IMAGE_PATH_PREFIX = "/api/v1/image/"

    String THUMBNAIL_PATH_PREFIX = "/api/v1/image/thumbnail/"

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
                headers: ["Authorization": "Bearer $user.token"],
                body:  [image: Utils.toBase64(image),
                        userIds: [friend1.id]],
                requestContentType : ContentType.JSON) as HttpResponseDecorator
        def dateAfter = new Date()

        then: "response is correct"
        assert response.status == 201

        and: "user has a new photo"
        def userImages = user.getAllImagesInfo()
        assert userImages.size() == 1
        assert userImages.get(0)["id"] != JSONNull.getInstance()
        assert userImages.get(0)["path"] == IMAGE_PATH_PREFIX + userImages.get(0)["id"]
        assert userImages.get(0)["thumbnailPath"] == THUMBNAIL_PATH_PREFIX + userImages.get(0)["id"]
        assert userImages.get(0)["ownerId"] == user.id
        assert Utils.checkTimestamp(dateBefore, userImages.get(0)["createdTime"] as String, dateAfter)

        and: "one friend has similar photo"
        def friendImages = friend1.getAllImagesInfo()
        assert friendImages.size() == 1
        assert friendImages.get(0) == userImages.get(0)

        and: "image data is correct"
        assert image == RequestUtils.getImage(user.token, IMAGE_PATH_PREFIX + userImages.get(0)["id"])
        assert Utils.toThumbnail(image) == RequestUtils.getImage(user.token, THUMBNAIL_PATH_PREFIX + userImages.get(0)["id"])

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
                headers: ["Authorization": "Bearer $user.token"],
                body:  [image: Utils.toBase64(image),
                        userIds: all],
                requestContentType : ContentType.JSON) as HttpResponseDecorator
        def dateAfter = new Date()

        then: "response is correct"
        assert response.status == 201

        and: "user has a new photo"
        def userImages = user.getAllImagesInfo()
        assert userImages.size() == 1
        assert userImages.get(0)["id"] != JSONNull.getInstance()
        assert userImages.get(0)["path"] == IMAGE_PATH_PREFIX + userImages.get(0)["id"]
        assert userImages.get(0)["thumbnailPath"] == THUMBNAIL_PATH_PREFIX + userImages.get(0)["id"]
        assert userImages.get(0)["ownerId"] == user.id
        assert Utils.checkTimestamp(dateBefore, userImages.get(0)["createdTime"] as String, dateAfter)

        and: "image data is correct"
        assert image == RequestUtils.getImage(user.token, IMAGE_PATH_PREFIX + userImages.get(0)["id"])
        assert Utils.toThumbnail(image) == RequestUtils.getImage(user.token, THUMBNAIL_PATH_PREFIX + userImages.get(0)["id"])

        and: "the first friend has similar photo"
        def friendImages1 = friend1.getAllImagesInfo()
        assert friendImages1.size() == 1
        assert friendImages1.get(0) == userImages.get(0)

        and: "the second friend has similar photo"
        def friendImages2 = friend2.getAllImagesInfo()
        assert friendImages2.size() == 1
        assert friendImages2.get(0) == userImages.get(0)

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
                headers: ["Authorization": "Bearer $user.token"],
                body:  [image: Utils.toBase64(image),
                        userIds: all],
                requestContentType : ContentType.JSON) as HttpResponseDecorator
        def dateAfter = new Date()

        then: "response is correct"
        assert response.status == 201

        and: "user has a new photo"
        def userImages = user.getAllImagesInfo()
        assert userImages.size() == 1
        assert userImages.get(0)["id"] != JSONNull.getInstance()
        assert userImages.get(0)["path"] == IMAGE_PATH_PREFIX + userImages.get(0)["id"]
        assert userImages.get(0)["thumbnailPath"] == THUMBNAIL_PATH_PREFIX + userImages.get(0)["id"]
        assert userImages.get(0)["ownerId"] == user.id
        assert Utils.checkTimestamp(dateBefore, userImages.get(0)["createdTime"] as String, dateAfter)

        and: "image data is correct"
        assert image == RequestUtils.getImage(user.token, IMAGE_PATH_PREFIX + userImages.get(0)["id"])
        assert Utils.toThumbnail(image) == RequestUtils.getImage(user.token, THUMBNAIL_PATH_PREFIX + userImages.get(0)["id"])

        where:
        all << [[], null]
    }

    def "send a photo to yourself"() {
        given: "user"
        def user = new TestUser().register()
        def base64Image = Utils.toBase64(DataGenerator.createValidImage())

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $user.token"],
                body:  [image: base64Image,
                        userIds: [user.id]],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400
    }

    def "photo send to friend with deleted profile"() {
        given: "user and friend with deleted profile"
        def user = new TestUser().register()
        def friend = new TestUser().register()
        user.sendFriendRequest(friend.id)
        friend.sendFriendRequest(user.id).delete()
        def base64Image = Utils.toBase64(DataGenerator.createValidImage())

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $user.token"],
                body:  [image: base64Image,
                        userIds: [friend.id]],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400
    }

    def "photo send to not friend"() {
        given: "two users"
        def user1 = new TestUser().register()
        def user2 = new TestUser().register()
        def base64Image = Utils.toBase64(DataGenerator.createValidImage())

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $user1.token"],
                body:  [image: base64Image,
                        userIds: [user2.id]],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400
    }

    def "photo send to user with incoming friend request"() {
        given: "user with outgoing friend request"
        def user1 = new TestUser().register()
        def user2 = new TestUser().register()
        user1.sendFriendRequest(user2.id)
        def base64Image = Utils.toBase64(DataGenerator.createValidImage())

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $user1.token"],
                body:  [image: base64Image,
                        userIds: [user2.id]],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400
    }

    def "photo send to user with outgoing friend request"() {
        given: "user with incoming friend request"
        def user1 = new TestUser().register()
        def user2 = new TestUser().register()
        user2.sendFriendRequest(user1.id)
        def base64Image = Utils.toBase64(DataGenerator.createValidImage())

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $user1.token"],
                body:  [image: base64Image,
                        userIds: [user2.id]],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400
    }

    def "photo send with invalid data"() {
        given: "two friends and invalid data"
        def user1 = new TestUser().register()
        def user2 = new TestUser().register()
        user1.sendFriendRequest(user2.id)
        user2.sendFriendRequest(user1.id)

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $user1.token"],
                body:  [image: invalidData,
                        userIds: [user2.id]],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400

        where:
        //TODO: Add big size
        invalidData << [null, "", ";", Base64.getEncoder().encodeToString("test".getBytes())]
    }

    def "photo send with invalid token"() {
        given: "invalid token"
        def token = UUID.randomUUID().toString()
        def base64Image = Utils.toBase64(DataGenerator.createValidImage())

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                body:  [image: base64Image],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 401
    }
}
