SUMMARY = "Phosphor OpenBMC Mt.Jade Platform Init Service"
DESCRIPTION = "Phosphor OpenBMC Mt.Jade Platform Init Daemon"

PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

inherit systemd
inherit obmc-phosphor-systemd

DEPENDS += "systemd"
RDEPENDS:${PN} += "libsystemd"
RDEPENDS:${PN} += "bash"

SRC_URI = " \
    file://ampere_platform_init.sh \
    file://mtjade_platform_gpios_init.sh \
    file://ampere-platform-init.service \
    "

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "ampere-platform-init.service"

do_install () {
    install -d ${D}${sbindir}
    install -m 0755 ${UNPACKDIR}/ampere_platform_init.sh ${D}${sbindir}/
    install -d ${D}${systemd_unitdir}/system/
    install -m 0644 ${UNPACKDIR}/ampere-platform-init.service ${D}${systemd_unitdir}/system
    install -m 0755 ${UNPACKDIR}/mtjade_platform_gpios_init.sh ${D}${sbindir}/platform_gpios_init.sh
}
