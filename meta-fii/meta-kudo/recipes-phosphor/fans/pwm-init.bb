SUMMARY = "Initialize PWM sensors"
DESCRIPTION = "Initialize PWM sensors"
LICENSE = "CLOSED"
PR = "r1"

inherit systemd

DEPENDS = "systemd"
RDEPENDS_${PN} = "bash"

S = "${WORKDIR}"
FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
SRC_URI += " \
    file://pwm_init.service \
    file://bin/pwm_init.sh \
    "

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_AUTO_ENABLE = "enable"
SYSTEMD_SERVICE_${PN} += "pwm_init.service"

FILES_${PN} += "${bindir}/* ${systemd_system_unitdir}/*"

do_install_append() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/bin/* ${D}${bindir}/
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${S}/*.service ${D}${systemd_system_unitdir}
}
