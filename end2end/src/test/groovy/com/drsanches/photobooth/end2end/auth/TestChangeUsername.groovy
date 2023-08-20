package com.drsanches.photobooth.end2end.auth

import org.apache.commons.lang3.RandomStringUtils
import com.drsanches.photobooth.end2end.utils.DataGenerator
import com.drsanches.photobooth.end2end.utils.RequestUtils
import com.drsanches.photobooth.end2end.utils.TestUser
import org.apache.commons.lang3.StringUtils
import spock.lang.Specification

class TestChangeUsername extends Specification {

    String PATH = "/api/v1/auth/changeUsername"

    def "success username change"() {
        given: "user, two tokens and new username"
        def user = new TestUser().register()
        def oldToken = RequestUtils.getToken(user.username, user.password)
        def token = RequestUtils.getToken(user.username, user.password)
        def newUsername = DataGenerator.createValidUsername()

        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: [Authorization: "Bearer $token"],
                body: [newUsername: newUsername])

        then: "response is correct"
        assert response.status == 200

        and: "previous token is invalid"
        assert RequestUtils.getAuthInfo(token) == null

        and: "old token is invalid"
        assert RequestUtils.getAuthInfo(oldToken) == null

        and: "user auth was updated"
        assert RequestUtils.getAuthInfo(newUsername, user.password)["username"] == newUsername

        and: "user profile was updated"
        assert RequestUtils.getUserProfile(newUsername, user.password)["username"] == newUsername

        and: "new token is different"
        assert RequestUtils.getToken(newUsername, user.password) != token
    }

    def "username change with old username"() {
        given: "user"
        def user = new TestUser().register()

        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: [Authorization: "Bearer $user.token"],
                body: [newUsername: user.username])

        then: "response is correct"
        assert response.status == 400
        assert StringUtils.isNotEmpty(response.data["uuid"] as CharSequence)
        assert response.data["message"] == "changeUsername.changeUsernameDto.newUsername: User with username '$user.username' already exists"
    }

    def "username change with existent username"() {
        given: "user"
        def user1 = new TestUser().register()
        def user2 = new TestUser().register()

        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: [Authorization: "Bearer $user1.token"],
                body: [newUsername: user2.username])

        then: "response is correct"
        assert response.status == 400
        assert StringUtils.isNotEmpty(response.data["uuid"] as CharSequence)
        assert response.data["message"] == "changeUsername.changeUsernameDto.newUsername: User with username '$user2.username' already exists"
    }

    def "username change with invalid username"() {
        given: "user"
        def user = new TestUser().register()

        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: [Authorization: "Bearer $user.token"],
                body: [newUsername: invalidUsername])

        then: "response is correct"
        assert response.status == 400
        assert StringUtils.isNotEmpty(response.data["uuid"] as CharSequence)
        assert response.data["message"] == message

        where:
        invalidUsername << [
                null,
                "",
                DataGenerator.createValidUsername() + ".",
                RandomStringUtils.randomAlphabetic(21)
        ]
        message << [
                "changeUsername.changeUsernameDto.newUsername: must not be empty",
                "changeUsername.changeUsernameDto.newUsername: must not be empty",
                "changeUsername.changeUsernameDto.newUsername: wrong username format",
                "changeUsername.changeUsernameDto.newUsername: length must be between 0 and 20"
        ]
    }

    def "username change with invalid token"() {
        given: "invalid token"
        def token = UUID.randomUUID().toString()

        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: [Authorization: "Bearer $token"])

        then: "response is correct"
        assert response.status == 401
        assert StringUtils.isNotEmpty(response.data["uuid"] as CharSequence)
        assert response.data["message"] == "Wrong token"
    }
}
