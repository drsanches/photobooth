export var copyText = {
    data() {
        return {
            copied: false
        }
    },
    methods: {
        copy: function() {
            navigator.clipboard.writeText(this.$refs.textToCopy.textContent);
            this.copied = true;
        }
    },
    template: `
        <span v-on:click="copy" role="button">
            <span ref="textToCopy" class="font-monospace me-2">
                <slot></slot>
            </span>
            <i v-if="!copied" class="bi bi-clipboard" title="Copy to clipboard"></i>
            <i v-if="copied" class="bi bi-clipboard-check" title="Copied"></i>
        </span>
    `
}
