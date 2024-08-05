import {sha256} from '/ui/js/lib/sha256.js'
import {setToken, getToken, deleteToken} from "/ui/js/utils/token.js"

const API_BASE_URL = window.location.protocol + "//" + window.location.host + "/api/v1";

function login(username, password, onSuccess, onError) {
    var body = {
        username: username,
        password: sha256(password),
    };
    sendData("/auth/token", "POST", body, true, data => {
        setToken(data.accessToken);
        onSuccess();
    }, onError);
}

function getInfo(onSuccess) {
    getData("/auth/account", true, onSuccess);
}

function logout(onSuccess) {
    sendData("/auth/token", "DELETE", null, false, () => {
        deleteToken();
        onSuccess();
    });
}

function sendData(path, method, body, withResponseData, onSuccess, onError) {
    var response;
    if (body != null) {
        response = {
            method: method,
            headers: {
                "Content-Type": "application/json",
                "Authorization": getToken()
            },
            body: JSON.stringify(body)
        }
    } else {
        response = {
            method: method,
            headers: {
                "Authorization": getToken()
            }
        }
    }
    fetch(API_BASE_URL + path, response)
    .then(response => {
        if (response.ok) {
            if (withResponseData) {
                response.json().then(data => onSuccess(data));
            } else {
                onSuccess();
            }
        } else {
            if (onError != null) {
                response.json().then(data => {
                    processError(response.status, data);
                    onError();
                });
            }
        }
    })
    .catch(error => console.error(error));
}

function getData(path, withResponseData, onSuccess) {
    return fetch(API_BASE_URL + path, {
        method: 'GET',
        headers: {
            "Authorization": getToken()
        }
    })
    .then(response => {
        if (response.ok) {
            if (withResponseData) {
                response.json().then(data => onSuccess(data));
            } else {
                onSuccess();
            }
        } else {
            response.json().then(data => processError(response.status, data));
        }
    })
    .catch(error => console.error(error));
}

function processError(status, data) {
    if (status == 401) {
        deleteToken();
    }
    console.error(JSON.stringify(data));
}

var API = {
    login: login,
    getInfo: getInfo,
    logout: logout
}

export default API;
