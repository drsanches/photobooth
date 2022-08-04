import {followLink, getData} from "/ui/js/utils/common.js";
import {isAuthorized, deleteToken} from "/ui/js/utils/token.js";
import {hasUsername, getUsername, setUsername, deleteUsername} from "/ui/js/utils/username.js";

export var header = {
    data() {
        return {
            authorized: false,
            username: null
        }
    },
    methods: {
            home: () => followLink("/ui/index.html"),
            login: () => followLink("/ui/login.html"),
            logout: () => {
                getData("/auth/logout").then(data => {
                    deleteToken();
                    deleteUsername();
                    followLink("/ui/index.html");
                });
            }
        },
    mounted() {
        this.authorized = isAuthorized();
        if (isAuthorized() && !hasUsername()) {
            getData("/auth/info").then(data => {
                setUsername(data.username);
                followLink("/ui/index.html");
            });
        }
        this.username = getUsername();
    },
    template: `
        <div>
            <div class="header">
                <!-- Menu -->
                <span class="logo" v-on:click="home">PhotoBooth</span>

                <!-- Without token -->
                <div class="auth" v-if="!authorized">
                    <button class="login-button" v-on:click="login">Login</button>
                </div>

                <!-- With token -->
                <div class="auth" v-if="authorized">
                    <span class="greetings" v-if="username != null">Welcome, {{username}}</span>
                    <button class="logout-button" v-on:click="logout">Logout</button>
                </div>
            </div>
            <hr>
        </div>
    `
}