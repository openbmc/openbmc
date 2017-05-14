SUMMARY = "Copy error yaml files to known path for elog parsing"

PR = "r1"

inherit native
inherit obmc-phosphor-license
inherit openpower-dbus-interfaces

PROVIDES += "openpower-debug-collector-error-native"
require openpower-debug-collector.inc

S = "${WORKDIR}/git"

do_install_append() {
    SRC=${S}/org/open_power
    DEST=${op_error_yaml_dir}/org/open_power
    install -d ${DEST}
    install ${SRC}/Host.errors.yaml ${DEST}
}
