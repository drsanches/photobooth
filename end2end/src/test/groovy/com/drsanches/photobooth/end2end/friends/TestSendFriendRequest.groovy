package com.drsanches.photobooth.end2end.friends

import com.drsanches.photobooth.end2end.utils.RequestUtils
import com.drsanches.photobooth.end2end.utils.TestUser
import com.drsanches.photobooth.end2end.utils.Utils
import org.json.JSONObject
import spock.lang.Specification

class TestSendFriendRequest extends Specification {

    String PATH = "/api/v1/app/friends/manage/add"

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
                headers: [Authorization: "Bearer $user1.token"],
                body: [userId: user2.id])

        then: "response is correct"
        assert response.status == 201

        and: "the first user has correct relationships"
        assert user1.getIncomingFriendRequests().size() == 0
        assert user1.getFriends().size() == 0
        def outgoingRequests = user1.getOutgoingFriendRequests()
        assert outgoingRequests.size() == 1
        assert outgoingRequests[0]["id"] == user2.id

        and: "the second user has correct relationships"
        assert user2.getOutgoingFriendRequests().size() == 0
        assert user2.getFriends().size() == 0
        def incomingRequests = user2.getIncomingFriendRequests()
        assert incomingRequests.size() == 1
        assert incomingRequests[0]["id"] == user1.id
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
                headers: [Authorization: "Bearer $user1.token"],
                body: [userId: user2.id])

        then: "response is correct"
        assert response.status == 201

        and: "the first user has correct relationships"
        assert user1.getIncomingFriendRequests().size() == 0
        assert user1.getOutgoingFriendRequests().size() == 0
        def friends1 = user1.getFriends()
        assert friends1.size() == 1
        assert friends1[0]["id"] == user2.id

        and: "the second user has correct relationships"
        assert user2.getIncomingFriendRequests().size() == 0
        assert user2.getOutgoingFriendRequests().size() == 0
        def friends2 = user2.getFriends()
        assert friends2.size() == 1
        assert friends2[0]["id"] == user1.id
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
                headers: [Authorization: "Bearer $user1.token"],
                body: [userId: user2.id])

        then: "response is correct"
        assert response.status == 201

        and: "the first user relationship has not changed"
        assert user1.getIncomingFriendRequests().size() == 0
        assert user1.getFriends().size() == 0
        def outgoingRequests = user1.getOutgoingFriendRequests()
        assert outgoingRequests.size() == 1
        assert outgoingRequests[0]["id"] == user2.id

        and: "the second user relationship has not changed"
        assert user2.getOutgoingFriendRequests().size() == 0
        assert user2.getFriends().size() == 0
        def incomingRequests = user2.getIncomingFriendRequests()
        assert incomingRequests.size() == 1
        assert incomingRequests[0]["id"] == user1.id
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
                headers: [Authorization: "Bearer $user1.token"],
                body: [userId: user2.id])

        then: "response is correct"
        assert response.status == 201

        and: "the first user relationship has not changed"
        assert user1.getIncomingFriendRequests().size() == 0
        assert user1.getOutgoingFriendRequests().size() == 0
        def friends1 = user1.getFriends()
        assert friends1.size() == 1
        assert friends1[0]["id"] == user2.id

        and: "the second user relationship has not changed"
        assert user2.getIncomingFriendRequests().size() == 0
        assert user2.getOutgoingFriendRequests().size() == 0
        def friends2 = user2.getFriends()
        assert friends2.size() == 1
        assert friends2[0]["id"] == user1.id
    }

    def "send friend request to deleted user"() {
        given: "user and deleted user"
        def user1 = new TestUser().register()
        def user2 = new TestUser().register().delete()

        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: [Authorization: "Bearer $user1.token"],
                body: [userId: user2.id])

        then: "response is correct"
        assert response.status == 400
        assert Utils.validateErrorResponse(response.data as JSONObject, "validation.error", [
                Map.of("field", "userId", "message", "the user does not exist")
        ])
    }

    def "send friend request to deleted friend"() {
        given: "user and friend with deleted profile"
        def user1 = new TestUser().register()
        def user2 = new TestUser().register()
        user1.sendFriendRequest(user2.id)
        user2.sendFriendRequest(user1.id).delete()

        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: [Authorization: "Bearer $user1.token"],
                body: [userId: user2.id])

        then: "response is correct"
        assert response.status == 400
        assert Utils.validateErrorResponse(response.data as JSONObject, "validation.error", [
                Map.of("field", "userId", "message", "the user does not exist")
        ])
    }

    def "send friend request to current user"() {
        given: "user"
        def user = new TestUser().register()

        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: [Authorization: "Bearer $user.token"],
                body: [userId: user.id])

        then: "response is correct"
        assert response.status == 400
        assert Utils.validateErrorResponse(response.data as JSONObject, "validation.error", [
                Map.of("field", "userId", "message", "the user can not be current")
        ])
    }

    def "send friend request with invalid data"() {
        given: "user"
        def user = new TestUser().register()

        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: [Authorization: "Bearer $user.token"],
                body: [userId: userId])

        then: "response is correct"
        assert response.status == 400
        assert Utils.validateErrorResponse(response.data as JSONObject, "validation.error", [
                Map.of("field", "userId", "message", message)
        ])

        where:
        userId << [
                null,
                "",
                UUID.randomUUID().toString()
        ]
        message << [
                "must not be empty",
                "must not be empty",
                "the user does not exist"
        ]
    }

    def "send friend request with invalid token"() {
        given: "invalid token"
        def invalidToken = UUID.randomUUID().toString()

        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: [Authorization: "Bearer $invalidToken"])

        then: "response is correct"
        assert response.status == 401
        assert Utils.validateErrorResponse(response.data as JSONObject, "Wrong token", null)
    }
}
