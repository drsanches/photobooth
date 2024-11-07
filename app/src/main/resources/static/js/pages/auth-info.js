import AppClient from "/ui/js/utils/app-client.js";

export var authInfo = {
    data() {
        return {
            emptyInputAlert: false,
            authErrorAlert: false,
            successAlert: false,
            username: "",
            password: "",
            token: {}
        }
    },
    methods: {
        getToken: function() {
            this.emptyInputAlert = false;
            this.authErrorAlert = false;
            this.successAlert = false;
            if (this.username == "" || this.password == "") {
                this.emptyInputAlert = true;
                return;
            }
            AppClient.login(
                this.username,
                this.password,
                data => {
                    this.successAlert = true;
                    this.token = data;
                },
                () => {
                    this.authErrorAlert = true;
                    this.username = "";
                    this.password = "";
                }
            );
        },
        closeEmptyInputAlert: function() {
            this.emptyInputAlert = false;
        },
        closeAuthErrorAlert: function() {
            this.authErrorAlert = false;
        },
        closeSuccessAlert: function() {
            this.successAlert = false;
        }
    },
    template: `
        <content-container active="auth-info">
            <div class="row mb-3">
                <span class="fw-bold">Get auth token</span>
            </div>
            <div class="mb-3">
                <div v-if="emptyInputAlert" class="alert alert-danger alert-dismissible show col-sm-12">
                    <b>Enter username and password!</b>
                    <button v-on:click="closeEmptyInputAlert" class="btn-close"></button>
                </div>
                <div v-if="authErrorAlert" class="alert alert-danger alert-dismissible show col-sm-12">
                    <b>Authentication error!</b>
                    <button v-on:click="closeAuthErrorAlert" class="btn-close"></button>
                </div>
                <div v-if="successAlert" class="alert alert-success alert-dismissible show col-sm-12">
                    <b>Access token:</b> <copy-text>{{token.accessToken}}</copy-text> <br>
                    <b>Refresh token:</b> <copy-text>{{token.refreshToken}}</copy-text> <br>
                    <button v-on:click="closeSuccessAlert" class="btn-close"></button>
                </div>
            </div>
            <div class="row mb-3">
                <label class="col-sm-3 col-form-label">Username:</label>
                <div class="col-sm-9">
                    <input v-model="username" type="text" class="form-control" placeholder="username">
                </div>
            </div>
            <div class="row mb-3">
                <label class="col-sm-3 col-form-label">Password:</label>
                <div class="col-sm-9">
                    <input v-model="password" type="password" class="form-control" placeholder="password">
                </div>
            </div>
            <div class="row mb-3">
                <div class="col-sm-3">
                </div>
                <div class="col-sm-9">
                    <button v-on:click="getToken" class="btn btn-primary col-sm-12">Get token</button>
                </div>
            </div>
        </content-container>
    `
}
