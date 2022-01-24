SUMMARY = "FRU properties config for ipmi-fru-parser"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit phosphor-ipmi-fru
inherit native

SRC_URI += "file://extra-properties.yaml"

PROVIDES += "virtual/phosphor-ipmi-fru-properties"

S = "${WORKDIR}"

do_install() {
        DEST=${D}${properties_datadir}
        install -d ${DEST}
        install extra-properties.yaml ${DEST}
}
