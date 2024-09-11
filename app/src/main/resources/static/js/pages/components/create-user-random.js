import AppClient from "/ui/js/utils/app-client.js";

export var createUserRandom = {
    data() {
        return {
            responseErrorAlert: false,
            successAlert: false,
            password: "password",
            successResponse: {},
            errorResponse: {}
        }
    },
    methods: {
        createUser: function() {
            this.responseErrorAlert = false;
            this.successAlert = false;
            AppClient.createTestUser(
                crypto.randomUUID().substring(0, 18),
                this.password,
                crypto.randomUUID().substring(0, 23) + "@example.com",
                data => {
                    this.successResponse = data;
                    this.successAlert = true;
                },
                data => {
                    this.errorResponse = data;
                    this.responseErrorAlert = true;
                }
            );
        },
        closeResponseErrorAlert: function() {
            this.responseErrorAlert = false;
        },
        closeSuccessAlert: function() {
            this.successAlert = false;
        }
    },
    template: `
        <div class="create-user-random">
            <div class="row mb-3">
                <span class="fw-bold">Create random user</span>
            </div>
            <div class="row">
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
                    <b>Password:</b> <copy-text>{{password}}</copy-text> <br>
                    <b>Email:</b> <copy-text>{{successResponse.email}}</copy-text>
                    <button v-on:click="closeSuccessAlert" class="btn-close"></button>
                </div>
            </div>
            <div class="mb-3">
                <button v-on:click="createUser" class="btn btn-primary col-sm-12">Create random</button>
            </div>
        </div>
    `
}
