package ru.drsanches.photobooth.image

import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.mime.MultipartEntityBuilder
import org.apache.http.impl.client.HttpClients
import ru.drsanches.photobooth.utils.DataGenerator
import ru.drsanches.photobooth.utils.RequestUtils
import ru.drsanches.photobooth.utils.Utils
import spock.lang.Specification

//TODO: Add validation tests (different file formats and sizes)
class TestUploadAvatar extends Specification {

    String PATH = "/api/v1/image/avatar"

    def "successful avatar upload"() {
        given: "user with token and image"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username, password, null)
        def token = RequestUtils.getToken(username, password)
        def entity = Utils.createTestImageMultipart()

        when: "request is sent"
        def httpPost = new HttpPost("$RequestUtils.SERVER_URL:$RequestUtils.PORT$PATH")
        httpPost.addHeader("Authorization", "Bearer $token")
        httpPost.setEntity(entity)
        def response = HttpClients.createDefault().execute(httpPost) as CloseableHttpResponse

        then: "response is correct"
        assert response.getStatusLine().getStatusCode() == 201

        and: "user profile contains new image path"
        def imagePath = RequestUtils.getUserProfile(username, password)['imagePath'] as String
        assert imagePath != Utils.getDefaultImagePath()

        and: "new image is correct"
        def image = RequestUtils.getImage(username, password, imagePath)
        assert image != null
        assert Utils.checkTestImage(image)
    }

    def "upload avatar with text multipart"() {
        given: "user with token and text"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username, password, null)
        def token = RequestUtils.getToken(username, password)

        MultipartEntityBuilder builder = MultipartEntityBuilder.create()
        builder.addTextBody("file", UUID.randomUUID().toString())
        def entity = builder.build()

        when: "request is sent"
        def httpPost = new HttpPost("$RequestUtils.SERVER_URL:$RequestUtils.PORT$PATH")
        httpPost.addHeader("Authorization", "Bearer $token")
        httpPost.setEntity(entity)
        def response = HttpClients.createDefault().execute(httpPost) as CloseableHttpResponse

        then: "response is correct"
        assert response.getStatusLine().getStatusCode() == 400

        and: "user profile does not change"
        def imagePath = RequestUtils.getUserProfile(username, password)['imagePath'] as String
        assert imagePath == Utils.getDefaultImagePath()
    }

    def "upload avatar without file"() {
        given: "user with token"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username, password, null)
        def token = RequestUtils.getToken(username, password)

        when: "request is sent"
        def httpPost = new HttpPost("$RequestUtils.SERVER_URL:$RequestUtils.PORT$PATH")
        httpPost.addHeader("Authorization", "Bearer $token")
        def response = HttpClients.createDefault().execute(httpPost) as CloseableHttpResponse

        then: "response is correct"
        assert response.getStatusLine().getStatusCode() == 400

        and: "user profile does not change"
        def imagePath = RequestUtils.getUserProfile(username, password)['imagePath'] as String
        assert imagePath == Utils.getDefaultImagePath()
    }

    def "upload avatar with invalid token"() {
        given: "user and invalid token"
        def username = DataGenerator.createValidUsername()
        def password = DataGenerator.createValidPassword()
        RequestUtils.registerUser(username, password, null)
        def token = UUID.randomUUID().toString()
        def entity = Utils.createTestImageMultipart()

        when: "request is sent"
        def httpPost = new HttpPost("$RequestUtils.SERVER_URL:$RequestUtils.PORT$PATH")
        httpPost.addHeader("Authorization", "Bearer $token")
        httpPost.setEntity(entity)
        def response = HttpClients.createDefault().execute(httpPost) as CloseableHttpResponse

        then: "response is correct"
        response.getStatusLine().getStatusCode() == 401
    }
}