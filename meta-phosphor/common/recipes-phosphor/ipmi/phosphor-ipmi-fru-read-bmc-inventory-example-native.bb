SUMMARY = "Sample BMC accessibe FRU inventory map for phosphor-ipmi-host"
PR = "r1"

inherit native
inherit phosphor-ipmi-host

require phosphor-ipmi-host.inc

DEPENDS += "virtual/phosphor-ipmi-fru-read-bmc-config"
PROVIDES += "virtual/phosphor-ipmi-fru-read-bmc-inventory"

S = "${WORKDIR}/git"
do_install() {
    DEST=${D}${config_datadir}
    install -d ${DEST}
    install scripts/fru-read-bmc-example.yaml  ${DEST}/bmc-fru-config.yaml
}
