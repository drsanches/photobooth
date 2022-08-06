package ru.drsanches.photobooth.profile

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import net.sf.json.JSONObject
import ru.drsanches.photobooth.utils.DataGenerator
import ru.drsanches.photobooth.utils.RequestUtils
import ru.drsanches.photobooth.utils.Utils
import spock.lang.Specification

class TestChangeCurrentProfile extends Specification {

    String PATH = "/api/v1/profile"

    def "success user profile change"() {
        given: "user, token and new profile data"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        def userId = RequestUtils.registerUser(username, password, null)
        def token = RequestUtils.getToken(username, password)
        def name = DataGenerator.createValidName()
        def status = DataGenerator.createValidStatus()

        when: "request is sent"
        def response = RequestUtils.getRestClient().put(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                body:  [name: name,
                        status: status],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200

        and: "user profile was updated"
        JSONObject userProfile = RequestUtils.getUserProfile(username, password)
        userProfile['id'] == userId
        userProfile['username'] == username
        userProfile['name'] == name
        userProfile['status'] == status
        userProfile['imagePath'] == Utils.getDefaultImagePath()
    }

    def "user profile change with invalid token"() {
        given: "user, new profile data and invalid token"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username, password,  null)
        def token = UUID.randomUUID().toString()
        def name = DataGenerator.createValidName()
        def status = DataGenerator.createValidStatus()

        when: "request is sent"
        RequestUtils.getRestClient().put(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                body:  [name: name,
                        status: status],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 401
    }
}
