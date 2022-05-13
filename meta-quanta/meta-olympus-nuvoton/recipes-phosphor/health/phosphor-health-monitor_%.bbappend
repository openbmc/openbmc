FILESEXTRAPATHS:prepend:olympus-nuvoton := "${THISDIR}/${PN}:"

SRC_URI:append:olympus-nuvoton = " file://bmc_health_config.json"

do_install:append:olympus-nuvoton() {
    install -m 0644 -D ${WORKDIR}/bmc_health_config.json \
        ${D}${sysconfdir}/healthMon/bmc_health_config.json
}
