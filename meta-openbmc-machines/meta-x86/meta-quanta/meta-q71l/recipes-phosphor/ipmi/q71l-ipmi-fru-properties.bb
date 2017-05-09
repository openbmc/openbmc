SUMMARY = "FRU properties config for ipmi-fru-parser"
PR = "r1"

inherit native
inherit obmc-phosphor-license
inherit phosphor-ipmi-fru

SRC_URI += "file://extra-properties.yaml"

PROVIDES += "virtual/phosphor-ipmi-fru-properties"

S = "${WORKDIR}"

do_install() {
        # TODO: install this to inventory_datadir
        # after ipmi-fru-parser untangles the host
        # firmware config from the machine inventory.
        DEST=${D}${properties_datadir}

        install -d ${DEST}
        install extra-properties.yaml ${DEST}/.
}

