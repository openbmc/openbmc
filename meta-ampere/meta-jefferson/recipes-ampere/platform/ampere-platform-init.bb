SUMMARY = "Phosphor OpenBMC Platform Init Service"
DESCRIPTION = "Phosphor OpenBMC Platform Init Daemon"

PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit systemd
inherit obmc-phosphor-systemd

DEPENDS += "systemd"
RDEPENDS:${PN} += "libsystemd"
RDEPENDS:${PN} += "bash"

SRC_URI = " \
           file://ampere-platform-init.service \
           file://ampere_platform_init.sh \
          "
SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "ampere-platform-init.service"

do_install () {
    install -d ${D}${sbindir}
    install -m 0755 ${WORKDIR}/ampere_platform_init.sh ${D}${sbindir}/
    install -d ${D}${systemd_unitdir}/system/
    install -m 0644 ${WORKDIR}/ampere-platform-init.service ${D}${systemd_unitdir}/system
}

