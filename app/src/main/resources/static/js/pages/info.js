export var info = {
    template: `
        <content-container active="info">
            <div class="info">
                <b>Public health check:</b> <a href=\"/actuator/health\">/actuator/health</a><br><br>
                <b>Admin links:</b><br>
                <a href="/h2-console">h2-console</a> - db access<br>
                <a href="/actuator">actuator</a> - monitoring endpoints<br>
                <a href="/swagger-ui/index.html">swagger-ui</a> - API for developers
            </div>
        </content-container>
    `
}
