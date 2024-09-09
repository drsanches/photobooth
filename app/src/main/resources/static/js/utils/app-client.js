import {sha256} from '/ui/js/lib/sha256.js'
import Token from "/ui/js/utils/token.js"

const API_BASE_URL = window.location.protocol + "//" + window.location.host + "/api/v1";

function login(username, password, onSuccess, onError) {
    var request = {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": Token.get()
        },
        body: JSON.stringify({
            username: username,
            password: sha256(password),
        })
    };
    fetch(API_BASE_URL + "/auth/token", request)
        .then(response => {
            if (response.ok) {
                response.json().then(data => onSuccess(data));
            } else {
                response.json().then(data => onError(data));
            }
        })
        .catch(error => console.error(error));
}

function getInfo(onSuccess, onUnauthorized) {
    var request = {
        method: "GET",
        headers: {
            "Authorization": Token.get()
        }
    };
    fetch(API_BASE_URL + "/auth/account", request)
        .then(response => {
            if (response.ok) {
                response.json().then(data => onSuccess(data));
            } else {
                response.json().then(data => {
                    if (response.status == 401) {
                        onUnauthorized(data)
                    } else {
                        console.error(JSON.stringify(data));
                    }
                });
            }
        })
        .catch(error => console.error(error));
}

function logout(onResult) {
    var request = {
        method: "DELETE",
        headers: {
            "Authorization": Token.get()
        }
    };
    fetch(API_BASE_URL + "/auth/token", request)
        .then(response => onResult())
        .catch(error => console.error(error));
}

function createTestUser(username, password, onSuccess, onError) {
    var request = {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": Token.get()
        },
        body: JSON.stringify({
            username: username,
            password: sha256(password),
        })
    };
    fetch(API_BASE_URL + "/admin/test/user", request)
        .then(response => {
            if (response.ok) {
                response.json().then(data => onSuccess(data));
            } else {
                response.json().then(data => onError(data));
            }
        })
        .catch(error => console.error(error));
}

var AppClient = {
    login: login,
    getInfo: getInfo,
    logout: logout,
    createTestUser: createTestUser
}

export default AppClient;
