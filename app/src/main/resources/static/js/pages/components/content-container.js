export var contentContainer = {
    props: {
        active: String
    },
    methods: {
        calcActive: function(x) {
            return this.active == x ? "nav-link active" : "nav-link";
        },
        goToInfo: function () {
            this.$router.push('/');
        },
        goToAuthInfo: function () {
            this.$router.push('/auth-info');
        },
        goToCreateUser: function () {
            this.$router.push('/create-user');
        }
    },
    computed: {
        authorized() {
            return this.$store.state.authInfo != null
        },
        info() {
            return this.calcActive("info");
        },
        authInfo() {
            return this.calcActive("auth-info");
        },
        createUser() {
            return this.calcActive("create-user");
        }
    },
    template: `
        <div class="content-container" v-if="authorized">
            <div class="d-flex align-items-start">
              <div class="nav flex-column nav-pills me-5">
                <button v-bind:class="info" v-on:click="goToInfo">Info</button>
                <button v-bind:class="authInfo" v-on:click="goToAuthInfo">Auth info</button>
                <button v-bind:class="createUser" v-on:click="goToCreateUser">Create user</button>
              </div>
              <div>
                <slot></slot>
              </div>
            </div>
        </div>
    `
}
