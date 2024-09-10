import AppClient from "/ui/js/utils/app-client.js";

export var createUser = {
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
                this.email,
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
        <content-container active="create-user">
            <div class="create-user">
                <div class="mb-3">
                    <div v-if="emptyInputAlert" class="alert alert-danger alert-dismissible show col-sm-12">
                        <b>Enter username and password!</b>
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
                        <b>Id:</b> {{successResponse.id}} <br>
                        <b>Username:</b> {{successResponse.username}} <br>
                        <b>Email:</b> {{successResponse.email}}
                        <button v-on:click="closeSuccessAlert" class="btn-close"></button>
                    </div>
                </div>
                <div class="mb-3 row">
                    <span class="fw-bold">Enter new test user data</span>
                </div>
                <div class="mb-3 row">
                    <label class="col-sm-3 col-form-label">Username:</label>
                    <div class="col-sm-9">
                        <input v-model="username" type="text" class="form-control" placeholder="username">
                    </div>
                </div>
                <div class="mb-3 row">
                    <label class="col-sm-3 col-form-label">Password:</label>
                    <div class="col-sm-9">
                        <input v-model="password" type="password" class="form-control">
                    </div>
                </div>
                <div class="mb-3 row">
                    <label class="col-sm-3 col-form-label">Email:</label>
                    <div class="col-sm-9">
                        <input v-model="email" type="text" class="form-control">
                    </div>
                </div>
                <div class="mb-3 row">
                    <div class="col-sm-3">
                    </div>
                    <div class="col-sm-9">
                        <button v-on:click="createUser" class="btn btn-primary col-sm-12">Create</button>
                    </div>
                </div>
            </div>
        </content-container>
    `
}
