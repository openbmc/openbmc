FILESEXTRAPATHS:prepend := "${THISDIR}:"
DESCRIPTION = "BMC Reboot Cause"
PR = "r1"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit systemd
inherit obmc-phosphor-systemd

DEPENDS += "systemd"
RDEPENDS:${PN} += "bash"

SRC_URI += " file://bmc-reboot-cause.service \
             file://reboot-cause.sh \
           "

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "bmc-reboot-cause.service"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/reboot-cause.sh ${D}${bindir}/
}
