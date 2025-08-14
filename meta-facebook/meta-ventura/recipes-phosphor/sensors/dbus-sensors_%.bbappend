FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

inherit obmc-phosphor-systemd systemd

CRITICAL_LEAK_SERVICE = "xyz.openbmc_project.leakdetector.critical.assert@.service"
CRITICAL_DEASSERT_SERVICE = "xyz.openbmc_project.leakdetector.critical.deassert@.service"
WARNING_LEAK_SERVICE = "xyz.openbmc_project.leakdetector.warning.assert@.service"
WARNING_DEASSERT_SERVICE = "xyz.openbmc_project.leakdetector.warning.deassert@.service"

PACKAGECONFIG:append = "\
    cablemonitor \
"

SRC_URI += "file://critical-leak-handler \
            file://deassert-leak-handler \
            file://warning-leak-handler \
            file://${CRITICAL_LEAK_SERVICE} \
            file://${CRITICAL_DEASSERT_SERVICE} \
            file://${WARNING_LEAK_SERVICE} \
            file://${WARNING_DEASSERT_SERVICE} \
            "
SRC_URI += "${@bb.utils.contains('PACKAGECONFIG', 'cablemonitor', ' file://cable-config.json', '', d)}"

RDEPENDS:${PN}:append: = " bash"

FILES:${PN} += "${systemd_system_unitdir}/*"

SYSTEMD_SERVICE:${PN} += "${CRITICAL_LEAK_SERVICE}"
SYSTEMD_SERVICE:${PN} += "${CRITICAL_DEASSERT_SERVICE}"
SYSTEMD_SERVICE:${PN} += "${WARNING_LEAK_SERVICE}"
SYSTEMD_SERVICE:${PN} += "${WARNING_DEASSERT_SERVICE}"

SYSTEMD_AUTO_ENABLE = "enable"

do_install:append:() {
    if ${@bb.utils.contains('PACKAGECONFIG', 'cablemonitor', 'true', 'false', d)}; then
        install -d ${D}/var/lib/cablemonitor
        install -m 0755 ${UNPACKDIR}/cable-config.json ${D}/var/lib/cablemonitor/cable-config.json
    fi

    install -m 0755 ${UNPACKDIR}/critical-leak-handler ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/deassert-leak-handler ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/warning-leak-handler ${D}${libexecdir}/${PN}/
    install -m 0644 ${UNPACKDIR}/${CRITICAL_LEAK_SERVICE} ${D}${systemd_system_unitdir}/${CRITICAL_LEAK_SERVICE}
    install -m 0644 ${UNPACKDIR}/${CRITICAL_DEASSERT_SERVICE} ${D}${systemd_system_unitdir}/${CRITICAL_DEASSERT_SERVICE}
}
