import API from "/ui/js/utils/api.js";
import {followLink} from "/ui/js/utils/utils.js";

export var login = {
    data() {
        return {
            username: "",
            password: ""
        }
    },
    methods: {
        login: function() {
            if (this.username == "" || this.password == "") {
                alert("Enter all data");
                return;
            }
            API.login(this.username, this.password, () => followLink("/ui/index.html"), () => alert("Login error!"));
        }
    },
    template: `
        <div class="login">
            <span>Username:</span><br>
            <input v-model="username"><br>
            <span>Password:</span><br>
            <input type="password" v-model="password"><br>
            <button v-on:click="login">Login</button>
        </div>
    `
}
