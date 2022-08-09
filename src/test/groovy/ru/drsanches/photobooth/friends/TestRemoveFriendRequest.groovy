package ru.drsanches.photobooth.friends

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import net.sf.json.JSONArray
import ru.drsanches.photobooth.utils.RequestUtils
import ru.drsanches.photobooth.utils.TestUser
import spock.lang.Specification

class TestRemoveFriendRequest extends Specification {

    String PATH = "/api/v1/friends/manage/delete"

    /**
     * user1 --req-> user2
     * user1 ---X--> user2
     */
    def "success outgoing friend request deletion"() {
        given: "two users"
        def user1 = new TestUser().register()
        def user2 = new TestUser().register()
        user1.sendFriendRequest(user2.id)

        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $user1.token"],
                body: ["userId": user2.id],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200

        and: "the first user has correct relationships"
        assert user1.getIncomingFriendRequests() == new JSONArray()
        assert user1.getOutgoingFriendRequests() == new JSONArray()
        assert user1.getFriends() == new JSONArray()

        and: "the second user has correct relationships"
        assert user2.getIncomingFriendRequests() == new JSONArray()
        assert user2.getOutgoingFriendRequests() == new JSONArray()
        assert user2.getFriends() == new JSONArray()
    }

    /**
     * user1 <-req-- user2
     * user1 ---X--> user2
     */
    def "success incoming friend request deletion"() {
        given: "two users and one side friend request"
        def user1 = new TestUser().register()
        def user2 = new TestUser().register()
        user2.sendFriendRequest(user1.id)

        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $user1.token"],
                body: ["userId": user2.id],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200

        and: "the first user has correct relationships"
        assert user1.getIncomingFriendRequests() == new JSONArray()
        assert user1.getOutgoingFriendRequests() == new JSONArray()
        assert user1.getFriends() == new JSONArray()

        and: "the second user has correct relationships"
        assert user2.getIncomingFriendRequests() == new JSONArray()
        assert user2.getOutgoingFriendRequests() == new JSONArray()
        assert user2.getFriends() == new JSONArray()
    }

    /**
     * friend1 ---X--> friend2
     */
    def "success friend deletion"() {
        given: "two friends"
        def user1 = new TestUser().register()
        def user2 = new TestUser().register()
        user1.sendFriendRequest(user2.id)
        user2.sendFriendRequest(user1.id)

        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $user1.token"],
                body: ["userId": user2.id],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200

        and: "the first user has correct relationships"
        assert user1.getIncomingFriendRequests() == new JSONArray()
        assert user1.getOutgoingFriendRequests() == new JSONArray()
        assert user1.getFriends() == new JSONArray()

        and: "the second user has correct relationships"
        assert user2.getIncomingFriendRequests() == new JSONArray()
        assert user2.getOutgoingFriendRequests() == new JSONArray()
        assert user2.getFriends() == new JSONArray()
    }

    /**
     * user <-req-- deleted
     * user ---X--> deleted
     */
    def "success incoming friend request deletion from deleted user"() {
        given: "user and user with deleted profile"
        def user1 = new TestUser().register()
        def user2 = new TestUser().register().sendFriendRequest(user1.id).delete()

        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $user1.token"],
                body: ["userId": user2.id],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200

        and: "the user has correct relationships"
        assert user1.getIncomingFriendRequests() == new JSONArray()
        assert user1.getOutgoingFriendRequests() == new JSONArray()
        assert user1.getFriends() == new JSONArray()
    }

    /**
     * friend ---X--> deleted friend
     */
    def "success friendship deletion for deleted user"() {
        given: "user and friend with deleted profile"
        def user1 = new TestUser().register()
        def user2 = new TestUser().register()
        user1.sendFriendRequest(user2.id)
        user2.sendFriendRequest(user1.id).delete()

        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $user1.token"],
                body: ["userId": user2.id],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200

        and: "the user has correct relationships"
        assert user1.getIncomingFriendRequests() == new JSONArray()
        assert user1.getOutgoingFriendRequests() == new JSONArray()
        assert user1.getFriends() == new JSONArray()
    }

    /**
     * user1 ---X--> user2
     */
    def "success delete nonexistent request"() {
        given: "two users"
        def user1 = new TestUser().register()
        def user2 = new TestUser().register()

        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $user1.token"],
                body: ["userId": user2.id],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200

        and: "the first user has correct relationships"
        assert user1.getIncomingFriendRequests() == new JSONArray()
        assert user1.getOutgoingFriendRequests() == new JSONArray()
        assert user1.getFriends() == new JSONArray()

        and: "the second user has correct relationships"
        assert user2.getIncomingFriendRequests() == new JSONArray()
        assert user2.getOutgoingFriendRequests() == new JSONArray()
        assert user2.getFriends() == new JSONArray()
    }

    /**
     * user ---X--> user
     */
    def "delete friend request for yourself"() {
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

        and: "the first user has correct relationships"
        assert user.getIncomingFriendRequests() == new JSONArray()
        assert user.getOutgoingFriendRequests() == new JSONArray()
        assert user.getFriends() == new JSONArray()
    }

    def "friend request deletion without userId"() {
        given: "two users"
        def user1 = new TestUser().register()
        def user2 = new TestUser().register()
        user1.sendFriendRequest(user2.id)

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $user1.token"],
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

    def "delete request with invalid token"() {
        given: "invalid token"
        def invalidToken = UUID.randomUUID().toString()

        when: "sendRequest is called with invalid token"
        RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $invalidToken"],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 401
    }
}
