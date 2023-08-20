package com.drsanches.photobooth.end2end.image

import com.drsanches.photobooth.end2end.utils.DataGenerator
import com.drsanches.photobooth.end2end.utils.RequestUtils
import com.drsanches.photobooth.end2end.utils.TestUser
import com.drsanches.photobooth.end2end.utils.Utils
import org.apache.commons.lang3.StringUtils
import spock.lang.Specification

class TestDeleteAvatar extends Specification {

    String PATH = "/api/v1/image/avatar"

    def "successful default avatar deletion"() {
        given: "user"
        def user = new TestUser().register()

        when: "request is sent"
        def response = RequestUtils.getRestClient().delete(
                path: PATH,
                headers: [Authorization: "Bearer $user.token"])

        then: "response is correct"
        assert response.status == 200
        assert user.getUserProfile()["imagePath"] == Utils.DEFAULT_IMAGE_PATH
        assert user.getUserProfile()["thumbnailPath"] == Utils.DEFAULT_THUMBNAIL_PATH
    }

    def "successful avatar deletion"() {
        given: "user"
        def image = DataGenerator.createValidImage()
        def user = new TestUser().register().uploadAvatar(image)
        def oldImagePath = user.imagePath
        def oldThumbnailPath = user.thumbnailPath

        when: "request is sent"
        def response = RequestUtils.getRestClient().delete(
                path: PATH,
                headers: [Authorization: "Bearer $user.token"])

        then: "response is correct"
        assert response.status == 200
        assert user.getUserProfile()["imagePath"] == Utils.DEFAULT_IMAGE_PATH
        assert user.getUserProfile()["thumbnailPath"] == Utils.DEFAULT_THUMBNAIL_PATH

        and: "the old image is available"
        assert image == RequestUtils.getImage(user.token, oldImagePath)
        assert Utils.toThumbnail(image) == RequestUtils.getImage(user.token, oldThumbnailPath)
    }

    def "delete avatar with invalid token"() {
        given: "invalid token"
        def token = UUID.randomUUID().toString()

        when: "request is sent"
        def response = RequestUtils.getRestClient().delete(
                path: PATH,
                headers: [Authorization: "Bearer $token"])

        then: "response is correct"
        assert response.status == 401
        assert StringUtils.isNotEmpty(response.data["uuid"] as CharSequence)
        assert response.data["message"] == "Wrong token"
    }
}
