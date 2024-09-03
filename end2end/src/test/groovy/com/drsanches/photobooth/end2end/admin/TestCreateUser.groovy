package com.drsanches.photobooth.end2end.admin

import com.drsanches.photobooth.end2end.utils.DataGenerator
import com.drsanches.photobooth.end2end.utils.RequestUtils
import com.drsanches.photobooth.end2end.utils.TestUser
import com.drsanches.photobooth.end2end.utils.Utils
import spock.lang.Specification

class TestCreateUser extends Specification {

    String PATH = "/api/v1/admin/test/user"

    //TODO: Get from env
    def USERNAME = "admin"
    def PASSWORD = Utils.sha256("admin")

    def "successful user creation"() {
        given: "username, password, email and admin token"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        def email = DataGenerator.createValidEmail()
        def adminToken = RequestUtils.getToken(USERNAME, PASSWORD)

        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: [Authorization: "Bearer $adminToken"],
                body: [username: username,
                       password: password,
                       email: email])

        then: "response is correct"
        assert response.status == 200

        and: "user account exists"
        var userAuth = RequestUtils.getAuthInfo(username, password)
        assert userAuth != null

        and: "user profile exists"
        var anotherUserToken = new TestUser().register().getToken()
        assert RequestUtils.getAnotherUserProfile(userAuth["id"] as String, anotherUserToken) != null
    }

    def "user creation with user token"() {
        given: "username, password, email and user token"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        def email = DataGenerator.createValidEmail()
        def user = new TestUser().register()

        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: [Authorization: "Bearer $user.token"],
                body: [username: username,
                       password: password,
                       email: email])

        then: "access"
        assert response.status == 403
    }
}
