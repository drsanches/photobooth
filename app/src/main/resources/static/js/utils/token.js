import Cookies from "/ui/js/lib/js.cookie.m.js";

function setToken(token) {
    Cookies.set("Authorization", "Bearer " + token);
}

function getToken() {
    return Cookies.get("Authorization");
}

function removeToken() {
    Cookies.remove("Authorization");
}

var Token = {
    set: setToken,
    get: getToken,
    remove: removeToken
}

export default Token;
