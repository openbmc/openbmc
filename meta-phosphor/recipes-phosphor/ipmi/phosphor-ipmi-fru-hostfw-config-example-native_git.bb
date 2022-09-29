SUMMARY = "Sample hostfw inventory map for phosphor-ipmi-fru"
PROVIDES += "virtual/phosphor-ipmi-fru-hostfw-config"
PV = "1.0+git${SRCPV}"
PR = "r1"

S = "${WORKDIR}/git"

inherit phosphor-ipmi-fru
inherit native

do_install() {
        DEST=${D}${hostfw_datadir}
        install -d ${DEST}
        # TODO: copy example hostfw yaml to ${DEST}/config.yaml
        # install fru-types.yaml ${DEST}/config.yaml
}

require phosphor-ipmi-fru.inc
