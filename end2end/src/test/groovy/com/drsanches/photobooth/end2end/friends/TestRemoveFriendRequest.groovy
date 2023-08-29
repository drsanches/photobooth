package com.drsanches.photobooth.end2end.friends

import com.drsanches.photobooth.end2end.utils.RequestUtils
import com.drsanches.photobooth.end2end.utils.TestUser
import org.apache.commons.lang3.StringUtils
import org.json.JSONArray
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
                headers: [Authorization: "Bearer $user1.token"],
                body: [userId: user2.id])

        then: "response is correct"
        assert response.status == 200

        and: "the first user has correct relationships"
        assert user1.getIncomingFriendRequests().size() == 0
        assert user1.getOutgoingFriendRequests().size() == 0
        assert user1.getFriends().size() == 0

        and: "the second user has correct relationships"
        assert user2.getIncomingFriendRequests().size() == 0
        assert user2.getOutgoingFriendRequests().size() == 0
        assert user2.getFriends().size() == 0
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
                headers: [Authorization: "Bearer $user1.token"],
                body: [userId: user2.id])

        then: "response is correct"
        assert response.status == 200

        and: "the first user has correct relationships"
        assert user1.getIncomingFriendRequests().size() == 0
        assert user1.getOutgoingFriendRequests().size() == 0
        assert user1.getFriends().size() == 0

        and: "the second user has correct relationships"
        assert user2.getIncomingFriendRequests().size() == 0
        assert user2.getOutgoingFriendRequests().size() == 0
        assert user2.getFriends().size() == 0
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
                headers: [Authorization: "Bearer $user1.token"],
                body: [userId: user2.id])

        then: "response is correct"
        assert response.status == 200

        and: "the first user has correct relationships"
        assert user1.getIncomingFriendRequests().size() == 0
        assert user1.getOutgoingFriendRequests().size() == 0
        assert user1.getFriends().size() == 0

        and: "the second user has correct relationships"
        assert user2.getIncomingFriendRequests().size() == 0
        assert user2.getOutgoingFriendRequests().size() == 0
        assert user2.getFriends().size() == 0
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
                headers: [Authorization: "Bearer $user1.token"],
                body: [userId: user2.id])

        then: "response is correct"
        assert response.status == 200

        and: "the user has correct relationships"
        assert user1.getIncomingFriendRequests().size() == 0
        assert user1.getOutgoingFriendRequests().size() == 0
        assert user1.getFriends().size() == 0
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
                headers: [Authorization: "Bearer $user1.token"],
                body: [userId: user2.id])

        then: "response is correct"
        assert response.status == 200

        and: "the user has correct relationships"
        assert user1.getIncomingFriendRequests().size() == 0
        assert user1.getOutgoingFriendRequests().size() == 0
        assert user1.getFriends().size() == 0
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
                headers: [Authorization: "Bearer $user1.token"],
                body: [userId: user2.id])

        then: "response is correct"
        assert response.status == 200

        and: "the first user has correct relationships"
        assert user1.getIncomingFriendRequests().size() == 0
        assert user1.getOutgoingFriendRequests().size() == 0
        assert user1.getFriends().size() == 0

        and: "the second user has correct relationships"
        assert user2.getIncomingFriendRequests().size() == 0
        assert user2.getOutgoingFriendRequests().size() == 0
        assert user2.getFriends().size() == 0
    }

    /**
     * user ---X--> user
     */
    def "delete friend request for yourself"() {
        given: "user"
        def user = new TestUser().register()

        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: [Authorization: "Bearer $user.token"],
                body: [userId: user.id])

        then: "response is correct"
        assert StringUtils.isNotEmpty(response.data["uuid"] as CharSequence)
        assert response.data["message"] == "removeRequest.removeRequestDto.userId: the user can not be current"
        assert response.status == 400

        and: "the first user has correct relationships"
        assert user.getIncomingFriendRequests().size() == 0
        assert user.getOutgoingFriendRequests().size() == 0
        assert user.getFriends().size() == 0
    }

    def "friend request deletion with invalid data"() {
        given: "user"
        def user = new TestUser().register()

        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: [Authorization: "Bearer $user.token"],
                body: [userId: userId])

        then: "response is correct"
        assert response.status == 400
        assert StringUtils.isNotEmpty(response.data["uuid"] as CharSequence)
        assert response.data["message"] == message

        where:
        userId << [
                null,
                "",
                UUID.randomUUID().toString()
        ]
        message << [
                "removeRequest.removeRequestDto.userId: must not be empty",
                "removeRequest.removeRequestDto.userId: must not be empty",
                "removeRequest.removeRequestDto.userId: the user does not exist"
        ]
    }

    def "delete request with invalid token"() {
        given: "invalid token"
        def invalidToken = UUID.randomUUID().toString()

        when: "sendRequest is called with invalid token"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: [Authorization: "Bearer $invalidToken"])

        then: "response is correct"
        assert StringUtils.isNotEmpty(response.data["uuid"] as CharSequence)
        assert response.data["message"] == "Wrong token"
        assert response.status == 401
    }
}
