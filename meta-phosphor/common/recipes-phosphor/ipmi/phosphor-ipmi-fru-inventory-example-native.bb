SUMMARY = "Sample inventory map for phosphor-ipmi-fru"
PR = "r1"

inherit native
inherit phosphor-ipmi-fru

require phosphor-ipmi-fru.inc

PROVIDES += "virtual/phosphor-ipmi-fru-inventory"

S = "${WORKDIR}/git"

do_install() {
        DEST=${D}${inventory_datadir}
        install -d ${DEST}

        # TODO: copy example inventory yaml to ${DEST}/config.yaml
        # install example-inventory.yaml ${DEST}/config.yaml
}
