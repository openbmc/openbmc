SUMMARY = "Phosphor OpenBMC Kudo System Power Control Service"
DESCRIPTION = "Phosphor OpenBMC Kudo System Power Control Daemon"

PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit systemd
inherit obmc-phosphor-systemd

DEPENDS += "systemd"
RDEPENDS_${PN} += "libsystemd"
RDEPENDS_${PN} += "bash"

SRC_URI += " \
    file://init_once.sh \
    file://host-powerctrl.service \
    "

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = " \
    host-powerctrl.service \
    "

do_install () {
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/init_once.sh ${D}${bindir}/

    install -d ${D}${systemd_unitdir}/system/
    install -m 0644 ${WORKDIR}/host-powerctrl.service ${D}${systemd_unitdir}/system
}
