SUMMARY = "Phosphor OpenBMC Quanta GBS System Initialization Service"
DESCRIPTION = "Phosphor OpenBMC Quanta GBS System Init"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit systemd

DEPENDS += "systemd"
RDEPENDS_${PN} += "bash"
RDEPENDS_${PN} += "libsystemd"
RDEPENDS_${PN} += "jq"

SRC_URI = "file://gbs-sysinit.sh \
           file://gbs-gpio-common.sh \
           file://gbs-sysinit.service \
          "

do_install () {
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/gbs-sysinit.sh ${D}${bindir}/

    install -d ${D}${libexecdir}
    install -m 0755 ${WORKDIR}/gbs-gpio-common.sh ${D}${libexecdir}/

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/gbs-sysinit.service ${D}${systemd_system_unitdir}
}

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "gbs-sysinit.service"
