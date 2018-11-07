SUMMARY = "Hostboot hostfw inventory map for phosphor-ipmi-fru"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${OPENPOWERBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit native
inherit phosphor-ipmi-fru

SRC_URI += "file://config.yaml"

PROVIDES += "virtual/phosphor-ipmi-fru-hostfw-config"

S = "${WORKDIR}"

do_install() {
        DEST=${D}${hostfw_datadir}

        install -d ${DEST}
        install config.yaml ${DEST}
}
