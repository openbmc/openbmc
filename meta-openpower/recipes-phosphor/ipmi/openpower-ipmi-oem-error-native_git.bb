SUMMARY = "Copy error yaml files to known path for elog parsing"

PR = "r1"
PV = "1.0+git${SRCPV}"

inherit phosphor-dbus-yaml
inherit native

require openpower-ipmi-oem.inc

S = "${WORKDIR}/git"

do_install_append() {
    SRC=${S}/org/open_power/OCC
    DEST=${D}${yaml_dir}/org/open_power/OCC
    install -d ${DEST}
    install ${SRC}/Metrics.errors.yaml ${DEST}
    install ${SRC}/Metrics.metadata.yaml ${DEST}
}
