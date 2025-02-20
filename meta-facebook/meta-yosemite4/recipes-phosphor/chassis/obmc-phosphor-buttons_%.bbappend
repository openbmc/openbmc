FILESEXTRAPATHS:append := "${THISDIR}/${PN}:"

EXTRA_OEMESON:append = "-Dhost-instances='${OBMC_HOST_INSTANCES}' \
"

SRC_URI += "file://gpio_defs.json"

do_install:append() {
        install -d ${D}${sysconfdir}/default/obmc/gpio/
        install -m 0644 ${UNPACKDIR}/gpio_defs.json ${D}/${sysconfdir}/default/obmc/gpio/
}

FILES:${PN}-signals:append = " ${sysconfdir}/default/obmc/gpio/gpio_defs.json"
