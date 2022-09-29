SUMMARY = "Sample inventory map for phosphor-ipmi-host"
PROVIDES += "virtual/phosphor-ipmi-fru-read-inventory"
PV = "1.0+git${SRCPV}"
PR = "r1"

S = "${WORKDIR}/git"

inherit phosphor-ipmi-host
inherit native

do_install() {
        DEST=${D}${config_datadir}
        install -d ${DEST}
        install scripts/fru-read-example.yaml ${DEST}/config.yaml
}

require phosphor-ipmi-host.inc
