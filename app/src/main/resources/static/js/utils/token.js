import Cookies from "/ui/js/lib/js.cookie.m.js";

export function setToken(token) {
    Cookies.set("Authorization", "Bearer " + token);
}

export function getToken() {
    return Cookies.get("Authorization");
}

export function deleteToken() {
    Cookies.remove("Authorization");
}
