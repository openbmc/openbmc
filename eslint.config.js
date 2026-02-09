const { defineConfig } = require("eslint/config");
const jsonPlugin = require("@eslint/json").default;

module.exports = defineConfig([
    {
        files: ["**/*.json"],
        plugins: { json: jsonPlugin },
        // OpenBMC allows comments in JSON; treat as JSON5.
        language: "json/json5",
        extends: ["json/recommended"],
    },
    {
        ignores: ["**/meson-*/*.json", "subprojects/**/*.json"],
    },
]);
