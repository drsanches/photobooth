package ru.drsanches.photobooth.friends

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import net.sf.json.JSONArray
import ru.drsanches.photobooth.utils.RequestUtils
import ru.drsanches.photobooth.utils.TestUser
import ru.drsanches.photobooth.utils.Utils
import spock.lang.Specification

class TestSendFriendRequest extends Specification {

    String PATH = "/api/v1/friends/manage/add"

    /**
     * user1 -req-> user2
     */
    def "success one side friend request sending"() {
        given: "two users"
        def user1 = new TestUser().register().fillProfile()
        def user2 = new TestUser().register().fillProfile()

        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $user1.token"],
                body: ["userId": user2.id],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 201

        and: "the first user has correct relationships"
        assert user1.getIncomingFriendRequests() == new JSONArray()
        assert user1.getFriends() == new JSONArray()
        def outgoingRequests = user1.getOutgoingFriendRequests()
        assert outgoingRequests.size() == 1
        assert outgoingRequests.get(0)["id"] == user2.id
        assert outgoingRequests.get(0)["username"] == user2.username
        assert outgoingRequests.get(0)["name"] == user2.name
        assert outgoingRequests.get(0)["status"] == user2.status
        assert outgoingRequests.get(0)["imagePath"] == Utils.getDefaultImagePath()

        and: "the second user has correct relationships"
        assert user2.getOutgoingFriendRequests() == new JSONArray()
        assert user2.getFriends() == new JSONArray()
        def incomingRequests = user2.getIncomingFriendRequests()
        assert incomingRequests.size() == 1
        assert incomingRequests.get(0)["id"] == user1.id
        assert incomingRequests.get(0)["username"] == user1.username
        assert incomingRequests.get(0)["name"] == user1.name
        assert incomingRequests.get(0)["status"] == user1.status
        assert incomingRequests.get(0)["imagePath"] == Utils.getDefaultImagePath()
    }

    /**
     * user2 -req-> user1
     * user1 -req-> user2
     */
    def "success two side friend request sending"() {
        given: "two users"
        def user1 = new TestUser().register().fillProfile()
        def user2 = new TestUser().register().fillProfile().sendFriendRequest(user1.id)

        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $user1.token"],
                body: ["userId": user2.id],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 201

        and: "the first user has correct relationships"
        assert user1.getIncomingFriendRequests() == new JSONArray()
        assert user1.getOutgoingFriendRequests() == new JSONArray()
        def friends1 = user1.getFriends()
        assert friends1.size() == 1
        assert friends1.get(0)["id"] == user2.id
        assert friends1.get(0)["username"] == user2.username
        assert friends1.get(0)["name"] == user2.name
        assert friends1.get(0)["status"] == user2.status
        assert friends1.get(0)["imagePath"] == Utils.getDefaultImagePath()

        and: "the second user has correct relationships"
        assert user2.getIncomingFriendRequests() == new JSONArray()
        assert user2.getOutgoingFriendRequests() == new JSONArray()
        def friends2 = user2.getFriends()
        assert friends2.size() == 1
        assert friends2.get(0)["id"] == user1.id
        assert friends2.get(0)["username"] == user1.username
        assert friends2.get(0)["name"] == user1.name
        assert friends2.get(0)["status"] == user1.status
        assert friends2.get(0)["imagePath"] == Utils.getDefaultImagePath()
    }

    /**
     * user1 -req-> user2
     * user1 -req-> user2
     */
    def "success second time friend request sending to user"() {
        given: "two users"
        def user1 = new TestUser().register().fillProfile()
        def user2 = new TestUser().register().fillProfile()
        user1.sendFriendRequest(user2.id)

        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $user1.token"],
                body: ["userId": user2.id],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 201

        and: "the first user relationship has not changed"
        assert user1.getIncomingFriendRequests() == new JSONArray()
        assert user1.getFriends() == new JSONArray()
        def outgoingRequests = user1.getOutgoingFriendRequests()
        assert outgoingRequests.size() == 1
        assert outgoingRequests.get(0)["id"] == user2.id
        assert outgoingRequests.get(0)["username"] == user2.username
        assert outgoingRequests.get(0)["name"] == user2.name
        assert outgoingRequests.get(0)["status"] == user2.status
        assert outgoingRequests.get(0)["imagePath"] == Utils.getDefaultImagePath()

        and: "the second user relationship has not changed"
        assert user2.getOutgoingFriendRequests() == new JSONArray()
        assert user2.getFriends() == new JSONArray()
        def incomingRequests = user2.getIncomingFriendRequests()
        assert incomingRequests.size() == 1
        assert incomingRequests.get(0)["id"] == user1.id
        assert incomingRequests.get(0)["username"] == user1.username
        assert incomingRequests.get(0)["name"] == user1.name
        assert incomingRequests.get(0)["status"] == user1.status
        assert incomingRequests.get(0)["imagePath"] == Utils.getDefaultImagePath()
    }

    /**
     * user -req-> friend
     */
    def "success friend request sending to friend"() {
        given: "two users"
        def user1 = new TestUser().register().fillProfile()
        def user2 = new TestUser().register().fillProfile()
        user2.sendFriendRequest(user1.id)
        user1.sendFriendRequest(user2.id)

        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $user1.token"],
                body: ["userId": user2.id],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 201

        and: "the first user relationship has not changed"
        assert user1.getIncomingFriendRequests() == new JSONArray()
        assert user1.getOutgoingFriendRequests() == new JSONArray()
        def friends1 = user1.getFriends()
        assert friends1.size() == 1
        assert friends1.get(0)["id"] == user2.id
        assert friends1.get(0)["username"] == user2.username
        assert friends1.get(0)["name"] == user2.name
        assert friends1.get(0)["status"] == user2.status
        assert friends1.get(0)["imagePath"] == Utils.getDefaultImagePath()

        and: "the second user relationship has not changed"
        assert user2.getIncomingFriendRequests() == new JSONArray()
        assert user2.getOutgoingFriendRequests() == new JSONArray()
        def friends2 = user2.getFriends()
        assert friends2.size() == 1
        assert friends2.get(0)["id"] == user1.id
        assert friends2.get(0)["username"] == user1.username
        assert friends2.get(0)["name"] == user1.name
        assert friends2.get(0)["status"] == user1.status
        assert friends2.get(0)["imagePath"] == Utils.getDefaultImagePath()
    }

    def "send friend request without userId"() {
        given: "user"
        def user = new TestUser().register()

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $user.token"],
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
        def user1 = new TestUser().register()
        def user2 = new TestUser().register().delete()

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $user1.token"],
                body: ["userId": user2.id],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400
    }

    def "send friend request to deleted friend"() {
        given: "user and friend with deleted profile"
        def user1 = new TestUser().register()
        def user2 = new TestUser().register()
        user1.sendFriendRequest(user2.id)
        user2.sendFriendRequest(user1.id).delete()

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $user1.token"],
                body: ["userId": user2.id],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400
    }

    def "send friend request to nonexistent user"() {
        given: "user and nonexistent user id"
        def user = new TestUser().register()
        def nonexistentId = UUID.randomUUID().toString()

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $user.token"],
                body: ["userId": nonexistentId],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400
    }

    def "send friend request to current user"() {
        given: "user"
        def user = new TestUser().register()

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $user.token"],
                body: ["userId": user.id],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400
    }

    def "send friend request with invalid token"() {
        given: "invalid token"
        def invalidToken = UUID.randomUUID().toString()

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $invalidToken"],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 401
    }
}
