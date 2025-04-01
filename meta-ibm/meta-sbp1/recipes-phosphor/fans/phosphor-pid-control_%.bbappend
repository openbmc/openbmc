FILESEXTRAPATHS:prepend:sbp1 := "${THISDIR}/${PN}:"

SRC_URI:append:sbp1 = " file://fan-setup.service"

RDEPENDS:${PN} += "bash"

SYSTEMD_SERVICE:${PN}:append:sbp1 = " fan-setup.service"

PACKAGECONFIG:append:sbp1 = "offline-failsafe"

do_install:append:sbp1() {
    install -d ${D}${systemd_unitdir}/system/
    install -m 0644 ${UNPACKDIR}/fan-setup.service ${D}${systemd_unitdir}/system
}
