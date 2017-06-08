SUMMARY = "Sample inventory map for phosphor-ipmi-host"
PR = "r1"

inherit native
inherit phosphor-ipmi-host

require phosphor-ipmi-host.inc

PROVIDES += "virtual/phosphor-ipmi-host-inventory"

S = "${WORKDIR}/git"

do_install() {
        # TODO: install this to inventory_datadir
        # after ipmi-host-parser untangles the host
        # firmware config from the machine inventory.
        DEST=${D}${config_datadir}
        install -d ${DEST}
        install scripts/example.yaml ${DEST}/config.yaml
}
