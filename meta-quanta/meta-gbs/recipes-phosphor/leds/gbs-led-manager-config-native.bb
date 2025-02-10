SUMMARY = "Phosphor LED Group Management for GBS"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit native

PROVIDES += "virtual/phosphor-led-manager-config-native"

SRC_URI += "file://led.json"
S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

# Overwrite the example led layout json file prior
# to building the phosphor-led-manager package
do_install() {
    install -m 0644 ${UNPACKDIR}/led.json ${D}${datadir}/phosphor-led-manager/
}
