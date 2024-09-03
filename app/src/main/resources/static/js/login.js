import AppClient from "/ui/js/utils/app-client.js";
import Token from "/ui/js/utils/token.js";

export var login = {
    data() {
        return {
            emptyInputAlert: false,
            authErrorAlert: false,
            username: "",
            password: ""
        }
    },
    methods: {
        login: function() {
            this.emptyInputAlert = false;
            this.authErrorAlert = false;
            if (this.username == "" || this.password == "") {
                this.emptyInputAlert = true;
                return;
            }
            AppClient.login(
                this.username,
                this.password,
                data => {
                    Token.set(data.accessToken);
                    AppClient.getInfo(
                        data => {
                            this.$store.commit('update', data);
                            this.$router.push('/');
                        },
                        data => {
                            this.authError();
                            this.$router.push('/login');
                        }
                    );
                },
                () => this.authError()
            );
        },
        authError: function() {
            this.authErrorAlert = true;
            this.username = "";
            this.password = "";
        },
        computed: {
            emptyInputAlert() {
                return this.emptyInputAlert;
            },
            authErrorAlert() {
                return this.authErrorAlert;
            }
        }
    },
    //TODO: Show validation (https://bootstrap-vue.org/docs/components/form-input)
    template: `
        <div class="login">
            <div class="d-grid col-6 mx-auto">
                <div class="border border-primary border-2 rounded">
                    <div class="m-3">
                        <label for="inputUsername" class="form-label">Username</label>
                        <input v-model="username" type="text" class="form-control" id="inputUsername" placeholder="username">
                    </div>
                    <div class="m-3">
                        <label for="inputPassword" class="form-label">Password</label>
                        <input v-model="password" type="password" class="form-control" id="inputPassword">
                    </div>
                    <div v-if="emptyInputAlert" class="alert alert-danger d-grid col-10 mx-auto m-3" role="alert">
                        Enter username and password!
                    </div>
                    <div v-if="authErrorAlert" class="alert alert-danger d-grid col-10 mx-auto m-3" role="alert">
                        Authentication error!
                    </div>
                    <div class="d-grid col-6 mx-auto m-3">
                        <button v-on:click="login" type="button" class="btn btn-primary">Login</button>
                    </div>
                </div>
            </div>
        </div>
    `
}
