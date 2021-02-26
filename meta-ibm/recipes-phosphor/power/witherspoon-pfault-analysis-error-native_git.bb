SUMMARY = "Copy error yaml files to known path for elog parsing"
PR = "r1"
PV = "1.0+git${SRCPV}"

inherit phosphor-dbus-yaml
inherit native

require recipes-phosphor/power/phosphor-power.inc

S = "${WORKDIR}/git"

do_install_append() {
    SRC=${S}/org/open_power/Witherspoon
    DEST=${D}${yaml_dir}/org/open_power/Witherspoon
    install -d ${DEST}
    install ${SRC}/Fault.errors.yaml ${DEST}
    install ${SRC}/Fault.metadata.yaml ${DEST}
}
