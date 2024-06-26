SUMMARY = "Phosphor OpenBMC Mori System Command"
DESCRIPTION = "Phosphor OpenBMC Mori System Command Daemon"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"
PR = "r1"

SRC_URI = "file://mori.sh"

do_install () {
    install -d ${D}${bindir}
    install -m 0755 ${UNPACKDIR}/mori.sh ${D}${bindir}/mori.sh
}

RDEPENDS:${PN}:append = " bash"
