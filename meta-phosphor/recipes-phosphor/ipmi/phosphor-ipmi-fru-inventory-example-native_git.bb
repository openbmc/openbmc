SUMMARY = "Sample inventory map for phosphor-ipmi-fru"
PROVIDES += "virtual/phosphor-ipmi-fru-inventory"
PV = "1.0+git${SRCPV}"
PR = "r1"

S = "${WORKDIR}/git"

inherit phosphor-ipmi-fru
inherit native

do_install() {
        # TODO: install this to inventory_datadir
        # after ipmi-fru-parser untangles the host
        # firmware config from the machine inventory.
        DEST=${D}${config_datadir}
        install -d ${DEST}
        install scripts/example.yaml ${DEST}/config.yaml
}

require phosphor-ipmi-fru.inc
