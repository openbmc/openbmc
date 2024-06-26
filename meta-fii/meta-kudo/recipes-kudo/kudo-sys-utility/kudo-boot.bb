SUMMARY = "Phosphor OpenBMC Kudo System Power Control Service"
DESCRIPTION = "Phosphor OpenBMC Kudo System Power Control Daemon"

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
    install -m 0755 ${UNPACKDIR}/init_once.sh ${D}${libexecdir}/${PN}/init_once.sh
    install -d ${D}${systemd_unitdir}/system/
    install -m 0644 ${UNPACKDIR}/host-powerctrl.service ${D}${systemd_unitdir}/system
}
