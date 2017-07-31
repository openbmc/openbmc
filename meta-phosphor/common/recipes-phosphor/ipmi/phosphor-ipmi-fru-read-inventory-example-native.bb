SUMMARY = "Sample inventory map for phosphor-ipmi-host"
PR = "r1"

inherit native
inherit phosphor-ipmi-host

require phosphor-ipmi-host.inc

PROVIDES += "virtual/phosphor-ipmi-fru-read-inventory"

S = "${WORKDIR}/git"

do_install() {
        DEST=${D}${config_datadir}
        install -d ${DEST}
        install scripts/fru-read-example.yaml ${DEST}/config.yaml
}
