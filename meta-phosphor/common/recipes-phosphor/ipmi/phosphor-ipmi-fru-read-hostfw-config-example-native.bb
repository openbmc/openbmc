SUMMARY = "Sample hostfw inventory map for phosphor-ipmi-host"
PR = "r1"

inherit native
inherit phosphor-ipmi-host

require phosphor-ipmi-host.inc

PROVIDES += "virtual/phosphor-ipmi-fru-read-hostfw-config"

S = "${WORKDIR}/git"

do_install() {
    SRC=${S}/scripts/
    DEST=${D}${hostfw_datadir}
    install -d ${DEST}
    install ${SRC}/example.yaml ${DEST}/config.yaml
}
