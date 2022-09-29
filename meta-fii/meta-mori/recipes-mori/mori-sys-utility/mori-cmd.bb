SUMMARY = "Phosphor OpenBMC Mori System Command"
DESCRIPTION = "Phosphor OpenBMC Mori System Command Daemon"

PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

RDEPENDS:${PN} += "bash"

SRC_URI = " \
    file://mori.sh \
    "

SYSTEMD_PACKAGES = "${PN}"

do_install () {
    install -d ${D}${sbindir}
    install -m 0755 ${WORKDIR}/mori.sh ${D}${sbindir}/mori.sh
}
