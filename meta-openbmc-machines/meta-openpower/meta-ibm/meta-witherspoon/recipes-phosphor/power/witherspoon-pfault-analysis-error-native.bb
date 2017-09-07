SUMMARY = "Copy error yaml files to known path for elog parsing"
PR = "r1"

inherit native
inherit obmc-phosphor-license
inherit phosphor-dbus-yaml

PROVIDES += "witherspoon-pfault-analysis-error-native"
require witherspoon-pfault-analysis.inc

S = "${WORKDIR}/git"

do_install_append() {
    SRC=${S}/xyz/openbmc_project/Power
    DEST=${yaml_dir}/xyz/openbmc_project/Power
    install -d ${DEST}
    install ${SRC}/Fault.errors.yaml ${DEST}
}
