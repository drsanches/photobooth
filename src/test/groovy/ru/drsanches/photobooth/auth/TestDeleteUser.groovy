package ru.drsanches.photobooth.auth

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import net.sf.json.JSONArray
import net.sf.json.JSONNull
import ru.drsanches.photobooth.utils.DataGenerator
import ru.drsanches.photobooth.utils.RequestUtils
import spock.lang.Specification

class TestDeleteUser extends Specification {

    String PATH = "/api/v1/auth/deleteUser"

    def "success user deleting"() {
        given: "registered user with friend and friend requests, password and token"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        def friendUsername = DataGenerator.createValidUsername()
        def friendPassword = DataGenerator.createValidPassword()
        def outgoingUsername = DataGenerator.createValidUsername()
        def outgoingPassword = DataGenerator.createValidPassword()
        def incomingUsername = DataGenerator.createValidUsername()
        def incomingPassword = DataGenerator.createValidPassword()

        def userId = RequestUtils.registerUser(username, password, null)
        def friendId = RequestUtils.registerUser(friendUsername, friendPassword, null)
        def outgoingId = RequestUtils.registerUser(outgoingUsername, outgoingPassword, null)
        RequestUtils.registerUser(incomingUsername, incomingPassword, null)

        RequestUtils.sendFriendRequest(username, password, friendId)
        RequestUtils.sendFriendRequest(friendUsername, friendPassword, userId)
        RequestUtils.sendFriendRequest(username, password, outgoingId)
        RequestUtils.sendFriendRequest(incomingUsername, incomingPassword, userId)

        def oldToken = RequestUtils.getToken(username, password)
        def token = RequestUtils.getToken(username, password)

        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                body:  [password: password],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200

        and: "user was deleted"
        assert RequestUtils.getAuthInfo(username, password) == null

        and: "user profile was deleted"
        assert RequestUtils.getUserProfile(username, password) == null

        and: "token is invalid"
        assert RequestUtils.getAuthInfo(token) == null
        assert RequestUtils.getToken(username, password) == null

        and: "old token is invalid"
        assert RequestUtils.getAuthInfo(oldToken) == null

        and: "new user with old user credentials has different token"
        RequestUtils.registerUser(username, password, null)
        assert RequestUtils.getAuthInfo(username, password) != null
        assert RequestUtils.getToken(username, password) != token

        and: "friend's relationships is correct"
        assert RequestUtils.getIncomingRequests(friendUsername, friendPassword) == new JSONArray()
        assert RequestUtils.getOutgoingRequests(friendUsername, friendPassword) == new JSONArray()
        JSONArray friends = RequestUtils.getFriends(friendUsername, friendPassword)
        assert friends.size() == 1
        assert friends.get(0)["id"] == userId
        assert friends.get(0)["username"] == JSONNull.getInstance()
        assert friends.get(0)["name"] == JSONNull.getInstance()
        assert friends.get(0)["status"] == JSONNull.getInstance()

        and: "incoming user relationships is correct"
        assert RequestUtils.getIncomingRequests(incomingUsername, incomingPassword) == new JSONArray()
        assert RequestUtils.getFriends(incomingUsername, incomingPassword) == new JSONArray()
        JSONArray outgoing = RequestUtils.getOutgoingRequests(incomingUsername, incomingPassword)
        assert outgoing.size() == 1
        assert outgoing.get(0)["id"] == userId
        assert outgoing.get(0)["username"] == JSONNull.getInstance()
        assert outgoing.get(0)["name"] == JSONNull.getInstance()
        assert outgoing.get(0)["status"] == JSONNull.getInstance()

        and: "outgoing user relationships is correct"
        assert RequestUtils.getOutgoingRequests(outgoingUsername, outgoingPassword) == new JSONArray()
        assert RequestUtils.getFriends(outgoingUsername, outgoingPassword) == new JSONArray()
        JSONArray incoming = RequestUtils.getIncomingRequests(outgoingUsername, outgoingPassword)
        assert incoming.size() == 1
        assert incoming.get(0)["id"] == userId
        assert incoming.get(0)["username"] == JSONNull.getInstance()
        assert incoming.get(0)["name"] == JSONNull.getInstance()
        assert incoming.get(0)["status"] == JSONNull.getInstance()
    }

    def "user deleting without password"() {
        given: "registered user, token"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username, password, null)
        def token = RequestUtils.getToken(username, password)

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                body:  [password: empty],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400

        and: "user was not deleted"
        assert RequestUtils.getAuthInfo(token) != null

        where:
        empty << [null, ""]
    }

    def "user deleting with invalid password"() {
        given: "registered user, token and invalid password"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username, password, null)
        def token = RequestUtils.getToken(username, password)
        def invalidPassword = DataGenerator.createValidPassword()

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                body:  [password: invalidPassword],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 401

        and: "user was not deleted"
        assert RequestUtils.getAuthInfo(token) != null
    }

    def "delete user with invalid token"() {
        given: "registered user, password and invalid token"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username, password, null)
        def token = UUID.randomUUID().toString()

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                body:  [password: password],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 401

        and: "user was not deleted"
        assert RequestUtils.getAuthInfo(username, password) != null
    }
}
