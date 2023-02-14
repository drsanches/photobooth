import API from "/ui/js/utils/api.js";
import {followLink} from "/ui/js/utils/utils.js";

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
            logout: () => API.logout(() => followLink("/ui/index.html"))
        },
    mounted() {
        API.getInfo(data => {
            this.username = data.username;
            this.authorized = true;
        });
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
