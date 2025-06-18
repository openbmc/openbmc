FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

inherit obmc-phosphor-systemd systemd

CRITICAL_LEAK_SERVICE = "xyz.openbmc_project.leakdetector.critical.assert@.service"
CRITICAL_DEASSERT_SERVICE = "xyz.openbmc_project.leakdetector.critical.deassert@.service"

PACKAGECONFIG:append = "\
    cablemonitor \
"

SRC_URI += "file://critical-leak-handler \
            file://deassert-leak-handler \
            file://${CRITICAL_LEAK_SERVICE} \
            file://${CRITICAL_DEASSERT_SERVICE} \
            "

RDEPENDS:${PN}:append: = " bash"

FILES:${PN} += "${systemd_system_unitdir}/*"

SYSTEMD_SERVICE:${PN} += "${CRITICAL_LEAK_SERVICE}"
SYSTEMD_SERVICE:${PN} += "${CRITICAL_DEASSERT_SERVICE}"

SYSTEMD_AUTO_ENABLE = "enable"

do_install:append:() {
    install -m 0755 ${UNPACKDIR}/critical-leak-handler ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/deassert-leak-handler ${D}${libexecdir}/${PN}/
    install -m 0644 ${UNPACKDIR}/${CRITICAL_LEAK_SERVICE} ${D}${systemd_system_unitdir}/${CRITICAL_LEAK_SERVICE}
    install -m 0644 ${UNPACKDIR}/${CRITICAL_DEASSERT_SERVICE} ${D}${systemd_system_unitdir}/${CRITICAL_DEASSERT_SERVICE}
}
