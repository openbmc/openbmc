SUMMARY = "Initialize PWM sensors"
DESCRIPTION = "Initialize PWM sensors"
LICENSE = "CLOSED"
PR = "r1"

inherit systemd

DEPENDS = "systemd"
RDEPENDS:${PN} = "bash"

S = "${UNPACKDIR}/sources"
FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"
SRC_URI += " \
    file://pwm_init.service \
    file://bin/pwm_init.sh \
    "

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_AUTO_ENABLE = "enable"
SYSTEMD_SERVICE:${PN} += "pwm_init.service"

FILES:${PN} += "${bindir}/* ${systemd_system_unitdir}/*"

do_install:append() {
    install -d ${D}${libexecdir}/${BPN}
    install -m 0755 ${S}/bin/* ${D}${libexecdir}/${BPN}/
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${S}/*.service ${D}${systemd_system_unitdir}
}
