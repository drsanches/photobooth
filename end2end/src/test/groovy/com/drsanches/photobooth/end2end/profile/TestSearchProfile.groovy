package com.drsanches.photobooth.end2end.profile

import com.drsanches.photobooth.end2end.utils.DataGenerator
import com.drsanches.photobooth.end2end.utils.RequestUtils
import com.drsanches.photobooth.end2end.utils.TestUser
import com.drsanches.photobooth.end2end.utils.Utils
import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import net.sf.json.JSONArray
import spock.lang.Specification

class TestSearchProfile extends Specification {

    String PATH = "/api/v1/profile/search"

    def "successful user profile searching"() {
        given: "two users"
        def user1 = new TestUser().register()
        def user2 = new TestUser().register().fillProfile()
        def search = user2.getUsername().substring(1, user2.username.length() - 1)

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH,
                params: [username: search,
                         page: 0,
                         size: 1],
                headers: [Authorization: "Bearer $user1.token"],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        def results = response.getData() as JSONArray
        assert results.size() == 1
        assert results.get(0)["id"] == user2.id
        assert results.get(0)["username"] == user2.username
        assert results.get(0)["name"] == user2.name
        assert results.get(0)["status"] == user2.status
        assert results.get(0)["imagePath"] == Utils.DEFAULT_IMAGE_PATH
        assert results.get(0)["thumbnailPath"] == Utils.DEFAULT_THUMBNAIL_PATH
        assert results.get(0)["relationship"] == "STRANGER"
    }

    def "successful user profile searching with upper case"() {
        given: "two users"
        def user1 = new TestUser().register()
        def user2 = new TestUser().register().fillProfile()
        def search = user2.getUsername().substring(1, user2.username.length() - 1).toUpperCase()

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH,
                params: [username: search,
                         page: 0,
                         size: 1],
                headers: [Authorization: "Bearer $user1.token"],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        def results = response.getData() as JSONArray
        assert results.size() == 1
        assert results.get(0)["id"] == user2.id
        assert results.get(0)["username"] == user2.username
        assert results.get(0)["name"] == user2.name
        assert results.get(0)["status"] == user2.status
        assert results.get(0)["imagePath"] == Utils.DEFAULT_IMAGE_PATH
        assert results.get(0)["thumbnailPath"] == Utils.DEFAULT_THUMBNAIL_PATH
        assert results.get(0)["relationship"] == "STRANGER"
    }

    def "search deleted user profile"() {
        given: "user and deleted user"
        def user1 = new TestUser().register()
        def user2 = new TestUser().register().delete()

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH,
                params: [username: user2.username],
                headers: [Authorization: "Bearer $user1.token"],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        assert (response.getData() as JSONArray).size() == 0
    }

    def "search nonexistent user profile"() {
        given: "user and nonexistent id"
        def user = new TestUser().register()
        def nonexistentUsername = DataGenerator.createValidUsername()

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH,
                params: [username: nonexistentUsername],
                headers: ["Authorization": "Bearer $user.token"],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        assert (response.getData() as JSONArray).size() == 0
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
