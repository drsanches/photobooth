import {header} from '/ui/js/header.js';
import {content} from '/ui/js/content.js';
import {login} from "/ui/js/login.js";
import {app} from "/ui/js/app.js";
import AppClient from "/ui/js/utils/app-client.js";
import Token from "/ui/js/utils/token.js";

const routes = [
    {path: '/', component: content},
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
        router.push('/'); //TODO
    },
    data => {
        Token.remove();
        router.push('/login');
    }
);

var vueApp = Vue.createApp({});
vueApp.use(store);
vueApp.use(router);
vueApp.component('custom-header', header);
vueApp.component('content', content);
vueApp.component('login', login);
vueApp.component('app', app);
vueApp.mount("#app");
