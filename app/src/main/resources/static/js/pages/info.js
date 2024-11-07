export var info = {
    template: `
        <content-container active="info">
            <div class="info">
                <b>Public health check:</b> <a href=\"/actuator/health\" target="_blank">/actuator/health</a><br><br>
                <b>Admin links:</b><br>
                <a href="/h2-console" target="_blank">h2-console</a> - db access<br>
                <a href="/actuator" target="_blank">actuator</a> - monitoring endpoints<br>
                <a href="/swagger-ui/index.html" target="_blank">swagger-ui</a> - API for developers
            </div>
        </content-container>
    `
}
