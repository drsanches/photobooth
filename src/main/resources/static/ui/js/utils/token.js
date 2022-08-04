import Cookies from "/ui/js/lib/js.cookie.m.js";

export function setToken(token) {
    localStorage.setItem("token", "Bearer " + token);
    Cookies.set("Authorization", "Bearer " + token);
}

export function getToken() {
    return localStorage.getItem("token");
}

export function isAuthorized() {
    return getToken() != null;
}

export function deleteToken() {
    localStorage.removeItem("token");
    Cookies.remove("Authorization");
}