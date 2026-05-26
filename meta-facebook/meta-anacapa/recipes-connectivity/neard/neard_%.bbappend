FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += " \
    file://neard.service \
"

inherit systemd

SYSTEMD_SERVICE:${PN} = "neard.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"

RDEPENDS:${PN} += "bash"

do_install:append() {
    install -d ${D}${systemd_system_unitdir}
    install -m 0755 ${UNPACKDIR}/neard.service \
        ${D}${systemd_system_unitdir}/neard.service
}
