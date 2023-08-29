package com.drsanches.photobooth.end2end.utils

import org.apache.commons.lang3.StringUtils
import org.json.JSONArray
import org.json.JSONObject

import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class RestClient {

    static def URL = "http://localhost:8080"

    static def client = HttpClient.newHttpClient()

    static Map<String, Object> get(Map<String, ?> args) {
        def request = prepareRequest(args).GET().build()
        logRequest(request)
        def response = client.send(request, HttpResponse.BodyHandlers.ofString())
        logResponse(response)
        return prepareResponse(response)
    }

    static Map<String, Object> getBytes(Map<String, ?> args) {
        def request = prepareRequest(args).GET().build()
        logRequest(request)
        def response = client.send(request, HttpResponse.BodyHandlers.ofByteArray())
        logResponse(response)
        return prepareResponse(response)
    }

    static Map<String, Object> post(Map<String, ?> args) {
        def request = prepareRequest(args)
                .POST(getBodyPublisher(args))
                .build()
        logRequest(request)
        def response = client.send(request, HttpResponse.BodyHandlers.ofString())
        logResponse(response)
        return prepareResponse(response)
    }

    static Map<String, Object> put(Map<String, ?> args) {
        def request = prepareRequest(args)
                .PUT(getBodyPublisher(args))
                .build()
        logRequest(request)
        def response = client.send(request, HttpResponse.BodyHandlers.ofString())
        logResponse(response)
        return prepareResponse(response)
    }

    static def delete(Map<String, ?> args) {
        def request = prepareRequest(args).DELETE().build()
        logRequest(request)
        def response = client.send(request, HttpResponse.BodyHandlers.ofString())
        logResponse(response)
        return prepareResponse(response)
    }

    private static HttpRequest.Builder prepareRequest(Map<String, ?> args) {
        def uri = URL + args.get("path")
        if (args.containsKey("params")) {
            uri += "?"
            (args.get("params") as Map<String, String>).forEach {key, value -> uri += "$key=$value&" }
            uri = uri.substring(0, uri.length() - 1)
        }

        def request = HttpRequest.newBuilder()
                .uri(URI.create(uri))

        if (args.containsKey("body")) {
            request.header("Content-Type", "application/json")
        }

        if (args.containsKey("headers")) {
            (args.get("headers") as Map<String, Object>).forEach { key, value -> request.header(key, value as String)}
        }

        return request
    }

    static HttpRequest.BodyPublisher getBodyPublisher(Map<String, ?> args) {
        if (args.containsKey("body")) {
            return HttpRequest.BodyPublishers.ofString(new JSONObject(args.get("body")).toString())
        } else {
            return HttpRequest.BodyPublishers.noBody()
        }
    }

    private static logRequest(HttpRequest request) {
        println(request.method() + " " + request.uri())
    }

    private static logResponse(HttpResponse<?> response) {
        println(response.statusCode())
        println(response.body())
    }

    private static def prepareResponse(HttpResponse<?> response) {
        if (response.body() instanceof byte[]) {
            return [
                    status: response.statusCode(),
                    data: response.body()
            ]
        }
        if (response.body() instanceof String) {
            if (StringUtils.isEmpty(response.body() as String)) {
                return [
                        status: response.statusCode()
                ]
            } else {
                switch ((response.body() as String).charAt(0)) {
                    case '{' -> [
                            status: response.statusCode(),
                            data: new JSONObject(response.body())
                    ]
                    case '[' -> [
                            status: response.statusCode(),
                            data: new JSONArray(response.body())
                    ]
                    default -> [
                            status: response.statusCode(),
                            data: response.body()
                    ]
                }
            }
        }
    }
}
