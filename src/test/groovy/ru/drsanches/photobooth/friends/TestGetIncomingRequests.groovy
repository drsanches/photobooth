package ru.drsanches.photobooth.friends

import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import net.sf.json.JSONArray
import net.sf.json.JSONNull
import ru.drsanches.photobooth.utils.RequestUtils
import ru.drsanches.photobooth.utils.TestUser
import ru.drsanches.photobooth.utils.Utils
import spock.lang.Specification

class TestGetIncomingRequests extends Specification {

    String PATH = "/api/v1/friends/requests/incoming"

    def "success incoming requests getting"() {
        given: "two users"
        def user1 = new TestUser().register()
        def user2 = new TestUser().register().fillProfile()
        user2.sendFriendRequest(user1.id)

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH,
                headers: ["Authorization": "Bearer $user1.token"]) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        def body = response.getData() as JSONArray
        assert body.size() == 1
        assert body.get(0)["id"] == user2.id
        assert body.get(0)["username"] == user2.username
        assert body.get(0)["name"] == user2.name
        assert body.get(0)["status"] == user2.status
        assert body.get(0)["imagePath"] == Utils.getDefaultImagePath()
    }

    def "success empty incoming requests getting"() {
        given: "three users"
        def user1 = new TestUser().register()
        def user2 = new TestUser().register()
        def user3 = new TestUser().register()
        user1.sendFriendRequest(user2.id)
        user2.sendFriendRequest(user1.id)
        user1.sendFriendRequest(user3.id)

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH,
                headers: ["Authorization": "Bearer $user1.token"]) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        assert response.getData() == new JSONArray()
    }

    def "success friend with deleted profile incoming requests getting"() {
        given: "user and friend with deleted profile"
        def user1 = new TestUser().register()
        def user2 = new TestUser().register().sendFriendRequest(user1.id).delete()

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH,
                headers: ["Authorization": "Bearer $user1.token"]) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        def body = response.getData() as JSONArray
        assert body.size() == 1
        assert body.get(0)["id"] == user2.id
        assert body.get(0)["username"] == JSONNull.getInstance()
        assert body.get(0)["name"] == JSONNull.getInstance()
        assert body.get(0)["status"] == JSONNull.getInstance()
        assert body.get(0)["imagePath"] == Utils.getDeletedImagePath()
    }

    def "get incoming requests with invalid token"() {
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
