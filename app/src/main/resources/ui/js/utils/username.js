import Cookies from "/ui/js/lib/js.cookie.m.js";

export function setUsername(username) {
    localStorage.setItem("username", username);
}

export function getUsername() {
    return localStorage.getItem("username");
}

export function hasUsername() {
    return getUsername() != null;
}

export function deleteUsername() {
    localStorage.removeItem("username");
}