SUMMARY = "Shell functions for manipulating IPMI formatted EEPROMs"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
SRC_URI += "file://lib.sh"
S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

DATA = "${datadir}/ipmi-fru"
FILES:${PN} += "${DATA}"

do_install:append() {
  install -d -m0755 ${D}${DATA}
  install -m0644 lib.sh ${D}${DATA}/
}
