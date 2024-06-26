SUMMARY = "udev rules for Aspeed VUART"
DESCRIPTION = "udev rules for Aspeed VUART"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"
SRC_URI += "file://61-aspeed-vuart.rules"

RDEPENDS:${PN} += "udev"

do_install() {
    install -d ${D}/${nonarch_base_libdir}/udev/rules.d
    install -m 0644 ${UNPACKDIR}/61-aspeed-vuart.rules ${D}/${nonarch_base_libdir}/udev/rules.d
}
