package com.drsanches.photobooth.end2end.admin

import com.drsanches.photobooth.end2end.utils.RequestUtils
import com.drsanches.photobooth.end2end.utils.TestUser
import com.drsanches.photobooth.end2end.utils.Utils
import spock.lang.Specification

class TestAdminUrls extends Specification {

    static def ADMIN_PATHS = ["/actuator", "/h2-console/login.jsp", "/swagger-ui/index.html"]
    static def PUBLIC_PATHS = ["/actuator/health"]

    //TODO: Get from env
    def USERNAME = "admin"
    def PASSWORD = Utils.sha256("admin")

    def "successful admin url access"() {
        given: "admin token"
        def token = RequestUtils.getToken(USERNAME, PASSWORD)

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: path,
                headers: [Authorization: "Bearer $token"])

        then: "access"
        assert response.status == 200

        where:
        path << ADMIN_PATHS
    }

    def "successful user url access"() {
        given: "admin token"
        def user = new TestUser().register()

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: path,
                headers: [Authorization: "Bearer $user.token"])

        then: "access"
        assert response.status == 200

        where:
        path << PUBLIC_PATHS
    }

    def "successful unauthorized url access"() {
        when: "request is sent"
        def response = RequestUtils.getRestClient().get(path: path)

        then: "access"
        assert response.status == 200

        where:
        path << PUBLIC_PATHS
    }

    def "user url access denial"() {
        given: "admin token"
        def user = new TestUser().register()

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: path,
                headers: [Authorization: "Bearer $user.token"])

        then: "access"
        assert response.status == 403

        where:
        path << ADMIN_PATHS
    }

    def "unauthorized url access denial"() {
        when: "request is sent"
        def response = RequestUtils.getRestClient().get(path: path)

        then: "access"
        assert response.status == 401

        where:
        path << ADMIN_PATHS
    }
}