SUMMARY = "Shell functions for manipulating network addresses"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
SRC_URI += "file://lib.sh"
SRC_URI += "file://test.sh"
S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

DATA = "${datadir}/network"
FILES:${PN} += "${DATA}"

DEPENDS += "test-sh"

do_compile() {
  SYSROOT="$PKG_CONFIG_SYSROOT_DIR" bash test.sh || exit
}

do_install:append() {
  install -d -m0755 ${D}${DATA}
  install -m0644 lib.sh ${D}${DATA}/
}
