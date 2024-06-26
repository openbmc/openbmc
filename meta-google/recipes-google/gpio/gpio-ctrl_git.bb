SUMMARY = "GPIO control library for bash scripts"
DESCRIPTION = "GPIO control library for bash scripts."
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"
PR = "r1"

SRC_URI += "file://lib.sh"

RDEPENDS:${PN} = "bash"

do_install() {
    install -d ${D}${datadir}/gpio-ctrl
    install -m 0755 ${UNPACKDIR}/lib.sh ${D}${datadir}/gpio-ctrl/
}
