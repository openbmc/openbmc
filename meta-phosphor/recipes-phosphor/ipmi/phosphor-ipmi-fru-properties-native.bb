SUMMARY = "FRU properties config for ipmi-fru-parser"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"
PROVIDES += "virtual/phosphor-ipmi-fru-properties"
PR = "r1"

SRC_URI += "file://extra-properties.yaml"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

inherit phosphor-ipmi-fru
inherit native

do_install() {
        # This recipe is supposed to create an output yaml file with
        # FRU property values extracted from the MRW. This example recipe
        # provides a sample output file.
        DEST=${D}${properties_datadir}
        install -d ${DEST}
        install extra-properties.yaml ${DEST}
}
