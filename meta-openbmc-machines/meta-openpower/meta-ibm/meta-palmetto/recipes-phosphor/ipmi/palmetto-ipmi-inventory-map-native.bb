SUMMARY = "Palmetto IPMI to DBus Inventory mapping."
PR = "r1"

inherit native
inherit obmc-phosphor-license
inherit phosphor-ipmi-fru

SRC_URI += "file://config.yaml"

PROVIDES += "virtual/phosphor-ipmi-fru-inventory"

S = "${WORKDIR}"

do_install() {
        # TODO: install this to inventory_datadir
        # after ipmi-fru-parser untangles the host
        # firmware config from the machine inventory.
        DEST=${D}${config_datadir}

        install -d ${DEST}
        install config.yaml ${DEST}
}

