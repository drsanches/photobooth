package com.drsanches.photobooth.end2end.friends

import com.drsanches.photobooth.end2end.utils.DataGenerator
import com.drsanches.photobooth.end2end.utils.RequestUtils
import com.drsanches.photobooth.end2end.utils.TestUser
import com.drsanches.photobooth.end2end.utils.Utils
import org.json.JSONArray
import org.json.JSONObject
import spock.lang.Specification

class TestGetFriends extends Specification {

    String PATH = "/api/v1/app/friends"

    def "success friends getting"() {
        given: "two friends"
        def user1 = new TestUser().register()
        def user2 = new TestUser().register().fillProfile()
        user1.sendFriendRequest(user2.id)
        user2.sendFriendRequest(user1.id)

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH,
                headers: [Authorization: "Bearer $user1.token"])

        then: "response is correct"
        assert response.status == 200
        def body = response.data as JSONArray
        assert body.size() == 1
        assert body[0]["id"] == user2.id
        assert body[0]["username"] == user2.username
        assert body[0]["name"] == user2.name
        assert body[0]["status"] == user2.status
        assert body[0]["imagePath"] == Utils.DEFAULT_IMAGE_PATH
        assert body[0]["thumbnailPath"] == Utils.DEFAULT_THUMBNAIL_PATH
        assert body[0]["relationship"] == "FRIEND"
    }

    def "success friends getting with paging"() {
        given: "two friends"
        def user = new TestUser().register()
        def friend1 = new TestUser().register(DataGenerator.createValidUsername("1")).fillProfile()
        def friend2 = new TestUser().register(DataGenerator.createValidUsername("2")).fillProfile()
        def friend3 = new TestUser().register(DataGenerator.createValidUsername("3")).fillProfile()
        user.sendFriendRequest(friend1.id)
        friend1.sendFriendRequest(user.id)
        user.sendFriendRequest(friend2.id)
        friend2.sendFriendRequest(user.id)
        user.sendFriendRequest(friend3.id)
        friend3.sendFriendRequest(user.id)

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH + "?page=1&size=1",
                headers: [Authorization: "Bearer $user.token"])

        then: "response is correct"
        assert response.status == 200
        def body = response.data as JSONArray
        assert body.size() == 1
        assert body[0]["id"] == friend2.id
        assert body[0]["username"] == friend2.username
        assert body[0]["name"] == friend2.name
        assert body[0]["status"] == friend2.status
        assert body[0]["imagePath"] == Utils.DEFAULT_IMAGE_PATH
        assert body[0]["thumbnailPath"] == Utils.DEFAULT_THUMBNAIL_PATH
        assert body[0]["relationship"] == "FRIEND"
    }

    def "success empty friends getting"() {
        given: "three users"
        def user1 = new TestUser().register()
        def user2 = new TestUser().register()
        def user3 = new TestUser().register()
        user1.sendFriendRequest(user2.id)
        user3.sendFriendRequest(user1.id)

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH,
                headers: [Authorization: "Bearer $user1.token"])

        then: "response is correct"
        assert response.status == 200
        assert (response.data as JSONArray).size() == 0
    }

    def "success friend with deleted profile getting"() {
        given: "user and friend with deleted profile"
        def user1 = new TestUser().register()
        def user2 = new TestUser().register()
        user1.sendFriendRequest(user2.id)
        user2.sendFriendRequest(user1.id)
        user2.delete()

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH,
                headers: [Authorization: "Bearer $user1.token"])

        then: "response is correct"
        assert response.status == 200
        def body = response.data as JSONArray
        assert body.size() == 1
        assert body[0]["id"] == user2.id
        assert body[0]["username"] == JSONObject.NULL
        assert body[0]["name"] == JSONObject.NULL
        assert body[0]["status"] == JSONObject.NULL
        assert body[0]["imagePath"] == Utils.DELETED_IMAGE_PATH
        assert body[0]["thumbnailPath"] == Utils.DELETED_THUMBNAIL_PATH
        assert body[0]["relationship"] == "FRIEND"
    }

    def "get friends with invalid token"() {
        given: "invalid token"
        def token = UUID.randomUUID().toString()

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH,
                headers: [Authorization: "Bearer $token"])

        then: "response is correct"
        assert response.status == 401
        assert Utils.validateErrorResponse(response.data as JSONObject, "wrong.token", null)
    }
}
