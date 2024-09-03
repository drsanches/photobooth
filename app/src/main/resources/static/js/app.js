import AppClient from "/ui/js/utils/app-client.js";

export var app = {
    template: `
        <div class="app">
            <custom-header></custom-header>
            <main class="m-5">
                <RouterView />
            </main>
        </div>
    `
}
