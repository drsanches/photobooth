package ru.drsanches.photobooth.friends

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import net.sf.json.JSONArray
import ru.drsanches.photobooth.utils.DataGenerator
import ru.drsanches.photobooth.utils.RequestUtils
import spock.lang.Specification

class TestRemoveFriendRequest extends Specification {

    String PATH = "/api/v1/friends/manage/delete"

    /**
     * user1 --req-> user2
     * user1 ---X--> user2
     */
    def "success outgoing friend request deletion"() {
        given: "two users and one side friend request"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        def username2 = DataGenerator.createValidUsername()
        def password2 = DataGenerator.createValidPassword()

        RequestUtils.registerUser(username1, password1, null)
        def userId2 = RequestUtils.registerUser(username2, password2, null)

        RequestUtils.sendFriendRequest(username1, password1, userId2)

        def token1 = RequestUtils.getToken(username1, password1)

        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token1"],
                body: ["userId": userId2],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200

        and: "the first user has correct relationships"
        assert RequestUtils.getIncomingRequests(username1, password1) == new JSONArray()
        assert RequestUtils.getOutgoingRequests(username1, password1) == new JSONArray()
        assert RequestUtils.getFriends(username1, password1) == new JSONArray()

        and: "the second user has correct relationships"
        assert RequestUtils.getIncomingRequests(username2, password2) == new JSONArray()
        assert RequestUtils.getOutgoingRequests(username2, password2) == new JSONArray()
        assert RequestUtils.getFriends(username2, password2) == new JSONArray()
    }

    /**
     * user1 <-req-- user1
     * user1 ---X--> user2
     */
    def "success incoming friend request deletion"() {
        given: "two users and one side friend request"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        def username2 = DataGenerator.createValidUsername()
        def password2 = DataGenerator.createValidPassword()

        def userId1 = RequestUtils.registerUser(username1, password1, null)
        def userId2 = RequestUtils.registerUser(username2, password2, null)

        RequestUtils.sendFriendRequest(username2, password2, userId1)

        def token1 = RequestUtils.getToken(username1, password1)

        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token1"],
                body: ["userId": userId2],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200

        and: "the first user has correct relationships"
        assert RequestUtils.getIncomingRequests(username1, password1) == new JSONArray()
        assert RequestUtils.getOutgoingRequests(username1, password1) == new JSONArray()
        assert RequestUtils.getFriends(username1, password1) == new JSONArray()

        and: "the second user has correct relationships"
        assert RequestUtils.getIncomingRequests(username2, password2) == new JSONArray()
        assert RequestUtils.getOutgoingRequests(username2, password2) == new JSONArray()
        assert RequestUtils.getFriends(username2, password2) == new JSONArray()
    }

    /**
     * friend1 ---X--> friend2
     */
    def "success friend deletion"() {
        given: "two friends"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        def username2 = DataGenerator.createValidUsername()
        def password2 = DataGenerator.createValidPassword()

        def userId1 = RequestUtils.registerUser(username1, password1, null)
        def userId2 = RequestUtils.registerUser(username2, password2, null)

        RequestUtils.sendFriendRequest(username1, password1, userId2)
        RequestUtils.sendFriendRequest(username2, password2, userId1)

        def token1 = RequestUtils.getToken(username1, password1)

        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token1"],
                body: ["userId": userId2],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200

        and: "the first user has correct relationships"
        assert RequestUtils.getIncomingRequests(username1, password1) == new JSONArray()
        assert RequestUtils.getOutgoingRequests(username1, password1) == new JSONArray()
        assert RequestUtils.getFriends(username1, password1) == new JSONArray()

        and: "the second user has correct relationships"
        assert RequestUtils.getIncomingRequests(username2, password2) == new JSONArray()
        assert RequestUtils.getOutgoingRequests(username2, password2) == new JSONArray()
        assert RequestUtils.getFriends(username2, password2) == new JSONArray()
    }

    /**
     * user <-req-- deleted
     * user ---X--> deleted
     */
    def "success incoming friend request deletion from deleted user"() {
        given: "user with incoming friend request from deleted user"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        def username2 = DataGenerator.createValidUsername()
        def password2 = DataGenerator.createValidPassword()

        def userId1 = RequestUtils.registerUser(username1, password1, null)
        def userId2 = RequestUtils.registerUser(username2, password2, null)

        RequestUtils.sendFriendRequest(username2, password2, userId1)

        def token1 = RequestUtils.getToken(username1, password1)

        RequestUtils.deleteUser(username2, password2)

        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token1"],
                body: ["userId": userId2],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200

        and: "the user has correct relationships"
        assert RequestUtils.getIncomingRequests(username1, password1) == new JSONArray()
        assert RequestUtils.getOutgoingRequests(username1, password1) == new JSONArray()
        assert RequestUtils.getFriends(username1, password1) == new JSONArray()
    }

    /**
     * friend ---X--> deleted friend
     */
    def "success friendship deletion for deleted user"() {
        given: "user with deleted friend"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        def username2 = DataGenerator.createValidUsername()
        def password2 = DataGenerator.createValidPassword()

        def userId1 = RequestUtils.registerUser(username1, password1, null)
        def userId2 = RequestUtils.registerUser(username2, password2, null)

        RequestUtils.sendFriendRequest(username1, password1, userId2)
        RequestUtils.sendFriendRequest(username2, password2, userId1)

        RequestUtils.deleteUser(username2, password2)

        def token1 = RequestUtils.getToken(username1, password1)

        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token1"],
                body: ["userId": userId2],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200

        and: "the user has correct relationships"
        assert RequestUtils.getIncomingRequests(username1, password1) == new JSONArray()
        assert RequestUtils.getOutgoingRequests(username1, password1) == new JSONArray()
        assert RequestUtils.getFriends(username1, password1) == new JSONArray()
    }

    /**
     * user1 ---X--> user2
     */
    def "success delete nonexistent request"() {
        given: "two users"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        def username2 = DataGenerator.createValidUsername()
        def password2 = DataGenerator.createValidPassword()

        RequestUtils.registerUser(username1, password1, null)
        def userId2 = RequestUtils.registerUser(username2, password2, null)

        def token1 = RequestUtils.getToken(username1, password1)

        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token1"],
                body: ["userId": userId2],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200

        and: "the first user has correct relationships"
        assert RequestUtils.getIncomingRequests(username1, password1) == new JSONArray()
        assert RequestUtils.getOutgoingRequests(username1, password1) == new JSONArray()
        assert RequestUtils.getFriends(username1, password1) == new JSONArray()

        and: "the second user has correct relationships"
        assert RequestUtils.getIncomingRequests(username2, password2) == new JSONArray()
        assert RequestUtils.getOutgoingRequests(username2, password2) == new JSONArray()
        assert RequestUtils.getFriends(username2, password2) == new JSONArray()
    }

    /**
     * user ---X--> user
     */
    def "delete friend request for yourself"() {
        given: "user"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()

        def userId1 = RequestUtils.registerUser(username1, password1, null)

        def token1 = RequestUtils.getToken(username1, password1)

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token1"],
                body: ["userId": userId1],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400

        and: "the first user has correct relationships"
        assert RequestUtils.getIncomingRequests(username1, password1) == new JSONArray()
        assert RequestUtils.getOutgoingRequests(username1, password1) == new JSONArray()
        assert RequestUtils.getFriends(username1, password1) == new JSONArray()
    }

    def "friend request deletion without userId"() {
        given: "two users and one side friend request"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        def username2 = DataGenerator.createValidUsername()
        def password2 = DataGenerator.createValidPassword()

        RequestUtils.registerUser(username1, password1, null)
        def userId2 = RequestUtils.registerUser(username2, password2, null)

        RequestUtils.sendFriendRequest(username1, password1, userId2)

        def token1 = RequestUtils.getToken(username1, password1)

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token1"],
                body: ["userId": empty],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400

        where:
        empty << [null, ""]
    }

    def "delete request for nonexistent user"() {
        given: "user"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username1, password1, null)
        def nonexistentId = UUID.randomUUID().toString()
        def token1 = RequestUtils.getToken(username1, password1)

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token1"],
                body: ["userId": nonexistentId],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400
    }

    def "delete request with invalid token"() {
        given: "two users"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        def username2 = DataGenerator.createValidUsername()
        def password2 = DataGenerator.createValidPassword()

        def userId1 = RequestUtils.registerUser(username1, password1, null)
        def userId2 = RequestUtils.registerUser(username2, password2, null)

        RequestUtils.sendFriendRequest(username1, password1, userId2)

        def token1 = UUID.randomUUID().toString()

        when: "sendRequest is called with invalid token"
        RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token1"],
                body: ["userId": userId2],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 401

        and: "the first user has correct relationships"
        assert RequestUtils.getIncomingRequests(username1, password1) == new JSONArray()
        assert RequestUtils.getFriends(username1, password1) == new JSONArray()
        def outgoingRequests = RequestUtils.getOutgoingRequests(username1, password1)
        assert outgoingRequests.size() == 1
        assert outgoingRequests.get(0)["id"] == userId2
        assert outgoingRequests.get(0)["username"] == username2

        and: "the second user has correct relationships"
        assert RequestUtils.getOutgoingRequests(username2, password2) == new JSONArray()
        assert RequestUtils.getFriends(username2, password2) == new JSONArray()
        def incomingRequests = RequestUtils.getIncomingRequests(username2, password2)
        assert incomingRequests.size() == 1
        assert incomingRequests.get(0)["id"] == userId1
        assert incomingRequests.get(0)["username"] == username1
    }
}