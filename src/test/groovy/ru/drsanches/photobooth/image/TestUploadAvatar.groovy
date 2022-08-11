package ru.drsanches.photobooth.image

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import ru.drsanches.photobooth.utils.RequestUtils
import ru.drsanches.photobooth.utils.TestUser
import ru.drsanches.photobooth.utils.Utils
import spock.lang.Specification

//TODO: Add validation tests (different file formats and sizes)
class TestUploadAvatar extends Specification {

    String PATH = "/api/v1/image/avatar"

    def "successful avatar upload"() {
        given: "user and image"
        def user = new TestUser().register()
        def base64Image = Utils.createTestBase64Image()

        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $user.token"],
                body:  [file: base64Image],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 201

        and: "user profile contains new image path"
        def imagePath = user.getUserProfile()['imagePath'] as String
        assert imagePath != Utils.getDefaultImagePath()

        and: "new image is correct"
        def image = RequestUtils.getImage(user.username, user.password, imagePath)
        assert image != null
        assert Utils.checkTestImage(image)
    }

    def "upload avatar with invalid data"() {
        given: "user and invalid data"
        def user = new TestUser().register()

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $user.token"],
                body:  [file: invalidData],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 400

        and: "user profile does not change"
        assert user.getUserProfile()['imagePath'] as String == Utils.getDefaultImagePath()

        where:
        invalidData << [null, "", ";", Base64.getEncoder().encodeToString("test".getBytes())]
    }

    def "upload avatar with invalid token"() {
        given: "invalid token"
        def token = UUID.randomUUID().toString()
        def base64Image = Utils.createTestBase64Image()

        when: "request is sent"
        RequestUtils.getRestClient().post(
                path: PATH,
                headers: ["Authorization": "Bearer $token"],
                body:  [file: base64Image],
                requestContentType : ContentType.JSON) as HttpResponseDecorator

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 401
    }
}
