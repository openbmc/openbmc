SUMMARY = "Copy error yaml files to known path for elog parsing"
PR = "r1"

inherit native
inherit obmc-phosphor-license
inherit phosphor-dbus-yaml

PROVIDES += "witherspoon-pfault-analysis-error-native"
require witherspoon-pfault-analysis.inc

S = "${WORKDIR}/git"

do_install_append() {
    SRC=${S}/org/open_power/Witherspoon
    DEST=${yaml_dir}/org/open_power/Witherspoon
    install -d ${DEST}
    install ${SRC}/Fault.errors.yaml ${DEST}
    install ${SRC}/Fault.metadata.yaml ${DEST}
}
