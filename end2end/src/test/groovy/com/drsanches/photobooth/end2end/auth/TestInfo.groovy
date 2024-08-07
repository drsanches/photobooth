package com.drsanches.photobooth.end2end.auth

import com.drsanches.photobooth.end2end.utils.RequestUtils
import com.drsanches.photobooth.end2end.utils.TestUser
import com.drsanches.photobooth.end2end.utils.Utils
import org.json.JSONObject
import spock.lang.Specification

class TestInfo extends Specification {

    String PATH = "/api/v1/auth/account"

    def "successful auth info getting"() {
        given: "user"
        def user = new TestUser().register()

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH,
                headers: [Authorization: "Bearer $user.token"])

        then: "response is correct"
        assert response.status == 200
        assert response.data["id"] == user.id
        assert response.data["username"] == user.username
        assert response.data["email"] == user.email
        assert response.data["passwordExists"] == true
        assert response.data["googleAuth"] == JSONObject.NULL
    }

    def "get auth info with invalid token"() {
        given: "invalid token"
        def token = UUID.randomUUID().toString()

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH,
                headers: [Authorization: "Bearer $token"])

        then: "response is correct"
        assert response.status == 401
        assert Utils.validateErrorResponse(response.data as JSONObject, "Wrong token", null)
    }
}
