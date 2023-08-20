package com.drsanches.photobooth.end2end.utils

import groovyx.net.http.ContentType
import net.sf.json.JSONArray
import net.sf.json.JSONObject
import org.apache.commons.lang.StringUtils

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
                .POST(HttpRequest.BodyPublishers.ofString(JSONObject.fromObject(args.get("body")).toString()))
                .build()
        logRequest(request)
        def response = client.send(request, HttpResponse.BodyHandlers.ofString())
        logResponse(response)
        return prepareResponse(response)
    }

    static Map<String, Object> put(Map<String, ?> args) {
        def request = prepareRequest(args)
                .PUT(HttpRequest.BodyPublishers.ofString(JSONObject.fromObject(args.get("body")).toString()))
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
            request.header("content-type", ContentType.JSON as String)
        }

        if (args.containsKey("headers")) {
            (args.get("headers") as Map<String, Object>).forEach { key, value -> request.header(key, value as String)}
        }

        return request
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
            if (StringUtils.isEmpty(response.body())) {
                return [
                        status: response.statusCode()
                ]
            } else {
                switch (response.body().charAt(0)) {
                    case '{' -> [
                            status: response.statusCode(),
                            data: JSONObject.fromObject(response.body())
                    ]
                    case '[' -> [
                            status: response.statusCode(),
                            data: JSONArray.fromObject(response.body())
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
