package com.drsanches.photobooth.end2end.image

import com.drsanches.photobooth.end2end.utils.DataGenerator
import com.drsanches.photobooth.end2end.utils.RequestUtils
import com.drsanches.photobooth.end2end.utils.TestUser
import com.drsanches.photobooth.end2end.utils.Utils
import org.apache.commons.lang3.StringUtils
import spock.lang.Specification

class TestUploadAvatar extends Specification {

    String PATH = "/api/v1/image/avatar"

    def "successful avatar upload"() {
        given: "user and image"
        def user = new TestUser().register()
        def image = DataGenerator.createValidImage()

        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: [Authorization: "Bearer $user.token"],
                body: [file: Utils.toBase64(image)])

        then: "response is correct"
        assert response.status == 201

        and: "user profile contains new image path"
        def userProfile = user.getUserProfile()
        assert userProfile["imagePath"] != Utils.DEFAULT_IMAGE_PATH
        assert userProfile["thumbnailPath"] != Utils.DEFAULT_THUMBNAIL_PATH

        and: "new image is correct"
        assert image == RequestUtils.getImage(user.token, userProfile["imagePath"] as String)
        assert Utils.toThumbnail(image) == RequestUtils.getImage(user.token, userProfile["thumbnailPath"] as String)
    }

    def "upload avatar with invalid data"() {
        given: "user and invalid data"
        def user = new TestUser().register()

        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: [Authorization: "Bearer $user.token"],
                body: [file: invalidData])

        then: "response is correct"
        assert response.status == 400
        assert StringUtils.isNotEmpty(response.data["uuid"] as CharSequence)
        assert response.data["message"] == message

        and: "user profile does not change"
        def userProfile = user.getUserProfile()
        assert userProfile["imagePath"] == Utils.DEFAULT_IMAGE_PATH
        assert userProfile["thumbnailPath"] == Utils.DEFAULT_THUMBNAIL_PATH

        where:
        invalidData << [
                null,
                "",
                ";",
                Base64.getEncoder().encodeToString("test".getBytes()),
                Base64.getEncoder().encodeToString(new byte[300 * 1000 + 1])
        ]
        message << [
                "uploadAvatar.uploadAvatarDto.file: must not be empty",
                "uploadAvatar.uploadAvatarDto.file: must not be empty",
                "uploadAvatar.uploadAvatarDto.file: invalid base64 image",
                "uploadAvatar.uploadAvatarDto.file: invalid image data",
                "uploadAvatar.uploadAvatarDto.file: base64 string is too long, max image size is 300000 bytes"
        ]
    }

    def "upload avatar with invalid token"() {
        given: "invalid token"
        def token = UUID.randomUUID().toString()
        def base64Image = Utils.toBase64(DataGenerator.createValidImage())

        when: "request is sent"
        def response = RequestUtils.getRestClient().post(
                path: PATH,
                headers: [Authorization: "Bearer $token"],
                body: [file: base64Image])

        then: "response is correct"
        assert response.status == 401
        assert StringUtils.isNotEmpty(response.data["uuid"] as CharSequence)
        assert response.data["message"] == "Wrong token"
    }
}
