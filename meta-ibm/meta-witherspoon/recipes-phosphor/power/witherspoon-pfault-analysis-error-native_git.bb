SUMMARY = "Copy error yaml files to known path for elog parsing"
PR = "r1"
PV = "1.0+git${SRCPV}"

inherit native
inherit phosphor-dbus-yaml

require witherspoon-pfault-analysis.inc

S = "${WORKDIR}/git"

do_install_append() {
    SRC=${S}/org/open_power/Witherspoon
    DEST=${D}${yaml_dir}/org/open_power/Witherspoon
    install -d ${DEST}
    install ${SRC}/Fault.errors.yaml ${DEST}
    install ${SRC}/Fault.metadata.yaml ${DEST}
}
