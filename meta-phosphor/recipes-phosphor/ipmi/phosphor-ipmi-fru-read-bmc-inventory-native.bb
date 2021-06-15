SUMMARY = "BMC accesible FRU inventory map for phosphor-ipmi-host"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit phosphor-ipmi-host
inherit native

SRC_URI += "file://bmc-fru-config.yaml"

S = "${WORKDIR}"

PROVIDES += "virtual/phosphor-ipmi-fru-read-bmc-inventory"

do_install_append() {
    DEST=${D}${config_datadir}
    install -d ${DEST}
    install bmc-fru-config.yaml ${DEST}/bmc-fru-config.yaml
}

