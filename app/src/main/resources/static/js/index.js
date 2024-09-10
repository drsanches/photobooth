import {app} from "/ui/js/app.js";
import {header} from '/ui/js/pages/components/header.js';
import {login} from "/ui/js/pages/login.js";
import {contentContainer} from '/ui/js/pages/components/content-container.js';
import {info} from '/ui/js/pages/info.js';
import {createUser} from '/ui/js/pages/create-user.js';
import {createUserManual} from '/ui/js/pages/components/create-user-manual.js';
import {createUserRandom} from '/ui/js/pages/components/create-user-random.js';
import AppClient from "/ui/js/utils/app-client.js";
import Token from "/ui/js/utils/token.js";

const routes = [
    {path: '/', redirect: "/info"},
    {path: '/info', component: info},
    {path: '/create-user', component: createUser},
    {path: '/login', component: login}
];

const router = VueRouter.createRouter({
    history: VueRouter.createWebHashHistory(),
    routes,
});

const store = Vuex.createStore({
    state() {
        return {
            authInfo: null
        }
    },
    mutations: {
        update(state, data) {
            state.authInfo = data;
        }
    }
})

AppClient.getInfo(
    data => {
        store.commit('update', data);
    },
    data => {
        Token.remove();
        store.commit('update', null);
    }
);

var vueApp = Vue.createApp({});
vueApp.use(store);
vueApp.use(router);
vueApp.component('app', app);
vueApp.component('custom-header', header);
vueApp.component('login', login);
vueApp.component('content-container', contentContainer);
vueApp.component('info', info);
vueApp.component('create-user', createUser);
vueApp.component('create-user-manual', createUserManual);
vueApp.component('create-user-random', createUserRandom);
vueApp.mount("#app");
