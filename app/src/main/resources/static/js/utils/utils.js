export function redirect(path) {
    window.location.href = window.location.protocol + "//" + window.location.host + path;
}
