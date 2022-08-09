package ru.drsanches.photobooth.auth

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import net.sf.json.JSONArray
import net.sf.json.JSONNull
import ru.drsanches.photobooth.utils.DataGenerator
import ru.drsanches.photobooth.utils.RequestUtils
import ru.drsanches.photobooth.utils.TestUser
import spock.lang.Specification

class TestDeleteUser extends Specification {

    String PATH = "/api/v1/auth/deleteUser"

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
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                body:  [password: user.password],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200

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
        assert friend.getIncomingFriendRequests() == new JSONArray()
        assert friend.getOutgoingFriendRequests() == new JSONArray()
        JSONArray friends = friend.getFriends()
        assert friends.size() == 1
        assert friends.get(0)["id"] == user.id
        assert friends.get(0)["username"] == JSONNull.getInstance()
        assert friends.get(0)["name"] == JSONNull.getInstance()
        assert friends.get(0)["status"] == JSONNull.getInstance()

        and: "incoming user relationships is correct"
        assert incoming.getIncomingFriendRequests() == new JSONArray()
        assert incoming.getFriends() == new JSONArray()
        JSONArray outgoingRequests = incoming.getOutgoingFriendRequests()
        assert outgoingRequests.size() == 1
        assert outgoingRequests.get(0)["id"] == user.id
        assert outgoingRequests.get(0)["username"] == JSONNull.getInstance()
        assert outgoingRequests.get(0)["name"] == JSONNull.getInstance()
        assert outgoingRequests.get(0)["status"] == JSONNull.getInstance()

        and: "outgoing user relationships is correct"
        assert outgoing.getOutgoingFriendRequests() == new JSONArray()
        assert outgoing.getFriends() == new JSONArray()
        JSONArray incomingRequests = outgoing.getIncomingFriendRequests()
        assert incomingRequests.size() == 1
        assert incomingRequests.get(0)["id"] == user.id
        assert incomingRequests.get(0)["username"] == JSONNull.getInstance()
        assert incomingRequests.get(0)["name"] == JSONNull.getInstance()
        assert incomingRequests.get(0)["status"] == JSONNull.getInstance()
    }

    def "user deleting without password"() {
        given: "user"
        def user = new TestUser().register()

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $user.token"],
                body:  [password: empty],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400

        and: "user was not deleted"
        assert user.getAuthInfo() != null

        where:
        empty << [null, ""]
    }

    def "user deleting with invalid password"() {
        given: "user and invalid password"
        def user = new TestUser().register()
        def invalidPassword = DataGenerator.createValidPassword()

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $user.token"],
                body:  [password: invalidPassword],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 401

        and: "user was not deleted"
        assert user.getAuthInfo() != null
    }

    def "delete user with invalid token"() {
        given: "invalid token"
        def token = UUID.randomUUID().toString()

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 401
    }
}
