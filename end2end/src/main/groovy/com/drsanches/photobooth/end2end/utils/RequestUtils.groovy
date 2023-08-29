package com.drsanches.photobooth.end2end.utils

import org.json.JSONArray
import org.json.JSONObject

class RequestUtils {

    static RestClient getRestClient() {
        return new RestClient()
    }

    static String registerUser(String username, String password, String email) {
        try {
            def response = getRestClient().post(
                    path: "/api/v1/auth/registration",
                    body: [username: username,
                           password: password,
                           email: email])
            return response.data["accessToken"]
        } catch(Exception e) {
            e.printStackTrace()
            return null
        }
    }

    static JSONObject getAuthInfo(String username, String password) {
        String token = getToken(username, password)
        if (token == null) {
            return null
        }
        return getAuthInfo(token)
    }

    static JSONObject getAuthInfo(String token) {
        try {
            def response = getRestClient().get(
                    path: "/api/v1/auth/info",
                    headers: [Authorization: "Bearer $token"])
            return response.status == 200 ? response.data as JSONObject : null
        } catch(Exception e) {
            e.printStackTrace()
            return null
        }
    }

    static JSONObject getUserProfile(String username, String password) {
        String token = getToken(username, password)
        if (token == null) {
            return null
        }
        return getUserProfile(token)
    }

    static JSONObject getUserProfile(String token) {
        try {
            def response = getRestClient().get(
                    path: "/api/v1/profile",
                    headers: [Authorization: "Bearer $token"])
            return response.status == 200 ? response.data as JSONObject : null
        } catch(Exception e) {
            e.printStackTrace()
            return null
        }
    }

    static void changeUserProfile(String token, String name, String status) {
        getRestClient().put(
                path: "/api/v1/profile",
                headers: [Authorization: "Bearer $token"],
                body: [name: name,
                       status: status])
    }

    static void sendFriendRequest(String token, String userId) {
        getRestClient().post(
                path: "/api/v1/friends/manage/add",
                headers: [Authorization: "Bearer $token"],
                body: [userId: userId])
    }

    static JSONArray getIncomingRequests(String token) {
        try {
            def response = getRestClient().get(
                    path: "/api/v1/friends/requests/incoming",
                    headers: [Authorization: "Bearer $token"])
            return response.status == 200 ? response.data as JSONArray : null
        } catch(Exception e) {
            e.printStackTrace()
            return null
        }
    }

    static JSONArray getOutgoingRequests(String token) {
        try {
            def response = getRestClient().get(
                    path: "/api/v1/friends/requests/outgoing",
                    headers: [Authorization: "Bearer $token"])
            return response.status == 200 ? response.data as JSONArray : null
        } catch(Exception e) {
            e.printStackTrace()
            return null
        }
    }

    static JSONArray getFriends(String token) {
        try {
            def response = getRestClient().get(
                    path: "/api/v1/friends",
                    headers: [Authorization: "Bearer $token"])
            return response.status == 200 ? response.data as JSONArray : null
        } catch(Exception e) {
            e.printStackTrace()
            return null
        }
    }

    static void deleteUser(String token, String password) {
        getRestClient().post(
                path: "/api/v1/auth/deleteUser",
                headers: [Authorization: "Bearer $token"],
                body: [password: password])
    }

    static String getToken(String username, String password) {
        try {
            def response = getRestClient().post(
                    path: "/api/v1/auth/login",
                    body: [username: username,
                           password: password])
            return response.status == 200 ? response.data["accessToken"] : null
        } catch (Exception e) {
            e.printStackTrace()
            return null
        }
    }

    static JSONObject getTokenInfo(String username, String password) {
        try {
            def response = getRestClient().post(
                    path: "/api/v1/auth/login",
                    body: [username: username,
                           password: password])
            return response.status == 200 ? response.data as JSONObject : null
        } catch (Exception e) {
            e.printStackTrace()
            return null
        }
    }

    static byte[] getImage(String token, String path) {
        try {
            def response = getRestClient().getBytes(
                    path: path,
                    headers: [Authorization: "Bearer $token"])
            return response.status == 200 ? (byte[]) response.data : null
        } catch(Exception e) {
            e.printStackTrace()
            return null
        }
    }

    static void uploadAvatar(String token, byte[] image) {
        getRestClient().post(
                path: "/api/v1/image/avatar",
                headers: [Authorization: "Bearer $token"],
                body: [file: Utils.toBase64(image)])
    }

    static JSONArray getAllImagesInfo(String token) {
        try {
            def response = getRestClient().get(
                    path: "/api/v1/image/all",
                    headers: [Authorization: "Bearer $token"])
            return response.status == 200 ? response.data as JSONArray : null
        } catch(Exception e) {
            e.printStackTrace()
            return null
        }
    }

    static void sendPhoto(String token, List<String> userIds, byte[] image) {
        getRestClient().post(
                path: "/api/v1/image/photo",
                headers: [Authorization: "Bearer $token"],
                body: [file: Utils.toBase64(image),
                       userIds: userIds])
    }
}
