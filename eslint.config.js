const { defineConfig } = require("eslint/config");
const jsonPlugin = require("eslint-plugin-json");

module.exports = defineConfig([
    {
        files: ["**/*.json"],
        plugins: { json: jsonPlugin },
        processor: "json/json",
        rules: {
            "json/*": ["error", "allowComments"],
        },
    },
    {
        ignores: ["**/meson-*/*.json", "subprojects/**/*.json"],
    },
]);
