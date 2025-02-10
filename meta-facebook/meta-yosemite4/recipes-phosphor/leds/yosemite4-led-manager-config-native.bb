SUMMARY = "Phosphor LED Group Management for Yosemite4"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit native

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

PROVIDES += "virtual/phosphor-led-manager-config-native"

SRC_URI += "file://led.json"

do_install() {
    install -m 0644 ${UNPACKDIR}/led.json ${D}${datadir}/phosphor-led-manager/
}
