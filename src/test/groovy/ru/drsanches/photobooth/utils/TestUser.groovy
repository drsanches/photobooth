package ru.drsanches.photobooth.utils

import net.sf.json.JSONArray
import net.sf.json.JSONObject

class TestUser {

    String id

    String username

    String password

    String email

    String token

    String name

    String status

    String imagePath

    String thumbnailPath

    TestUser() {}

    TestUser(String username, String password) {
        this.username = username
        this.password = password
        this.token = RequestUtils.getToken(this.username, this.password)
        JSONObject authInfo = RequestUtils.getAuthInfo(this.token)
        this.id = authInfo["id"]
        this.email = authInfo["email"]
        JSONObject userProfile = RequestUtils.getUserProfile(this.token)
        this.name = userProfile["name"]
        this.status = userProfile["status"]
        this.imagePath = userProfile["imagePath"]
        this.thumbnailPath = userProfile["thumbnailPath"]
    }

    TestUser register() {
        username = DataGenerator.createValidUsername()
        password = DataGenerator.createValidPassword()
        email = DataGenerator.createValidEmail()
        token = RequestUtils.registerUser(username, password, email)
        id = RequestUtils.getAuthInfo(token)["id"]
        imagePath = Utils.getDefaultImagePath()
        thumbnailPath = Utils.getDefaultThumbnailPath()
        return this
    }

    TestUser fillProfile() {
        this.name = DataGenerator.createValidName()
        this.status = DataGenerator.createValidStatus()
        RequestUtils.changeUserProfile(token, name, status)
        return this
    }

    TestUser uploadAvatar(byte[] image) {
        RequestUtils.uploadAvatar(token, image)
        JSONObject userProfile = RequestUtils.getUserProfile(token)
        this.imagePath = userProfile["imagePath"]
        this.thumbnailPath = userProfile["thumbnailPath"]
        return this
    }

    TestUser sendFriendRequest(String userId) {
        RequestUtils.sendFriendRequest(token, userId)
        return this
    }

    TestUser sendPhoto(List<String> userIds, byte[] image) {
        RequestUtils.sendPhoto(token, userIds, image)
        return this
    }

    TestUser delete() {
        RequestUtils.deleteUser(token, password)
        return this
    }

    JSONObject getAuthInfo() {
        return RequestUtils.getAuthInfo(token)
    }

    JSONObject getUserProfile() {
        return RequestUtils.getUserProfile(token)
    }

    JSONArray getIncomingFriendRequests() {
        return RequestUtils.getIncomingRequests(token)
    }

    JSONArray getOutgoingFriendRequests() {
        return RequestUtils.getOutgoingRequests(token)
    }

    JSONArray getFriends() {
        return RequestUtils.getFriends(token)
    }

    JSONArray getAllImagesInfo() {
        return RequestUtils.getAllImagesInfo(token)
    }
}
