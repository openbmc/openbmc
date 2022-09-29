SUMMARY = "Phosphor OpenBMC Mori System Power Control Service"
DESCRIPTION = "Phosphor OpenBMC Mori System Power Control Daemon"

PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit systemd
inherit obmc-phosphor-systemd

DEPENDS += "systemd"
RDEPENDS:${PN} += "libsystemd"
RDEPENDS:${PN} += "bash"

SRC_URI = " \
    file://init_once.sh \
    file://host-powerctrl.service \
    "

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = " \
    host-powerctrl.service \
    "

do_install () {
    install -d ${D}${libexecdir}/${PN}
    install -m 0755 ${WORKDIR}/init_once.sh ${D}${libexecdir}/${PN}
    install -d ${D}${systemd_unitdir}/system/
    install -m 0644 ${WORKDIR}/host-powerctrl.service ${D}${systemd_unitdir}/system
}
