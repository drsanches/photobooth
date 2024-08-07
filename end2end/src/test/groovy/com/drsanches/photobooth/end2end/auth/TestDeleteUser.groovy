package com.drsanches.photobooth.end2end.auth

import com.drsanches.photobooth.end2end.utils.RequestUtils
import com.drsanches.photobooth.end2end.utils.TestUser
import com.drsanches.photobooth.end2end.utils.Utils
import org.json.JSONObject
import spock.lang.Specification

class TestDeleteUser extends Specification {

    String PATH = "/api/v1/auth/account"

    def "success user deleting"() {
        given: "user with friend and friend requests and tokens"
        def user = new TestUser().register()
        def friend = new TestUser().register()
        def outgoing = new TestUser().register()
        def incoming = new TestUser().register()
        user.sendFriendRequest(friend.id)
        friend.sendFriendRequest(user.id)
        user.sendFriendRequest(outgoing.id)
        incoming.sendFriendRequest(user.id)
        def oldToken = RequestUtils.getToken(user.username, user.password)
        def token = RequestUtils.getToken(user.username, user.password)

        when: "request is sent"
        def response = RequestUtils.getRestClient().delete(
                path: PATH,
                headers: [Authorization: "Bearer $token"])

        then: "response is correct"
        assert response.status == 200
        assert response.data["result"] == JSONObject.NULL
        assert response.data["with2FA"] == false

        and: "user was deleted"
        assert RequestUtils.getAuthInfo(user.username, user.password) == null

        and: "user profile was deleted"
        assert RequestUtils.getUserProfile(user.username, user.password) == null

        and: "token is invalid"
        assert RequestUtils.getAuthInfo(token) == null
        assert RequestUtils.getToken(user.username, user.password) == null

        and: "old token is invalid"
        assert RequestUtils.getAuthInfo(oldToken) == null

        and: "new user with old user credentials has different token"
        RequestUtils.registerUser(user.username, user.password, user.email)
        assert RequestUtils.getAuthInfo(user.username, user.password) != null
        assert RequestUtils.getToken(user.username, user.password) != token

        and: "friend's relationships is correct"
        assert friend.getIncomingFriendRequests().size() == 0
        assert friend.getOutgoingFriendRequests().size() == 0
        def friends = friend.getFriends()
        assert friends.size() == 1
        assert friends[0]["id"] == user.id
        assert friends[0]["username"] == JSONObject.NULL
        assert friends[0]["name"] == JSONObject.NULL
        assert friends[0]["status"] == JSONObject.NULL
        assert friends[0]["imagePath"] == Utils.DELETED_IMAGE_PATH
        assert friends[0]["thumbnailPath"] == Utils.DELETED_THUMBNAIL_PATH

        and: "incoming user relationships is correct"
        assert incoming.getIncomingFriendRequests().size() == 0
        assert incoming.getFriends().size() == 0
        def outgoingRequests = incoming.getOutgoingFriendRequests()
        assert outgoingRequests.size() == 1
        assert outgoingRequests[0]["id"] == user.id
        assert outgoingRequests[0]["username"] == JSONObject.NULL
        assert outgoingRequests[0]["name"] == JSONObject.NULL
        assert outgoingRequests[0]["status"] == JSONObject.NULL
        assert outgoingRequests[0]["imagePath"] == Utils.DELETED_IMAGE_PATH
        assert outgoingRequests[0]["thumbnailPath"] == Utils.DELETED_THUMBNAIL_PATH

        and: "outgoing user relationships is correct"
        assert outgoing.getOutgoingFriendRequests().size() == 0
        assert outgoing.getFriends().size() == 0
        def incomingRequests = outgoing.getIncomingFriendRequests()
        assert incomingRequests.size() == 1
        assert incomingRequests[0]["id"] == user.id
        assert incomingRequests[0]["username"] == JSONObject.NULL
        assert incomingRequests[0]["name"] == JSONObject.NULL
        assert incomingRequests[0]["status"] == JSONObject.NULL
        assert incomingRequests[0]["imagePath"] == Utils.DELETED_IMAGE_PATH
        assert incomingRequests[0]["thumbnailPath"] == Utils.DELETED_THUMBNAIL_PATH
    }

    def "delete user with invalid token"() {
        given: "invalid token"
        def token = UUID.randomUUID().toString()

        when: "request is sent"
        def response = RequestUtils.getRestClient().delete(
                path: PATH,
                headers: [Authorization: "Bearer $token"])

        then: "response is correct"
        assert response.status == 401
        assert Utils.validateErrorResponse(response.data as JSONObject, "Wrong token", null)
    }
}
