SUMMARY = "Phosphor OpenBMC Kudo Firmware Upgrade Command"
DESCRIPTION = "Phosphor OpenBMC Kudo Firmware Upgrade Comman Daemon"

PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit systemd
inherit obmc-phosphor-systemd

DEPENDS += "systemd"
DEPENDS += "phosphor-ipmi-flash"
RDEPENDS:${PN} += "libsystemd"
RDEPENDS:${PN} += "bash"


SRC_URI = " \
    file://kudo-fw.sh \
    file://kudo-fw-ver.service \
    file://kudo-fw-ver.sh \
    file://kudo-lib.sh \
    "

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = " \
    kudo-fw-ver.service \
    "

do_install () {
    install -d ${D}/${sbindir}
    install -m 0755 ${WORKDIR}/kudo-fw.sh ${D}/${sbindir}/kudo-fw.sh
    install -m 0755 ${WORKDIR}/kudo-fw-ver.sh ${D}/${sbindir}/kudo-fw-ver.sh
    install -m 0755 ${WORKDIR}/kudo-lib.sh ${D}/${sbindir}/kudo-lib.sh
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/kudo-fw-ver.service ${D}${systemd_system_unitdir}
}
