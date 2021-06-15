SUMMARY = "The inventory map of frus not sent by host for phosphor-ipmi-host"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit phosphor-ipmi-host
inherit native

SRC_URI += "file://fru-config-not-sent-by-host.yaml"

S = "${WORKDIR}"

PROVIDES += "virtual/phosphor-ipmi-fru-read-not-sent-by-host-inventory"

do_install_append() {
    DEST=${D}${config_datadir}
    install -d ${DEST}
    install fru-config-not-sent-by-host.yaml ${DEST}/fru-config-not-sent-by-host.yaml
}

