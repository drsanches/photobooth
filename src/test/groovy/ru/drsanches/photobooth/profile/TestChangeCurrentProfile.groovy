package ru.drsanches.photobooth.profile

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import net.sf.json.JSONNull
import net.sf.json.JSONObject
import org.apache.commons.lang3.RandomStringUtils
import ru.drsanches.photobooth.utils.DataGenerator
import ru.drsanches.photobooth.utils.RequestUtils
import ru.drsanches.photobooth.utils.TestUser
import ru.drsanches.photobooth.utils.Utils
import spock.lang.Specification

class TestChangeCurrentProfile extends Specification {

    String PATH = "/api/v1/profile"

    def "success user profile change"() {
        given: "user and new profile data"
        def user = new TestUser().register()
        def name = DataGenerator.createValidName()
        def status = DataGenerator.createValidStatus()

        when: "request is sent"
        def response = RequestUtils.getRestClient().put(
                path: PATH,
                headers: ["Authorization": "Bearer $user.token"],
                body:  [name: name,
                        status: status],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200

        and: "user profile was updated"
        JSONObject userProfile = RequestUtils.getUserProfile(user.username, user.password)
        assert userProfile['id'] == user.id
        assert userProfile['username'] == user.username
        assert userProfile['name'] == name
        assert userProfile['status'] == status
        assert userProfile['imagePath'] == Utils.DEFAULT_IMAGE_PATH
        assert userProfile['thumbnailPath'] == Utils.DEFAULT_THUMBNAIL_PATH
    }

    def "success user profile clean"() {
        given: "user with profile data"
        def user = new TestUser().register().fillProfile()

        when: "request is sent"
        def response = RequestUtils.getRestClient().put(
                path: PATH,
                headers: ["Authorization": "Bearer $user.token"],
                body:  [name: null,
                        status: null],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200

        and: "user profile was updated"
        JSONObject userProfile = RequestUtils.getUserProfile(user.username, user.password)
        assert userProfile['id'] == user.id
        assert userProfile['username'] == user.username
        assert userProfile['name'] == JSONNull.instance
        assert userProfile['status'] == JSONNull.instance
        assert userProfile['imagePath'] == Utils.DEFAULT_IMAGE_PATH
        assert userProfile['thumbnailPath'] == Utils.DEFAULT_THUMBNAIL_PATH
    }

    def "user profile change with invalid data"() {
        given: "user"
        def user = new TestUser().register()

        when: "request is sent"
        RequestUtils.getRestClient().put(
                path: PATH,
                headers: ["Authorization": "Bearer $user.token"],
                body:  [name: name,
                        status: status],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400

        where:
        name                                     | status
        RandomStringUtils.randomAlphabetic(101)  | DataGenerator.createValidStatus()
        DataGenerator.createValidName()          | RandomStringUtils.randomAlphabetic(51)
    }

    def "user profile change with invalid token"() {
        given: "invalid token"
        def token = UUID.randomUUID().toString()

        when: "request is sent"
        RequestUtils.getRestClient().put(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 401
    }
}
