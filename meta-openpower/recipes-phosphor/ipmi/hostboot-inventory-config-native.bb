SUMMARY = "Hostboot hostfw inventory map for phosphor-ipmi-fru"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit phosphor-ipmi-fru
inherit native

SRC_URI += "file://config.yaml"

PROVIDES += "virtual/phosphor-ipmi-fru-hostfw-config"

S = "${WORKDIR}"

do_install() {
        DEST=${D}${hostfw_datadir}

        install -d ${DEST}
        install config.yaml ${DEST}
}
