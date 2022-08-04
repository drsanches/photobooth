package ru.drsanches.photobooth.utils

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.RESTClient
import net.sf.json.JSONArray
import net.sf.json.JSONObject
import org.apache.http.HttpEntity
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.HttpClients

class RequestUtils {

    static final String SERVER_URL = "http://localhost"

    static final String PORT = "8080"

    static RESTClient getRestClient() {
        return new RESTClient( "$SERVER_URL:$PORT")
    }

    /**
     * Registers user and returns user id
     * @return user id
     */
    static String registerUser(String username, String password, String email) {
        try {
            HttpResponseDecorator response = getRestClient().post(
                    path: '/api/v1/auth/registration',
                    body: [username: username,
                           password: password,
                           email: email],
                    requestContentType: ContentType.JSON) as HttpResponseDecorator
            return response.status == 201 ? response.getData()["id"] : null
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

    static void changeUserProfile(String token, String firstName, String lastName) {
        getRestClient().put(
                path: '/api/v1/profile',
                headers: ["Authorization": "Bearer $token"],
                body: [firstName: firstName,
                       lastName: lastName],
                requestContentType: ContentType.JSON)
    }

    static void sendFriendRequest(String username, String password, String userId) {
        String token = getToken(username, password)
        getRestClient().post(
                path: "/api/v1/friends/manage/add",
                headers: ["Authorization": "Bearer $token"],
                body: ["userId": userId],
                requestContentType: ContentType.JSON)
    }

    static void deleteFriendRequest(String username, String password, String userId) {
        String token = getToken(username, password)
        getRestClient().post(
                path: "/api/v1/friends/manage/delete",
                headers: ["Authorization": "Bearer $token"],
                body: ["userId": userId],
                requestContentType : ContentType.JSON)
    }

    static JSONArray getIncomingRequests(String username, String password) {
        String token = getToken(username, password)
        if (token == null) {
            return null
        }
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

    static JSONArray getOutgoingRequests(String username, String password) {
        String token = getToken(username, password)
        if (token == null) {
            return null
        }
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

    static JSONArray getFriends(String username, String password) {
        String token = getToken(username, password)
        if (token == null) {
            return null
        }
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

    static void deleteUser(String username, String password) {
        String token = getToken(username, password)
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

    static String getRefreshToken(String username, String password) {
        try {
            HttpResponseDecorator response = getRestClient().post(
                    path: "/api/v1/auth/login",
                    body: ["username": username,
                           "password": password],
                    requestContentType : ContentType.JSON) as HttpResponseDecorator
            return response.status == 200 ? response.getData()["refreshToken"] : null
        } catch (Exception e) {
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

    static void uploadTestAvatar(String token) {
        HttpEntity entity = Utils.createTestImageMultipart()
        HttpPost httpPost = new HttpPost("$SERVER_URL:$PORT/api/v1/image/avatar")
        httpPost.addHeader("Authorization", "Bearer $token")
        httpPost.setEntity(entity)
        CloseableHttpResponse response = HttpClients.createDefault().execute(httpPost)
        int status = response.getStatusLine().getStatusCode()
        if (status != 201) {
            throw new RuntimeException("Avatar upload error, response status $status")
        }
    }

    static void deleteAvatar(String token) {
        getRestClient().delete(
                path: "/api/v1/image/avatar",
                headers: ["Authorization": "Bearer $token"])
    }
}