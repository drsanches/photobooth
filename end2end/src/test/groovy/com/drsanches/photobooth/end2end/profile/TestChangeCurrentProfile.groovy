package com.drsanches.photobooth.end2end.profile

import com.drsanches.photobooth.end2end.utils.DataGenerator
import com.drsanches.photobooth.end2end.utils.RequestUtils
import com.drsanches.photobooth.end2end.utils.TestUser
import com.drsanches.photobooth.end2end.utils.Utils
import org.apache.commons.lang3.RandomStringUtils
import org.json.JSONObject
import spock.lang.Specification

class TestChangeCurrentProfile extends Specification {

    String PATH = "/api/v1/app/profile"

    def "success user profile change"() {
        given: "user and new profile data"
        def user = new TestUser().register()
        def name = DataGenerator.createValidName()
        def status = DataGenerator.createValidStatus()

        when: "request is sent"
        def response = RequestUtils.getRestClient().put(
                path: PATH,
                headers: [Authorization: "Bearer $user.token"],
                body: [name: name,
                       status: status])

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
        assert userProfile["incomingRequestsCount"] == 0
        assert userProfile["outgoingRequestsCount"] == 0
        assert userProfile["friendsCount"] == 0
    }

    def "success user profile clean"() {
        given: "user with profile data"
        def user = new TestUser().register().fillProfile()

        when: "request is sent"
        def response = RequestUtils.getRestClient().put(
                path: PATH,
                headers: [Authorization: "Bearer $user.token"],
                body: [name: null,
                       status: null])

        then: "response is correct"
        assert response.status == 200

        and: "user profile was updated"
        JSONObject userProfile = RequestUtils.getUserProfile(user.username, user.password)
        assert userProfile["id"] == user.id
        assert userProfile["username"] == user.username
        assert userProfile["name"] == JSONObject.NULL
        assert userProfile["status"] == JSONObject.NULL
        assert userProfile["imagePath"] == Utils.DEFAULT_IMAGE_PATH
        assert userProfile["thumbnailPath"] == Utils.DEFAULT_THUMBNAIL_PATH
        assert userProfile["relationship"] == "CURRENT"
        assert userProfile["incomingRequestsCount"] == 0
        assert userProfile["outgoingRequestsCount"] == 0
        assert userProfile["friendsCount"] == 0
    }

    def "user profile change with invalid data"() {
        given: "user"
        def user = new TestUser().register()

        when: "request is sent"
        def response = RequestUtils.getRestClient().put(
                path: PATH,
                headers: [Authorization: "Bearer $user.token"],
                body: [name: name,
                       status: status])

        then: "response is correct"
        assert response.status == 400
        assert Utils.validateErrorResponse(response.data as JSONObject, "validation.error", details)

        where:
        name << [
                RandomStringUtils.randomAlphabetic(101),
                "#"
        ]
        status << [
                RandomStringUtils.randomAlphabetic(51),
                "#"
        ]
        details << [
                [
                        Map.of("field", "name", "message", "length must be between 0 and 100"),
                        Map.of("field", "status", "message", "length must be between 0 and 50")
                ], [
                        Map.of("field", "name", "message", "wrong name format"),
                        Map.of("field", "status", "message", "wrong status format")
                ]
        ]
    }

    def "user profile change with invalid token"() {
        given: "invalid token"
        def token = UUID.randomUUID().toString()

        when: "request is sent"
        def response = RequestUtils.getRestClient().put(
                path: PATH,
                headers: [Authorization: "Bearer $token"])

        then: "response is correct"
        assert response.status == 401
        assert Utils.validateErrorResponse(response.data as JSONObject, "Wrong token", null)
    }
}
