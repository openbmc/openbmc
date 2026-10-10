# e3c246d4i doesn't have the space/budget for gpio-presence
PACKAGECONFIG:remove:e3c246d4i = "gpio-presence"

do_install:append:e3c246d4i() {
    # this device doesn't have enough space for all configs
    find ${D}${datadir}/${BPN}/configurations \
        -mindepth 1 -maxdepth 1 \
        -type d \
        -not -name "asrock" \
        -exec rm -rf {} +
}
