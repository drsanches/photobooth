package ru.drsanches.photobooth.profile

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import ru.drsanches.photobooth.app.data.profile.dto.response.Relationship
import ru.drsanches.photobooth.utils.RequestUtils
import ru.drsanches.photobooth.utils.TestUser
import ru.drsanches.photobooth.utils.Utils
import spock.lang.Specification

class TestGetProfile extends Specification {

    String PATH = "/api/v1/profile/"

    def "successful user profile getting"() {
        given: "two users"
        def user1 = new TestUser().register()
        def user2 = new TestUser().register().fillProfile()

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH + user2.id,
                headers: ["Authorization": "Bearer $user1.token"],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        assert response.getData()["id"] == user2.id
        assert response.getData()["username"] == user2.username
        assert response.getData()["name"] == user2.name
        assert response.getData()["status"] == user2.status
        assert response.getData()["imagePath"] == Utils.DEFAULT_IMAGE_PATH
        assert response.getData()["thumbnailPath"] == Utils.DEFAULT_THUMBNAIL_PATH
        assert response.getData()["relationship"] == Relationship.STRANGER.name()
    }

    def "successful incoming user profile getting"() {
        given: "two users"
        def user1 = new TestUser().register()
        def user2 = new TestUser().register().fillProfile().sendFriendRequest(user1.id)

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH + user2.id,
                headers: ["Authorization": "Bearer $user1.token"],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        assert response.getData()["id"] == user2.id
        assert response.getData()["username"] == user2.username
        assert response.getData()["name"] == user2.name
        assert response.getData()["status"] == user2.status
        assert response.getData()["imagePath"] == Utils.DEFAULT_IMAGE_PATH
        assert response.getData()["thumbnailPath"] == Utils.DEFAULT_THUMBNAIL_PATH
        assert response.getData()["relationship"] == Relationship.INCOMING_FRIEND_REQUEST.name()
    }

    def "successful outgoing user profile getting"() {
        given: "two users"
        def user1 = new TestUser().register()
        def user2 = new TestUser().register().fillProfile()
        user1.sendFriendRequest(user2.id)

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH + user2.id,
                headers: ["Authorization": "Bearer $user1.token"],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        assert response.getData()["id"] == user2.id
        assert response.getData()["username"] == user2.username
        assert response.getData()["name"] == user2.name
        assert response.getData()["status"] == user2.status
        assert response.getData()["imagePath"] == Utils.DEFAULT_IMAGE_PATH
        assert response.getData()["thumbnailPath"] == Utils.DEFAULT_THUMBNAIL_PATH
        assert response.getData()["relationship"] == Relationship.OUTGOING_FRIEND_REQUEST.name()
    }

    def "successful friend profile getting"() {
        given: "two users"
        def user1 = new TestUser().register()
        def user2 = new TestUser().register().fillProfile().sendFriendRequest(user1.id)
        user1.sendFriendRequest(user2.id)

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH + user2.id,
                headers: ["Authorization": "Bearer $user1.token"],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        assert response.getData()["id"] == user2.id
        assert response.getData()["username"] == user2.username
        assert response.getData()["name"] == user2.name
        assert response.getData()["status"] == user2.status
        assert response.getData()["imagePath"] == Utils.DEFAULT_IMAGE_PATH
        assert response.getData()["thumbnailPath"] == Utils.DEFAULT_THUMBNAIL_PATH
        assert response.getData()["relationship"] == Relationship.FRIEND.name()
    }

    def "successful current profile getting"() {
        given: "two users"
        def user1 = new TestUser().register().fillProfile()

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH + user1.id,
                headers: ["Authorization": "Bearer $user1.token"],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        assert response.getData()["id"] == user1.id
        assert response.getData()["username"] == user1.username
        assert response.getData()["name"] == user1.name
        assert response.getData()["status"] == user1.status
        assert response.getData()["imagePath"] == Utils.DEFAULT_IMAGE_PATH
        assert response.getData()["thumbnailPath"] == Utils.DEFAULT_THUMBNAIL_PATH
        assert response.getData()["relationship"] == Relationship.CURRENT.name()
    }

    def "get deleted user profile"() {
        given: "user and deleted user"
        def user1 = new TestUser().register()
        def user2 = new TestUser().register().delete()

        when: "request is sent"
        RequestUtils.getRestClient().get(
                path: PATH + user2.id,
                headers: ["Authorization": "Bearer $user1.token"],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 404
    }

    def "get nonexistent user profile"() {
        given: "user and nonexistent user id"
        def user = new TestUser().register()
        def nonexistentId = UUID.randomUUID().toString()

        when: "request is sent"
        RequestUtils.getRestClient().get(
                path: PATH + nonexistentId,
                headers: ["Authorization": "Bearer $user.token"],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 404
    }

    def "get user profile with invalid token"() {
        given: "user id and invalid token"
        def userId = UUID.randomUUID().toString()
        def token = UUID.randomUUID().toString()

        when: "request is sent"
        RequestUtils.getRestClient().get(
                path: PATH + userId,
                headers: ["Authorization": "Bearer $token"],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 401
    }
}
