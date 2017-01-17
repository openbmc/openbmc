SUMMARY = "Phosphor IPMI FRU Configuration Example"
DESCRIPTION = "An example Phosphor IPMI FRU IPMI to \
DBUS mapping configuration."

PR = "r1"

inherit native

require phosphor-ipmi-fru.inc

PROVIDES += "virtual/phosphor-ipmi-fru-config"

S = "${WORKDIR}/git"

do_install() {
        SRC=${S}/example-map.yaml
        DEST=${D}${datadir}/phosphor-ipmi-fru

        install -d ${DEST}
        # TODO - install the example, once one is provided by ipmi-fru-parser.
        # install-data ${SRC} ${DEST}
}
