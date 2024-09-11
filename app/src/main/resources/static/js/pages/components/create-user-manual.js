import AppClient from "/ui/js/utils/app-client.js";

export var createUserManual = {
    data() {
        return {
            emptyInputAlert: false,
            responseErrorAlert: false,
            successAlert: false,
            username: "",
            password: "",
            email: "",
            successResponse: {},
            errorResponse: {}
        }
    },
    methods: {
        createUser: function() {
            this.emptyInputAlert = false;
            this.responseErrorAlert = false;
            this.successAlert = false;
            if (this.username == "" || this.password == "" || this.email == "") {
                this.emptyInputAlert = true;
                return;
            }
            AppClient.createTestUser(
                this.username,
                this.password,
                this.email + "@example.com",
                data => {
                    this.username = "";
                    this.password = "";
                    this.email = "";
                    this.successResponse = data;
                    this.successAlert = true;
                },
                data => {
                    this.username = "";
                    this.password = "";
                    this.email = "";
                    this.errorResponse = data;
                    this.responseErrorAlert = true;
                }
            );
        },
        closeEmptyInputAlert: function() {
            this.emptyInputAlert = false;
        },
        closeResponseErrorAlert: function() {
            this.responseErrorAlert = false;
        },
        closeSuccessAlert: function() {
            this.successAlert = false;
        }
    },
    template: `
        <div class="create-user-manual">
            <div class="row mb-3">
                <span class="fw-bold">Create user</span>
            </div>
            <div class="mb-3">
                <div v-if="emptyInputAlert" class="alert alert-danger alert-dismissible show col-sm-12">
                    <b>Enter username, password and email!</b>
                    <button v-on:click="closeEmptyInputAlert" class="btn-close"></button>
                </div>
                <div v-if="responseErrorAlert" class="alert alert-danger alert-dismissible show col-sm-12">
                    <b>User creation error!</b><br>
                    {{errorResponse.code}}
                    <button v-on:click="closeResponseErrorAlert" class="btn-close"></button>
                </div>
                <div v-if="successAlert" class="alert alert-success alert-dismissible show col-sm-12">
                    <b>The user has been created successfully!</b><br>
                    <br>
                    <b>Id:</b> <copy-text>{{successResponse.id}}</copy-text> <br>
                    <b>Username:</b> <copy-text>{{successResponse.username}}</copy-text> <br>
                    <b>Email:</b> <copy-text>{{successResponse.email}}</copy-text>
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
                <label class="col-sm-3 col-form-label">Email:</label>
                <div class="col-sm-9">
                    <div class="input-group">
                        <input v-model="email" type="text" class="form-control" placeholder="email">
                        <span class="input-group-text">@example.com</span>
                    </div>
                </div>
            </div>
            <div class="row mb-3">
                <div class="col-sm-3">
                </div>
                <div class="col-sm-9">
                    <button v-on:click="createUser" class="btn btn-primary col-sm-12">Create</button>
                </div>
            </div>
        </div>
    `
}
