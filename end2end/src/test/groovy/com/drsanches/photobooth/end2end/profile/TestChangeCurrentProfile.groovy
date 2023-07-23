package com.drsanches.photobooth.end2end.profile

import com.drsanches.photobooth.end2end.utils.DataGenerator
import com.drsanches.photobooth.end2end.utils.RequestUtils
import com.drsanches.photobooth.end2end.utils.TestUser
import com.drsanches.photobooth.end2end.utils.Utils
import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import net.sf.json.JSONNull
import net.sf.json.JSONObject
import org.apache.commons.lang3.RandomStringUtils
import org.apache.commons.lang3.StringUtils
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
                headers: [Authorization: "Bearer $user.token"],
                body:  [name: name,
                        status: status],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200

        and: "user profile was updated"
        JSONObject userProfile = RequestUtils.getUserProfile(user.username, user.password)
        assert userProfile["id"] == user.id
        assert userProfile["username"] == user.username
        assert userProfile["name"] == name
        assert userProfile["status"] == status
        assert userProfile["imagePath"] == Utils.DEFAULT_IMAGE_PATH
        assert userProfile["thumbnailPath"] == Utils.DEFAULT_THUMBNAIL_PATH
        assert userProfile["relationship"] == "CURRENT"
    }

    def "success user profile clean"() {
        given: "user with profile data"
        def user = new TestUser().register().fillProfile()

        when: "request is sent"
        def response = RequestUtils.getRestClient().put(
                path: PATH,
                headers: [Authorization: "Bearer $user.token"],
                body:  [name: null,
                        status: null],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200

        and: "user profile was updated"
        JSONObject userProfile = RequestUtils.getUserProfile(user.username, user.password)
        assert userProfile["id"] == user.id
        assert userProfile["username"] == user.username
        assert userProfile["name"] == JSONNull.getInstance()
        assert userProfile["status"] == JSONNull.getInstance()
        assert userProfile["imagePath"] == Utils.DEFAULT_IMAGE_PATH
        assert userProfile["thumbnailPath"] == Utils.DEFAULT_THUMBNAIL_PATH
        assert userProfile["relationship"] == "CURRENT"
    }

    def "user profile change with invalid data"() {
        given: "user"
        def user = new TestUser().register()

        when: "request is sent"
        RequestUtils.getRestClient().put(
                path: PATH,
                headers: [Authorization: "Bearer $user.token"],
                body:  [name: name,
                        status: status],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert StringUtils.isNotEmpty(e.response.data["uuid"] as CharSequence)
        assert e.response.data["message"] == message
        assert e.response.status == 400

        where:
        name << [
                //Invalid name
                RandomStringUtils.randomAlphabetic(101),
                "#",

                //Invalid status
                DataGenerator.createValidName(),
                DataGenerator.createValidName()
        ]
        status << [
                //Invalid name
                DataGenerator.createValidStatus(),
                DataGenerator.createValidStatus(),

                //Invalid status
                RandomStringUtils.randomAlphabetic(51),
                "#"
        ]
        message << [
                //Invalid name
                "changeCurrentProfile.changeUserProfileDto.name: length must be between 0 and 100",
                "changeCurrentProfile.changeUserProfileDto.name: wrong name format",

                //Invalid status
                "changeCurrentProfile.changeUserProfileDto.status: length must be between 0 and 50",
                "changeCurrentProfile.changeUserProfileDto.status: wrong status format"
        ]
    }

    def "user profile change with invalid token"() {
        given: "invalid token"
        def token = UUID.randomUUID().toString()

        when: "request is sent"
        RequestUtils.getRestClient().put(
                path: PATH,
                headers: [Authorization: "Bearer $token"],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert StringUtils.isNotEmpty(e.response.data["uuid"] as CharSequence)
        assert e.response.data["message"] == "Wrong token"
        assert e.response.status == 401
    }
}
