package ru.drsanches.photobooth.image

import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import ru.drsanches.photobooth.utils.RequestUtils
import ru.drsanches.photobooth.utils.TestUser
import ru.drsanches.photobooth.utils.Utils
import spock.lang.Specification

class TestDeleteAvatar extends Specification {

    String PATH = "/api/v1/image/avatar"

    def "successful default avatar deletion"() {
        given: "user"
        def user = new TestUser().register()

        when: "request is sent"
        def response = RequestUtils.getRestClient().delete(
                path: PATH,
                headers: ["Authorization": "Bearer $user.token"]) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        assert user.getUserProfile()["imagePath"] == Utils.getDefaultImagePath()

        and: "image does not change"
        assert Utils.checkDefaultImage(user.getImageData())
    }

    def "successful avatar deletion"() {
        given: "user"
        def user = new TestUser().register().uploadTestAvatar()
        def oldImagePath = user.imagePath

        when: "request is sent"
        def response = RequestUtils.getRestClient().delete(
                path: PATH,
                headers: ["Authorization": "Bearer $user.token"]) as HttpResponseDecorator

        then: "response is correct"
        assert response.status == 200
        assert user.getUserProfile()["imagePath"] == Utils.getDefaultImagePath()

        and: "image is correct"
        assert Utils.checkDefaultImage(user.getImageData())

        and: "the old image is available"
        def oldImageData = RequestUtils.getImage(user.username, user.password, oldImagePath)
        assert Utils.checkTestImage(oldImageData)
    }

    def "delete avatar with invalid token"() {
        given: "invalid token"
        def token = UUID.randomUUID().toString()

        when: "request is sent"
        RequestUtils.getRestClient().delete(
                path: PATH,
                headers: ["Authorization": "Bearer $token"])

        then: "response is correct"
        HttpResponseException e = thrown(HttpResponseException)
        assert e.response.status == 401
    }
}
