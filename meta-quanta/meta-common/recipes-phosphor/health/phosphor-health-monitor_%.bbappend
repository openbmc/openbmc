FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI:append = " file://bmc_health_config.json \
                 "

do_install:append() {
    install -d ${D}/${sysconfdir}/healthMon/
    install -m 0644 ${WORKDIR}/bmc_health_config.json ${D}/${sysconfdir}/healthMon/
}

