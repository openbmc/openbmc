SUMMARY = "FRU properties config for ipmi-fru-parser"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${PHOSPHORBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit native
inherit phosphor-ipmi-fru

SRC_URI += "file://extra-properties.yaml"

PROVIDES += "virtual/phosphor-ipmi-fru-properties"

S = "${WORKDIR}"

do_install() {
        DEST=${D}${properties_datadir}
        install -d ${DEST}
        install extra-properties.yaml ${DEST}
}
