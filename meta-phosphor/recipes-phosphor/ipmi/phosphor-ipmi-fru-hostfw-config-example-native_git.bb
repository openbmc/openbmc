SUMMARY = "Sample hostfw inventory map for phosphor-ipmi-fru"
PR = "r1"
PV = "1.0+git${SRCPV}"

inherit phosphor-ipmi-fru
inherit native

require phosphor-ipmi-fru.inc

PROVIDES += "virtual/phosphor-ipmi-fru-hostfw-config"

S = "${WORKDIR}/git"

do_install() {
        DEST=${D}${hostfw_datadir}
        install -d ${DEST}

        # TODO: copy example hostfw yaml to ${DEST}/config.yaml
        # install fru-types.yaml ${DEST}/config.yaml
}
