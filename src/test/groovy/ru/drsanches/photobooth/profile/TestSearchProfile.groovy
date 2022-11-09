package ru.drsanches.photobooth.profile

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import ru.drsanches.photobooth.utils.DataGenerator
import ru.drsanches.photobooth.utils.RequestUtils
import ru.drsanches.photobooth.utils.TestUser
import ru.drsanches.photobooth.utils.Utils
import spock.lang.Specification

class TestSearchProfile extends Specification {

    String PATH = "/api/v1/profile/search/"

    def "successful user profile searching"() {
        given: "two users"
        def user1 = new TestUser().register()
        def user2 = new TestUser().register().fillProfile()

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH + user2.username,
                headers: ["Authorization": "Bearer $user1.token"],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        assert response.getData()["id"] == user2.id
        assert response.getData()["username"] == user2.username
        assert response.getData()["name"] == user2.name
        assert response.getData()["status"] == user2.status
        assert response.getData()["imagePath"] == Utils.getDefaultImagePath()
        assert response.getData()["thumbnailPath"] == Utils.getDefaultThumbnailPath()
    }

    def "successful user profile searching with upper case"() {
        given: "two users"
        def user1 = new TestUser().register()
        def user2 = new TestUser().register().fillProfile()

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH + user2.username.toUpperCase(),
                headers: ["Authorization": "Bearer $user1.token"],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        assert response.getData()["id"] == user2.id
        assert response.getData()["username"] == user2.username
        assert response.getData()["name"] == user2.name
        assert response.getData()["status"] == user2.status
        assert response.getData()["imagePath"] == Utils.getDefaultImagePath()
        assert response.getData()["thumbnailPath"] == Utils.getDefaultThumbnailPath()
    }

    def "search deleted user profile"() {
        given: "user and deleted user"
        def user1 = new TestUser().register()
        def user2 = new TestUser().register().delete()

        when: "request is sent"
        RequestUtils.getRestClient().get(
                path: PATH + user2.username,
                headers: ["Authorization": "Bearer $user1.token"],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 404
    }

    def "search nonexistent user profile"() {
        given: "user and nonexistent id"
        def user = new TestUser().register()
        def nonexistentUsername = DataGenerator.createValidUsername()

        when: "request is sent"
        RequestUtils.getRestClient().get(
                path: PATH + nonexistentUsername,
                headers: ["Authorization": "Bearer $user.token"],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 404
    }

    def "get user profile with invalid token"() {
        given: "username and invalid token"
        def username = DataGenerator.createValidUsername()
        def token = UUID.randomUUID().toString()

        when: "request is sent"
        RequestUtils.getRestClient().get(
                path: PATH + username,
                headers: ["Authorization": "Bearer $token"],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 401
    }
}
