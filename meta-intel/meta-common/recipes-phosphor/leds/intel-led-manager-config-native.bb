SUMMARY = "Phosphor LED Group Management for Intel"
PR = "r1"

inherit native
inherit obmc-phosphor-utils

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

PROVIDES += "virtual/phosphor-led-manager-config-native"

SRC_URI += "file://led.yaml"
S = "${WORKDIR}"

do_install() {
    SRC=${S}
    DEST=${D}${datadir}/phosphor-led-manager
    install -D ${SRC}/led.yaml ${DEST}/led.yaml
}
