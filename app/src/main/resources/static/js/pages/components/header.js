import {redirect} from "/ui/js/utils/utils.js";
import AppClient from "/ui/js/utils/app-client.js";
import Token from "/ui/js/utils/token.js";

export var header = {
    methods: {
        home: () => redirect("/ui/index.html"),
        logout: function () {
            AppClient.logout(() => {
                Token.remove();
                this.$store.commit('update', null);
            });
        },
        login: function() {
            this.$router.push('/login');
        }
    },
    computed: {
        username() {
            return this.$store.state.authInfo == null ? null : this.$store.state.authInfo.username;
        },
        authorized() {
            return this.$store.state.authInfo != null;
        }
    },
    template: `
        <div class="header">
            <div class="d-flex ms-5 me-5">
                <div class="mt-3 mb-3 me-auto">
                    <span v-on:click="home" class="h1" role="button">PhotoBooth admin</span>
                </div>
                <div class="mt-3 mb-3">

                    <!-- Unauthorized -->
                    <div v-if="!authorized">
                        <button v-on:click="login" type="button" class="btn btn-primary">Login</button>
                    </div>

                    <!-- Authorized -->
                    <div v-if="authorized">
                        <span v-if="username != null" class="align-middle fs-5 me-3">Welcome, {{username}}</span>
                        <button v-on:click="logout" type="button" class="btn btn-secondary">Logout</button>
                    </div>
                </div>
            </div>
            <hr class="border border-primary border-3 m-0 opacity-75">
        </div>
    `
}
