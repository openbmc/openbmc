SUMMARY = "Initialize PWM sensors"
DESCRIPTION = "Initialize PWM sensors"
LICENSE = "CLOSED"
PR = "r1"

inherit systemd

DEPENDS = "systemd"
RDEPENDS:${PN} = "bash"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
SRC_URI += " \
    file://pwm_init.service \
    file://bin/pwm_init.sh \
    "

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_AUTO_ENABLE = "enable"
SYSTEMD_SERVICE:${PN} += "pwm_init.service"

FILES:${PN} += "${bindir}/* ${systemd_system_unitdir}/*"

do_install:append() {
    install -d ${D}${libexecdir}/${PN}
    install -m 0755 ${S}/bin/* ${D}${libexecdir}/${PN}/
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${S}/*.service ${D}${systemd_system_unitdir}
}
