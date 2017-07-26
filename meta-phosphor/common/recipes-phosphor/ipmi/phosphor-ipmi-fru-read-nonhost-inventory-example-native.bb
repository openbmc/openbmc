SUMMARY = "Sample non host inventory map for phosphor-ipmi-host"
PR = "r1"

inherit native
inherit phosphor-ipmi-host

require phosphor-ipmi-host.inc

DEPENDS += "virtual/phosphor-ipmi-fru-read-nonhost-config"
PROVIDES += "virtual/phosphor-ipmi-fru-read-nonhost-inventory"

S = "${WORKDIR}/git"
do_install() {
    DEST=${D}${config_datadir}
    install -d ${DEST}
    install scripts/fru-read-nonhost-example.yaml  ${DEST}/nonhost-config.yaml
}
