package com.drsanches.photobooth.end2end.friends

import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import net.sf.json.JSONArray
import net.sf.json.JSONNull
import com.drsanches.photobooth.end2end.utils.RequestUtils
import com.drsanches.photobooth.end2end.utils.TestUser
import com.drsanches.photobooth.end2end.utils.Utils
import org.apache.commons.lang3.StringUtils
import spock.lang.Specification

class TestGetFriends extends Specification {

    String PATH = "/api/v1/friends"

    def "success friends getting"() {
        given: "two friends"
        def user1 = new TestUser().register()
        def user2 = new TestUser().register().fillProfile()
        user1.sendFriendRequest(user2.id)
        user2.sendFriendRequest(user1.id)

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH,
                headers: [Authorization: "Bearer $user1.token"]) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        def body = response.data as JSONArray
        assert body.size() == 1
        assert body.get(0)["id"] == user2.id
        assert body.get(0)["username"] == user2.username
        assert body.get(0)["name"] == user2.name
        assert body.get(0)["status"] == user2.status
        assert body.get(0)["imagePath"] == Utils.DEFAULT_IMAGE_PATH
        assert body.get(0)["thumbnailPath"] == Utils.DEFAULT_THUMBNAIL_PATH
        assert body.get(0)["relationship"] == "FRIEND"
    }

    def "success empty friends getting"() {
        given: "three users"
        def user1 = new TestUser().register()
        def user2 = new TestUser().register()
        def user3 = new TestUser().register()
        user1.sendFriendRequest(user2.id)
        user3.sendFriendRequest(user1.id)

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH,
                headers: [Authorization: "Bearer $user1.token"]) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        assert response.data == new JSONArray()
    }

    def "success friend with deleted profile getting"() {
        given: "user and friend with deleted profile"
        def user1 = new TestUser().register()
        def user2 = new TestUser().register()
        user1.sendFriendRequest(user2.id)
        user2.sendFriendRequest(user1.id)
        user2.delete()

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH,
                headers: [Authorization: "Bearer $user1.token"]) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        def body = response.data as JSONArray
        assert body.size() == 1
        assert body.get(0)["id"] == user2.id
        assert body.get(0)["username"] == JSONNull.getInstance()
        assert body.get(0)["name"] == JSONNull.getInstance()
        assert body.get(0)["status"] == JSONNull.getInstance()
        assert body.get(0)["imagePath"] == Utils.DELETED_IMAGE_PATH
        assert body.get(0)["thumbnailPath"] == Utils.DELETED_THUMBNAIL_PATH
        assert body.get(0)["relationship"] == "FRIEND"
    }

    def "get friends with invalid token"() {
        given: "invalid token"
        def token = UUID.randomUUID().toString()

        when: "request is sent"
        RequestUtils.getRestClient().get(
                path: PATH,
                headers: [Authorization: "Bearer $token"])

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert StringUtils.isNotEmpty(e.response.data["uuid"] as CharSequence)
        assert e.response.data["message"] == "Wrong token"
        assert e.response.status == 401
    }
}
