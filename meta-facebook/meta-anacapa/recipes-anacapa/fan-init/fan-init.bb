SUMMARY = "Fan Init service"
DESCRIPTION = "Settings used to initialize fan controller"

PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit systemd
inherit obmc-phosphor-systemd

RDEPENDS:${PN} += "bash"

S = "${UNPACKDIR}"
SRC_URI:append = " \
    file://fan-init \
    file://fan-init.service \
"


SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "fan-init.service"

do_install () {
    install -d ${D}${libexecdir}/${BPN}
    install -m 0755 ${UNPACKDIR}/fan-init ${D}${libexecdir}/${BPN}
}

