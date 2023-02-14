import API from "/ui/js/utils/api.js";

export var adminContent = {
    data() {
        return {
            authorized: false
        }
    },
    mounted() {
        API.getInfo(data => {
            this.authorized = true;
        });
    },
    template: `
        <div>
            <div class="admin-content" v-if="authorized">
                <b>Public health check:</b> <a href=\"/actuator/health\">/actuator/health</a><br><br>
                <b>Admin links:</b><br>
                <a href="/h2-console">/h2-console</a> - db access<br>
                <a href="/actuator">/actuator</a> - monitoring endpoints<br>
                <a href="/swagger-ui.html">/swagger-ui.html</a> - API for developers
            </div>
        </div>
    `
}
