SUMMARY = "Init for Nvidia GPU PCIe card"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit systemd
inherit obmc-phosphor-systemd

DEPENDS += "systemd"
RDEPENDS:${PN} += "libsystemd"
RDEPENDS:${PN} += "bash"

SRC_URI = " \
           file://mctp_init.service \
           file://mctp_init.sh \
           file://mctp_setup.service \
           file://mctp_setup.sh \
          "

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} += "mctp_init.service"
SYSTEMD_SERVICE:${PN} += "mctp_setup.service"

do_install () {
    install -d ${D}${sbindir}
    install -m 0755 ${UNPACKDIR}/mctp_init.sh ${D}${sbindir}/
    install -d ${D}${systemd_unitdir}/system/
    install -m 0644 ${UNPACKDIR}/mctp_init.service ${D}${systemd_unitdir}/system

    install -m 0755 ${UNPACKDIR}/mctp_setup.sh ${D}${sbindir}/
    install -m 0644 ${UNPACKDIR}/mctp_setup.service ${D}${systemd_unitdir}/system
}
