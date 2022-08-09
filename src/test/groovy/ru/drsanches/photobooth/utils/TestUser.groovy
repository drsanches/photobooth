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

    TestUser register() {
        username = DataGenerator.createValidUsername()
        password = DataGenerator.createValidPassword()
        email = DataGenerator.createValidEmail()
        token = RequestUtils.registerUser(username, password, email)
        id = RequestUtils.getAuthInfo(token)["id"]
        imagePath = Utils.getDefaultImagePath()
        return this
    }

    TestUser fillProfile() {
        this.name = DataGenerator.createValidName()
        this.status = DataGenerator.createValidStatus()
        RequestUtils.changeUserProfile(token, name, status)
        return this
    }

    TestUser uploadTestAvatar() {
        RequestUtils.uploadTestAvatar(token)
        this.imagePath = RequestUtils.getUserProfile(token)["imagePath"]
        return this
    }

    TestUser sendFriendRequest(String userId) {
        RequestUtils.sendFriendRequest(token, userId)
        return this
    }

    TestUser sendTestPhoto(List<String> userIds) {
        RequestUtils.sendTestPhoto(token, userIds)
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

    byte[] getImageData() {
        String imagePath = RequestUtils.getUserProfile(token)["imagePath"]
        return RequestUtils.getImage(token, imagePath)
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
