import {sendData, followLink, hash} from "/ui/js/utils/common.js";
import {setToken} from "/ui/js/utils/token.js";

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
            var body = {
                username: this.username,
                password: hash(this.password),
            }
            sendData("/auth/login", "POST", body, true, function(data) {
                setToken(data.accessToken);
                followLink("/ui/index.html");
            });
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