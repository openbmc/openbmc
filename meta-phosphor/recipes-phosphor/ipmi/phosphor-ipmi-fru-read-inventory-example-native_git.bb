SUMMARY = "Sample inventory map for phosphor-ipmi-host"
PR = "r1"
PV = "1.0+git${SRCPV}"

inherit phosphor-ipmi-host
inherit native

require phosphor-ipmi-host.inc

PROVIDES += "virtual/phosphor-ipmi-fru-read-inventory"

S = "${WORKDIR}/git"

do_install() {
        DEST=${D}${config_datadir}
        install -d ${DEST}
        install scripts/fru-read-example.yaml ${DEST}/config.yaml
}
