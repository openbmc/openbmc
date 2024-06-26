SUMMARY = "FRU properties config for openpower-vpd-parser"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit openpower-fru-vpd
inherit native

SRC_URI += "file://properties.yaml"

PROVIDES += "virtual/openpower-fru-properties"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

do_install() {
        DEST=${D}${properties_datadir}
        install -d ${DEST}
        install properties.yaml ${DEST}/out.yaml
}
