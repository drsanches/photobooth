package ru.drsanches.photobooth.friends

import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import net.sf.json.JSONArray
import net.sf.json.JSONNull
import ru.drsanches.photobooth.utils.DataGenerator
import ru.drsanches.photobooth.utils.RequestUtils
import spock.lang.Specification

class TestGetFriends extends Specification {

    String PATH = "/api/v1/friends"

    def "success friends getting"() {
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
        def token2 = RequestUtils.getToken(username2, password2)

        def firstName2 = DataGenerator.createValidFirstName()
        def lastName2 = DataGenerator.createValidLastName()
        RequestUtils.changeUserProfile(token2, firstName2, lastName2)

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH,
                headers: ["Authorization": "Bearer $token1"]) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        def body = response.getData() as JSONArray
        assert body.size() == 1
        assert body.get(0)["id"] == userId2
        assert body.get(0)["username"] == username2
        assert body.get(0)["firstName"] == firstName2
        assert body.get(0)["lastName"] == lastName2
    }

    def "success empty friends getting"() {
        given: "three users"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        def username2 = DataGenerator.createValidUsername()
        def password2 = DataGenerator.createValidPassword()
        def username3 = DataGenerator.createValidUsername()
        def password3 = DataGenerator.createValidPassword()

        def userId1 = RequestUtils.registerUser(username1, password1, null)
        def userId2 = RequestUtils.registerUser(username2, password2, null)
        RequestUtils.registerUser(username3, password3, null)

        RequestUtils.sendFriendRequest(username1, password1, userId2)
        RequestUtils.sendFriendRequest(username3, password3, userId1)

        def token1 = RequestUtils.getToken(username1, password1)

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH,
                headers: ["Authorization": "Bearer $token1"]) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        assert response.getData() == new JSONArray()
    }

    def "success deleted friend getting"() {
        given: "user with deleted friend"
        def username1 = DataGenerator.createValidUsername()
        def password1 = DataGenerator.createValidPassword()
        def username2 = DataGenerator.createValidUsername()
        def password2 = DataGenerator.createValidPassword()

        def userId1 = RequestUtils.registerUser(username1, password1, null)
        def userId2 = RequestUtils.registerUser(username2, password2, null)

        RequestUtils.sendFriendRequest(username1, password1, userId2)
        RequestUtils.sendFriendRequest(username2, password2, userId1)

        def token1 = RequestUtils.getToken(username1, password1)

        RequestUtils.deleteUser(username2, password2)

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH,
                headers: ["Authorization": "Bearer $token1"]) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        def body = response.getData() as JSONArray
        assert body.size() == 1
        assert body.get(0)["id"] == userId2
        assert body.get(0)["username"] == JSONNull.getInstance()
        assert body.get(0)["firstName"] == JSONNull.getInstance()
        assert body.get(0)["lastName"] == JSONNull.getInstance()
    }

    def "get friends with invalid token"() {
        given: "invalid token"
        def token = UUID.randomUUID().toString()

        when: "request is sent"
        RequestUtils.getRestClient().get(
                path: PATH,
                headers: ["Authorization": "Bearer $token"])

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 401
    }
}