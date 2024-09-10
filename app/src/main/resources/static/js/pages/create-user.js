import AppClient from "/ui/js/utils/app-client.js";

export var createUser = {
    template: `
        <content-container active="create-user">
            <div class="row">
                <div class="col mb-5" style="min-width: 450px; max-width: 450px"> <!--TODO-->
                    <create-user-manual></create-user-manual>
                </div>
                <div class="col me-5">
                </div>
                <div class="col mb-5" style="min-width: 450px; max-width: 450px"> <!--TODO-->
                    <create-user-random></create-user-random>
                </div>
            </div>
        </content-container>
    `
}
