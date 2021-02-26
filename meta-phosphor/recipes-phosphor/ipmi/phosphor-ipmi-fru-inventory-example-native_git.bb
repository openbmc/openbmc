SUMMARY = "Sample inventory map for phosphor-ipmi-fru"
PR = "r1"
PV = "1.0+git${SRCPV}"

inherit phosphor-ipmi-fru
inherit native

require phosphor-ipmi-fru.inc

PROVIDES += "virtual/phosphor-ipmi-fru-inventory"

S = "${WORKDIR}/git"

do_install() {
        # TODO: install this to inventory_datadir
        # after ipmi-fru-parser untangles the host
        # firmware config from the machine inventory.
        DEST=${D}${config_datadir}
        install -d ${DEST}
        install scripts/example.yaml ${DEST}/config.yaml
}
