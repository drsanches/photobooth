package ru.drsanches.photobooth.utils

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.RESTClient
import net.sf.json.JSONArray
import net.sf.json.JSONObject

class RequestUtils {

    static final String SERVER_URL = "http://localhost:8080"

    static RESTClient getRestClient() {
        return new RESTClient(SERVER_URL)
    }

    static String registerUser(String username, String password, String email) {
        try {
            HttpResponseDecorator response = getRestClient().post(
                    path: '/api/v1/auth/registration',
                    body: [username: username,
                           password: password,
                           email: email],
                    requestContentType: ContentType.JSON) as HttpResponseDecorator
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
            HttpResponseDecorator response = getRestClient().get(
                    path: "/api/v1/auth/info",
                    headers: ["Authorization": "Bearer $token"]) as HttpResponseDecorator
            return response.status == 200 ? response.getData() as JSONObject : null
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
            HttpResponseDecorator response = getRestClient().get(
                    path: "/api/v1/profile",
                    headers: ["Authorization": "Bearer $token"]) as HttpResponseDecorator
            return response.status == 200 ? response.getData() as JSONObject : null
        } catch(Exception e) {
            e.printStackTrace()
            return null
        }
    }

    static void changeUserProfile(String token, String name, String status) {
        getRestClient().put(
                path: '/api/v1/profile',
                headers: ["Authorization": "Bearer $token"],
                body: [name: name,
                       status: status],
                requestContentType: ContentType.JSON)
    }

    static void sendFriendRequest(String token, String userId) {
        getRestClient().post(
                path: "/api/v1/friends/manage/add",
                headers: ["Authorization": "Bearer $token"],
                body: ["userId": userId],
                requestContentType: ContentType.JSON)
    }

    static JSONArray getIncomingRequests(String token) {
        try {
            HttpResponseDecorator response = getRestClient().get(
                    path: "/api/v1/friends/requests/incoming",
                    headers: ["Authorization": "Bearer $token"]) as HttpResponseDecorator
            return response.status == 200 ? response.getData() as JSONArray : null
        } catch(Exception e) {
            e.printStackTrace()
            return null
        }
    }

    static JSONArray getOutgoingRequests(String token) {
        try {
            HttpResponseDecorator response = getRestClient().get(
                    path: "/api/v1/friends/requests/outgoing",
                    headers: ["Authorization": "Bearer $token"]) as HttpResponseDecorator
            return response.status == 200 ? response.getData() as JSONArray : null
        } catch(Exception e) {
            e.printStackTrace()
            return null
        }
    }

    static JSONArray getFriends(String token) {
        try {
            HttpResponseDecorator response = getRestClient().get(
                    path: "/api/v1/friends",
                    headers: ["Authorization": "Bearer $token"]) as HttpResponseDecorator
            return response.status == 200 ? response.getData() as JSONArray : null
        } catch(Exception e) {
            e.printStackTrace()
            return null
        }
    }

    static void deleteUser(String token, String password) {
        getRestClient().post(
                path: "/api/v1/auth/deleteUser",
                headers: ["Authorization": "Bearer $token"],
                body:  [password: password],
                requestContentType : ContentType.JSON)
    }

    static String getToken(String username, String password) {
        try {
            HttpResponseDecorator response = getRestClient().post(
                    path: "/api/v1/auth/login",
                    body: ["username": username,
                            "password": password],
                    requestContentType : ContentType.JSON) as HttpResponseDecorator
            return response.status == 200 ? response.getData()["accessToken"] : null
        } catch (Exception e) {
            e.printStackTrace()
            return null
        }
    }

    static JSONObject getTokenInfo(String username, String password) {
        try {
            HttpResponseDecorator response = getRestClient().post(
                    path: "/api/v1/auth/login",
                    body: ["username": username,
                           "password": password],
                    requestContentType : ContentType.JSON) as HttpResponseDecorator
            return response.status == 200 ? response.getData() as JSONObject : null
        } catch (Exception e) {
            e.printStackTrace()
            return null
        }
    }

    static byte[] getImage(String token, String path) {
        try {
            HttpResponseDecorator response = getRestClient().get(
                    path: path,
                    headers: ["Authorization": "Bearer $token"]) as HttpResponseDecorator
            return response.status == 200 ? (response.getData() as ByteArrayInputStream).getBytes() as byte[] : null
        } catch(Exception e) {
            e.printStackTrace()
            return null
        }
    }

    static byte[] getImage(String username, String password, String path) {
        String token = getToken(username, password)
        if (token == null) {
            return null
        }
        try {
            HttpResponseDecorator response = getRestClient().get(
                    path: path,
                    headers: ["Authorization": "Bearer $token"]) as HttpResponseDecorator
            return response.status == 200 ? (response.getData() as ByteArrayInputStream).getBytes() as byte[] : null
        } catch(Exception e) {
            e.printStackTrace()
            return null
        }
    }

    static void uploadAvatar(String token, byte[] image) {
        getRestClient().post(
                path: '/api/v1/image/avatar',
                headers: ["Authorization": "Bearer $token"],
                body: [file: Utils.toBase64(image)],
                requestContentType: ContentType.JSON)
    }

    static JSONArray getAllImagesInfo(String token) {
        try {
            HttpResponseDecorator response = getRestClient().get(
                    path: "/api/v1/image/all",
                    headers: ["Authorization": "Bearer $token"]) as HttpResponseDecorator
            return response.status == 200 ? response.getData() as JSONArray : null
        } catch(Exception e) {
            e.printStackTrace()
            return null
        }
    }

    static void sendPhoto(String token, List<String> userIds, byte[] image) {
        getRestClient().post(
                path: '/api/v1/image/photo',
                headers: ["Authorization": "Bearer $token"],
                body:  [file: Utils.toBase64(image),
                        userIds: userIds],
                requestContentType : ContentType.JSON)
    }
}
