package com.drsanches.photobooth.end2end.profile

import com.drsanches.photobooth.end2end.utils.DataGenerator
import com.drsanches.photobooth.end2end.utils.RequestUtils
import com.drsanches.photobooth.end2end.utils.TestUser
import com.drsanches.photobooth.end2end.utils.Utils
import org.json.JSONObject
import spock.lang.Specification

class TestDeleteProfilePhoto extends Specification {

    String PATH = "/api/v1/app/profile/photo"

    def "successful default profile photo deletion"() {
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

    def "successful profile photo deletion"() {
        given: "user"
        def image = DataGenerator.createValidImage()
        def user = new TestUser().register().uploadProfilePhoto(image)
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

    def "delete profile photo with invalid token"() {
        given: "invalid token"
        def token = UUID.randomUUID().toString()

        when: "request is sent"
        def response = RequestUtils.getRestClient().delete(
                path: PATH,
                headers: [Authorization: "Bearer $token"])

        then: "response is correct"
        assert response.status == 401
        assert Utils.validateErrorResponse(response.data as JSONObject, "wrong.token", null)
    }
}
