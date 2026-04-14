FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

FACEBOOK_REMOVED_DBUS_SENSORS:remove = " \
    external \
"

PACKAGECONFIG:append = " \
    nvmesensor \
"

inherit obmc-phosphor-systemd systemd

CRITICAL_LEAK_SERVICE = "xyz.openbmc_project.leakdetector.critical.assert@.service"

WARNING_LEAK_SERVICE = "xyz.openbmc_project.leakdetector.warning.assert@.service"

SRC_URI:append = " \
    file://critical-leak-handler \
    file://warning-leak-handler \
    file://${CRITICAL_LEAK_SERVICE} \
    file://${WARNING_LEAK_SERVICE} \
    "

RDEPENDS:${PN}:append = " bash"

FILES:${PN} += "${systemd_system_unitdir}/*"

SYSTEMD_SERVICE:${PN} += " \
    ${CRITICAL_LEAK_SERVICE} \
    ${WARNING_LEAK_SERVICE} \
    "

do_install:append() {

    install -d ${D}${libexecdir}/${PN}/
    install -m 0755 ${UNPACKDIR}/critical-leak-handler \
                    ${D}${libexecdir}/${PN}/critical-leak-handler
    install -m 0755 ${UNPACKDIR}/warning-leak-handler \
                    ${D}${libexecdir}/${PN}/warning-leak-handler

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/${CRITICAL_LEAK_SERVICE} \
                    ${D}${systemd_system_unitdir}/${CRITICAL_LEAK_SERVICE}
    install -m 0644 ${UNPACKDIR}/${WARNING_LEAK_SERVICE} \
                    ${D}${systemd_system_unitdir}/${WARNING_LEAK_SERVICE}
}