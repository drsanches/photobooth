package com.drsanches.photobooth.end2end.profile

import com.drsanches.photobooth.end2end.utils.RequestUtils
import com.drsanches.photobooth.end2end.utils.TestUser
import com.drsanches.photobooth.end2end.utils.Utils
import org.json.JSONObject
import spock.lang.Specification

class TestGetProfile extends Specification {

    String PATH = "/api/v1/app/profile/"

    def "successful user profile getting"() {
        given: "two users"
        def user1 = new TestUser().register()
        def user2 = new TestUser().register().fillProfile()

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH + user2.id,
                headers: [Authorization: "Bearer $user1.token"])

        then: "response is correct"
        assert response.status == 200
        assert response.data["id"] == user2.id
        assert response.data["username"] == user2.username
        assert response.data["name"] == user2.name
        assert response.data["status"] == user2.status
        assert response.data["imagePath"] == Utils.DEFAULT_IMAGE_PATH
        assert response.data["thumbnailPath"] == Utils.DEFAULT_THUMBNAIL_PATH
        assert response.data["relationship"] == "STRANGER"
    }

    def "successful incoming user profile getting"() {
        given: "two users"
        def user1 = new TestUser().register()
        def user2 = new TestUser().register().fillProfile().sendFriendRequest(user1.id)

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH + user2.id,
                headers: [Authorization: "Bearer $user1.token"])

        then: "response is correct"
        assert response.status == 200
        assert response.data["id"] == user2.id
        assert response.data["username"] == user2.username
        assert response.data["name"] == user2.name
        assert response.data["status"] == user2.status
        assert response.data["imagePath"] == Utils.DEFAULT_IMAGE_PATH
        assert response.data["thumbnailPath"] == Utils.DEFAULT_THUMBNAIL_PATH
        assert response.data["relationship"] == "INCOMING_FRIEND_REQUEST"
    }

    def "successful outgoing user profile getting"() {
        given: "two users"
        def user1 = new TestUser().register()
        def user2 = new TestUser().register().fillProfile()
        user1.sendFriendRequest(user2.id)

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH + user2.id,
                headers: [Authorization: "Bearer $user1.token"])

        then: "response is correct"
        assert response.status == 200
        assert response.data["id"] == user2.id
        assert response.data["username"] == user2.username
        assert response.data["name"] == user2.name
        assert response.data["status"] == user2.status
        assert response.data["imagePath"] == Utils.DEFAULT_IMAGE_PATH
        assert response.data["thumbnailPath"] == Utils.DEFAULT_THUMBNAIL_PATH
        assert response.data["relationship"] == "OUTGOING_FRIEND_REQUEST"
    }

    def "successful friend profile getting"() {
        given: "two users"
        def user1 = new TestUser().register()
        def user2 = new TestUser().register().fillProfile().sendFriendRequest(user1.id)
        user1.sendFriendRequest(user2.id)

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH + user2.id,
                headers: [Authorization: "Bearer $user1.token"])

        then: "response is correct"
        assert response.status == 200
        assert response.data["id"] == user2.id
        assert response.data["username"] == user2.username
        assert response.data["name"] == user2.name
        assert response.data["status"] == user2.status
        assert response.data["imagePath"] == Utils.DEFAULT_IMAGE_PATH
        assert response.data["thumbnailPath"] == Utils.DEFAULT_THUMBNAIL_PATH
        assert response.data["relationship"] == "FRIEND"
    }

    def "successful current profile getting"() {
        given: "two users"
        def user1 = new TestUser().register().fillProfile()

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH + user1.id,
                headers: [Authorization: "Bearer $user1.token"])

        then: "response is correct"
        assert response.status == 200
        assert response.data["id"] == user1.id
        assert response.data["username"] == user1.username
        assert response.data["name"] == user1.name
        assert response.data["status"] == user1.status
        assert response.data["imagePath"] == Utils.DEFAULT_IMAGE_PATH
        assert response.data["thumbnailPath"] == Utils.DEFAULT_THUMBNAIL_PATH
        assert response.data["relationship"] == "CURRENT"
    }

    def "get deleted user profile"() {
        given: "user and deleted user"
        def user1 = new TestUser().register()
        def user2 = new TestUser().register().delete()

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH + user2.id,
                headers: [Authorization: "Bearer $user1.token"])

        then: "response is correct"
        assert response.status == 404
        assert Utils.validateErrorResponse(response.data as JSONObject, "user.not.found", null)
    }

    def "get nonexistent user profile"() {
        given: "user and nonexistent user id"
        def user = new TestUser().register()
        def nonexistentId = UUID.randomUUID().toString()

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH + nonexistentId,
                headers: [Authorization: "Bearer $user.token"])

        then: "response is correct"
        assert response.status == 404
        assert Utils.validateErrorResponse(response.data as JSONObject, "user.not.found", null)
    }

    def "get user profile with invalid token"() {
        given: "user id and invalid token"
        def userId = UUID.randomUUID().toString()
        def token = UUID.randomUUID().toString()

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH + userId,
                headers: [Authorization: "Bearer $token"])

        then: "response is correct"
        assert response.status == 401
        assert Utils.validateErrorResponse(response.data as JSONObject, "wrong.token", null)
    }
}
