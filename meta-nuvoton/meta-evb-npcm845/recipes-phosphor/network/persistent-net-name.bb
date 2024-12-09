SUMMARY = "udev rules for persistent net name"
DESCRIPTION = "udev rules for persistent net name"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"
SRC_URI += "file://85-persistent-net.rules"

RDEPENDS:${PN} += "udev"

do_install() {
    install -d ${D}/${base_libdir}/udev/rules.d
    install -m 0644 ${UNPACKDIR}/85-persistent-net.rules ${D}/${base_libdir}/udev/rules.d
}
