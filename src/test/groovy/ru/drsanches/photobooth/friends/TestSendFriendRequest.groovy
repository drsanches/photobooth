package ru.drsanches.photobooth.friends

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import net.sf.json.JSONArray
import ru.drsanches.photobooth.utils.DataGenerator
import ru.drsanches.photobooth.utils.RequestUtils
import spock.lang.Specification

class TestSendFriendRequest extends Specification {

    String PATH = "/api/v1/friends/manage/add"

    /**
     * user1 -req-> user2
     */
    def "success one side friend request sending"() {
        given: "two users"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        def name1 = DataGenerator.createValidName()
        def status1 = DataGenerator.createValidStatus()
        def username2 = DataGenerator.createValidUsername()
        def password2 = DataGenerator.createValidPassword()
        def name2 = DataGenerator.createValidName()
        def status2 = DataGenerator.createValidStatus()

        def userId1 = RequestUtils.registerUser(username1, password1, null)
        def userId2 = RequestUtils.registerUser(username2, password2, null)

        def token1 = RequestUtils.getToken(username1, password1)
        def token2 = RequestUtils.getToken(username2, password2)

        RequestUtils.changeUserProfile(token1, name1, status1)
        RequestUtils.changeUserProfile(token2, name2, status2)

        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token1"],
                body: ["userId": userId2],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 201

        and: "the first user has correct relationships"
        assert RequestUtils.getIncomingRequests(username1, password1) == new JSONArray()
        assert RequestUtils.getFriends(username1, password1) == new JSONArray()
        def outgoingRequests = RequestUtils.getOutgoingRequests(username1, password1)
        assert outgoingRequests.size() == 1
        assert outgoingRequests.get(0)["id"] == userId2
        assert outgoingRequests.get(0)["username"] == username2
        assert outgoingRequests.get(0)["name"] == name2
        assert outgoingRequests.get(0)["status"] == status2

        and: "the second user has correct relationships"
        assert RequestUtils.getOutgoingRequests(username2, password2) == new JSONArray()
        assert RequestUtils.getFriends(username2, password2) == new JSONArray()
        def incomingRequests = RequestUtils.getIncomingRequests(username2, password2)
        assert incomingRequests.size() == 1
        assert incomingRequests.get(0)["id"] == userId1
        assert incomingRequests.get(0)["username"] == username1
        assert incomingRequests.get(0)["name"] == name1
        assert incomingRequests.get(0)["status"] == status1
    }

    /**
     * user2 -req-> user1
     * user1 -req-> user2
     */
    def "success two side friend request sending"() {
        given: "two users"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        def name1 = DataGenerator.createValidName()
        def status1 = DataGenerator.createValidStatus()
        def username2 = DataGenerator.createValidUsername()
        def password2 = DataGenerator.createValidPassword()
        def name2 = DataGenerator.createValidName()
        def status2 = DataGenerator.createValidStatus()

        def userId1 = RequestUtils.registerUser(username1, password1, null)
        def userId2 = RequestUtils.registerUser(username2, password2, null)

        RequestUtils.sendFriendRequest(username2, password2, userId1)

        def token1 = RequestUtils.getToken(username1, password1)
        def token2 = RequestUtils.getToken(username2, password2)

        RequestUtils.changeUserProfile(token1, name1, status1)
        RequestUtils.changeUserProfile(token2, name2, status2)

        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token1"],
                body: ["userId": userId2],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 201

        and: "the first user has correct relationships"
        assert RequestUtils.getIncomingRequests(username1, password1) == new JSONArray()
        assert RequestUtils.getOutgoingRequests(username1, password1) == new JSONArray()
        def friends1 = RequestUtils.getFriends(username1, password1)
        assert friends1.size() == 1
        assert friends1.get(0)["id"] == userId2
        assert friends1.get(0)["username"] == username2
        assert friends1.get(0)["name"] == name2
        assert friends1.get(0)["status"] == status2

        and: "the second user has correct relationships"
        assert RequestUtils.getIncomingRequests(username2, password2) == new JSONArray()
        assert RequestUtils.getOutgoingRequests(username2, password2) == new JSONArray()
        def friends2 = RequestUtils.getFriends(username2, password2)
        assert friends2.size() == 1
        assert friends2.get(0)["id"] == userId1
        assert friends2.get(0)["username"] == username1
        assert friends2.get(0)["name"] == name1
        assert friends2.get(0)["status"] == status1
    }

    /**
     * user1 -req-> user2
     * user1 -req-> user2
     */
    def "success second time friend request sending to user"() {
        given: "two users with one side friend request"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        def name1 = DataGenerator.createValidName()
        def status1 = DataGenerator.createValidStatus()
        def username2 = DataGenerator.createValidUsername()
        def password2 = DataGenerator.createValidPassword()
        def name2 = DataGenerator.createValidName()
        def status2 = DataGenerator.createValidStatus()

        def userId1 = RequestUtils.registerUser(username1, password1, null)
        def userId2 = RequestUtils.registerUser(username2, password2, null)

        def token1 = RequestUtils.getToken(username1, password1)
        def token2 = RequestUtils.getToken(username2, password2)

        RequestUtils.changeUserProfile(token1, name1, status1)
        RequestUtils.changeUserProfile(token2, name2, status2)

        RequestUtils.sendFriendRequest(username1, password1, userId2)

        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token1"],
                body: ["userId": userId2],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 201

        and: "the first user relationship has not changed"
        assert RequestUtils.getIncomingRequests(username1, password1) == new JSONArray()
        assert RequestUtils.getFriends(username1, password1) == new JSONArray()
        def outgoingRequests = RequestUtils.getOutgoingRequests(username1, password1)
        assert outgoingRequests.size() == 1
        assert outgoingRequests.get(0)["id"] == userId2
        assert outgoingRequests.get(0)["username"] == username2
        assert outgoingRequests.get(0)["name"] == name2
        assert outgoingRequests.get(0)["status"] == status2

        and: "the second user relationship has not changed"
        assert RequestUtils.getOutgoingRequests(username2, password2) == new JSONArray()
        assert RequestUtils.getFriends(username2, password2) == new JSONArray()
        def incomingRequests = RequestUtils.getIncomingRequests(username2, password2)
        assert incomingRequests.size() == 1
        assert incomingRequests.get(0)["id"] == userId1
        assert incomingRequests.get(0)["username"] == username1
        assert incomingRequests.get(0)["name"] == name1
        assert incomingRequests.get(0)["status"] == status1
    }

    /**
     * user -req-> friend
     */
    def "success friend request sending to friend"() {
        given: "two users with one side friend request"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        def name1 = DataGenerator.createValidName()
        def status1 = DataGenerator.createValidStatus()
        def username2 = DataGenerator.createValidUsername()
        def password2 = DataGenerator.createValidPassword()
        def name2 = DataGenerator.createValidName()
        def status2 = DataGenerator.createValidStatus()

        def userId1 = RequestUtils.registerUser(username1, password1, null)
        def userId2 = RequestUtils.registerUser(username2, password2, null)

        def token1 = RequestUtils.getToken(username1, password1)
        def token2 = RequestUtils.getToken(username2, password2)

        RequestUtils.changeUserProfile(token1, name1, status1)
        RequestUtils.changeUserProfile(token2, name2, status2)

        RequestUtils.sendFriendRequest(username2, password2, userId1)
        RequestUtils.sendFriendRequest(username1, password1, userId2)

        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token1"],
                body: ["userId": userId2],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 201

        and: "the first user relationship has not changed"
        assert RequestUtils.getIncomingRequests(username1, password1) == new JSONArray()
        assert RequestUtils.getOutgoingRequests(username1, password1) == new JSONArray()
        def friends1 = RequestUtils.getFriends(username1, password1)
        assert friends1.size() == 1
        assert friends1.get(0)["id"] == userId2
        assert friends1.get(0)["username"] == username2
        assert friends1.get(0)["name"] == name2
        assert friends1.get(0)["status"] == status2

        and: "the second user relationship has not changed"
        assert RequestUtils.getIncomingRequests(username2, password2) == new JSONArray()
        assert RequestUtils.getOutgoingRequests(username2, password2) == new JSONArray()
        def friends2 = RequestUtils.getFriends(username2, password2)
        assert friends2.size() == 1
        assert friends2.get(0)["id"] == userId1
        assert friends2.get(0)["username"] == username1
        assert friends2.get(0)["name"] == name1
        assert friends2.get(0)["status"] == status1
    }

    def "send friend request without userId"() {
        given: "two users"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()

        RequestUtils.registerUser(username1, password1, null)

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

    def "send friend request to deleted user"() {
        given: "user and deleted user"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        def username2 = DataGenerator.createValidUsername()
        def password2 = DataGenerator.createValidPassword()

        RequestUtils.registerUser(username1, password1, null)
        def userId2 = RequestUtils.registerUser(username2, password2, null)

        def token = RequestUtils.getToken(username1, password1)

        RequestUtils.deleteUser(username2, password2)

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                body: ["userId": userId2],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400
    }

    def "send friend request to deleted friend"() {
        given: "user and deleted user"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        def username2 = DataGenerator.createValidUsername()
        def password2 = DataGenerator.createValidPassword()

        def userId1 = RequestUtils.registerUser(username1, password1, null)
        def userId2 = RequestUtils.registerUser(username2, password2, null)

        RequestUtils.sendFriendRequest(username2, password2, userId1)
        RequestUtils.sendFriendRequest(username1, password1, userId2)

        def token = RequestUtils.getToken(username1, password1)

        RequestUtils.deleteUser(username2, password2)

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                body: ["userId": userId2],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400
    }

    def "send friend request to nonexistent user"() {
        given: "user"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username, password, null)
        def token = RequestUtils.getToken(username, password)
        def nonexistentId = UUID.randomUUID().toString()

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                body: ["userId": nonexistentId],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400
    }

    def "send friend request to current user"() {
        given: "user"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        def userId = RequestUtils.registerUser(username, password, null)
        def token = RequestUtils.getToken(username, password)

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                body: ["userId": userId],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400
    }

    def "send friend request with invalid token"() {
        given: "two users"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        def username2 = DataGenerator.createValidUsername()
        def password2 = DataGenerator.createValidPassword()

        RequestUtils.registerUser(username1, password1, null)
        def userId2 = RequestUtils.registerUser(username2, password2, null)

        def token1 = UUID.randomUUID().toString()

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token1"],
                body: ["userId": userId2],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 401

        and: "users has no relationships"
        assert RequestUtils.getIncomingRequests(username1, password1) == new JSONArray()
        assert RequestUtils.getOutgoingRequests(username1, password1) == new JSONArray()
        assert RequestUtils.getFriends(username1, password1) == new JSONArray()
        assert RequestUtils.getIncomingRequests(username2, password2) == new JSONArray()
        assert RequestUtils.getOutgoingRequests(username2, password2) == new JSONArray()
        assert RequestUtils.getFriends(username2, password2) == new JSONArray()
    }
}
