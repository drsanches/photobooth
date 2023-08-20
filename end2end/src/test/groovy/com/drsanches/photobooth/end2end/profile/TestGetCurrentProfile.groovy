package com.drsanches.photobooth.end2end.profile

import com.drsanches.photobooth.end2end.utils.RequestUtils
import com.drsanches.photobooth.end2end.utils.TestUser
import com.drsanches.photobooth.end2end.utils.Utils
import org.apache.commons.lang3.StringUtils
import spock.lang.Specification

class TestGetCurrentProfile extends Specification {

    String PATH = "/api/v1/profile"

    def "successful current user profile getting"() {
        given: "user with profile"
        def user = new TestUser().register().fillProfile()

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH,
                headers: [Authorization: "Bearer $user.token"])

        then: "response is correct"
        assert response.status == 200
        assert response.data["id"] == user.id
        assert response.data["username"] == user.username
        assert response.data["name"] == user.name
        assert response.data["status"] == user.status
        assert response.data["imagePath"] == Utils.DEFAULT_IMAGE_PATH
        assert response.data["thumbnailPath"] == Utils.DEFAULT_THUMBNAIL_PATH
        assert response.data["relationship"] == "CURRENT"
    }

    def "get current user profile with invalid token"() {
        given: "invalid token"
        def token = UUID.randomUUID().toString()

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH,
                headers: [Authorization: "Bearer $token"])

        then: "response is correct"
        assert StringUtils.isNotEmpty(response.data["uuid"] as CharSequence)
        assert response.data["message"] == "Wrong token"
        assert response.status == 401
    }
}
