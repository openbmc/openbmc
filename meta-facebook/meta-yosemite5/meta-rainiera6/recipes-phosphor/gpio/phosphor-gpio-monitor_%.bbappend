FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " \
    file://rainier-gpio-handler \
    file://rainier-gpio-handler@.service \
"

do_install:append() {
    install -d ${D}${libexecdir}/phosphor-gpio-monitor
    install -m 0755 ${UNPACKDIR}/rainier-gpio-handler \
        ${D}${libexecdir}/phosphor-gpio-monitor/rainier-gpio-handler

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/rainier-gpio-handler@.service \
        ${D}${systemd_system_unitdir}/rainier-gpio-handler@.service
}

FILES:${PN} += " \
    ${libexecdir}/phosphor-gpio-monitor/rainier-gpio-handler \
    ${systemd_system_unitdir}/rainier-gpio-handler@.service \
"
