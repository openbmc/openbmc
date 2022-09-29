SUMMARY = "BMC accesible FRU inventory map for phosphor-ipmi-host"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"
PROVIDES += "virtual/phosphor-ipmi-fru-read-bmc-inventory"
PR = "r1"

SRC_URI += "file://bmc-fru-config.yaml"

S = "${WORKDIR}"

inherit phosphor-ipmi-host
inherit native

do_install:append() {
    DEST=${D}${config_datadir}
    install -d ${DEST}
    install bmc-fru-config.yaml ${DEST}/bmc-fru-config.yaml
}
