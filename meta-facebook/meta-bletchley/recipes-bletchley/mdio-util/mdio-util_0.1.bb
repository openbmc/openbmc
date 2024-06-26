SUMMARY = "MDIO utility for AST2600"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

RDEPENDS:${PN} += "bash"

SRC_URI += " \
        file://mdio-util \
"
S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

do_install() {
        install -d ${D}${sbindir}
        install -m 0755 ${S}/mdio-util ${D}${sbindir}/
}