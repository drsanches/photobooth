package com.drsanches.photobooth.end2end

import com.drsanches.photobooth.end2end.utils.RequestUtils
import com.drsanches.photobooth.end2end.utils.TestUser
import com.drsanches.photobooth.end2end.utils.Utils

class TestScripts
//        extends Specification //Uncomment before using
{
    String username = "admin"
    String password = "admin"
    String googleAccessToken = "googleAccessToken"

    def "register a user"() {
        when:
        RequestUtils.getRestClient().post(
                path: "/api/v1/auth/registration",
                body: [
                        username: "user",
                        password: Utils.sha256("user"),
                        email: "user@email.com"
                ])

        then: true
    }

    def "register a user by google"() {
        when:
        RequestUtils.getRestClient().post(
                path: "/api/v1/auth/google/registration",
                body: [accessToken: googleAccessToken])

        then: true
    }

    def "login a user by google"() {
        when:
        RequestUtils.getRestClient().post(
                path: "/api/v1/auth/google/login",
                body: [accessToken: googleAccessToken])

        then: true
    }

    def "send incoming friend request to the user"() {
        given:
        TestUser user = new TestUser(username, Utils.sha256(password))

        when:
        new TestUser().register().sendFriendRequest(user.id)

        then: true
    }

    def "send outgoing friend request from the user"() {
        given:
        TestUser user = new TestUser(username, Utils.sha256(password))

        when:
        TestUser outgoing = new TestUser().register()
        user.sendFriendRequest(outgoing.id)

        then: true
    }

    def "add a friend to the user"() {
        given:
        TestUser user = new TestUser(username, Utils.sha256(password))

        when:
        TestUser friend = new TestUser().register().sendFriendRequest(user.id)
        user.sendFriendRequest(friend.id)

        then: true
    }
}
