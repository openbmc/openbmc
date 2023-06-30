SUMMARY = "Initialize PWM sensors"
DESCRIPTION = "Initialize PWM sensors"
LICENSE = "CLOSED"
DEPENDS:append = " systemd"
PR = "r1"

SRC_URI = " \
    file://pwm_init.service \
    file://bin/pwm_init.sh \
"

S = "${WORKDIR}"
SYSTEMD_AUTO_ENABLE = "enable"
SYSTEMD_SERVICE:${PN} = " pwm_init.service"

inherit systemd

do_install() {
    install -d ${D}${libexecdir}/${PN}
    install -m 0755 ${S}/bin/* ${D}${libexecdir}/${PN}/
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${S}/*.service ${D}${systemd_system_unitdir}
}

RDEPENDS:${PN}:append = " bash"

FILES:${PN}:append = " ${bindir}/* ${systemd_system_unitdir}/*"
