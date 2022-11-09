package ru.drsanches.photobooth.profile

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import ru.drsanches.photobooth.utils.RequestUtils
import ru.drsanches.photobooth.utils.TestUser
import ru.drsanches.photobooth.utils.Utils
import spock.lang.Specification

class TestGetCurrentProfile extends Specification {

    String PATH = "/api/v1/profile"

    def "successful current user profile getting"() {
        given: "user with profile"
        def user = new TestUser().register().fillProfile()

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH,
                headers: ["Authorization": "Bearer $user.token"],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        assert response.getData()["id"] == user.id
        assert response.getData()["username"] == user.username
        assert response.getData()["name"] == user.name
        assert response.getData()["status"] == user.status
        assert response.getData()["imagePath"] == Utils.getDefaultImagePath()
        assert response.getData()['thumbnailPath'] == Utils.getDefaultThumbnailPath()
    }

    def "get current user profile with invalid token"() {
        given: "invalid token"
        def token = UUID.randomUUID().toString()

        when: "request is sent"
        RequestUtils.getRestClient().get(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                requestContentType : ContentType.JSON)

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 401
    }
}
