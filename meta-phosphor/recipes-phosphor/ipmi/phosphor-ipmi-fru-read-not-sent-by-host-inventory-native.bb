SUMMARY = "The inventory map of frus not sent by host for phosphor-ipmi-host"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${PHOSPHORBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit native
inherit phosphor-ipmi-host

SRC_URI += "file://fru-config-not-sent-by-host.yaml"

S = "${WORKDIR}"

PROVIDES += "virtual/phosphor-ipmi-fru-read-not-sent-by-host-inventory"

do_install_append() {
    DEST=${D}${config_datadir}
    install -d ${DEST}
    install fru-config-not-sent-by-host.yaml ${DEST}/fru-config-not-sent-by-host.yaml
}

