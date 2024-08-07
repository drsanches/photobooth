package com.drsanches.photobooth.end2end.profile

import com.drsanches.photobooth.end2end.utils.RequestUtils
import com.drsanches.photobooth.end2end.utils.TestUser
import com.drsanches.photobooth.end2end.utils.Utils
import org.json.JSONObject
import spock.lang.Specification

class TestGetCurrentProfile extends Specification {

    String PATH = "/api/v1/app/profile"

    def "successful current user profile getting"() {
        given: "user with profile"
        def user = new TestUser().register().fillProfile()
        def friend1 = new TestUser().register().sendFriendRequest(user.id)
        def outgoing1 = new TestUser().register()
        def outgoing2 = new TestUser().register()
        new TestUser().register().sendFriendRequest(user.id)
        new TestUser().register().sendFriendRequest(user.id)
        new TestUser().register().sendFriendRequest(user.id)
        user.sendFriendRequest(outgoing1.id)
                .sendFriendRequest(outgoing2.id)
                .sendFriendRequest(friend1.id)

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH,
                headers: [Authorization: "Bearer $user.token"])

        then: "response is correct"
        assert response.status == 200
        assert response.data["id"] == user.id
        assert response.data["username"] == user.username
        assert response.data["name"] == user.name
        assert response.data["status"] == user.status
        assert response.data["imagePath"] == Utils.DEFAULT_IMAGE_PATH
        assert response.data["thumbnailPath"] == Utils.DEFAULT_THUMBNAIL_PATH
        assert response.data["relationship"] == "CURRENT"
        assert response.data["friendsCount"] == 1
        assert response.data["outgoingRequestsCount"] == 2
        assert response.data["incomingRequestsCount"] == 3
    }

    def "get current user profile with invalid token"() {
        given: "invalid token"
        def token = UUID.randomUUID().toString()

        when: "request is sent"
        def response = RequestUtils.getRestClient().get(
                path: PATH,
                headers: [Authorization: "Bearer $token"])

        then: "response is correct"
        assert response.status == 401
        assert Utils.validateErrorResponse(response.data as JSONObject, "Wrong token", null)
    }
}
