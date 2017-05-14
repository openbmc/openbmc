SUMMARY = "Copy error yaml files to shared folder for elog parsing"

PR = "r1"

inherit native
inherit pkgconfig
inherit obmc-phosphor-license
inherit openpower-dbus-interfaces

PROVIDES += "virtual/openpower-debug-collector-error"

require openpower-debug-collector.inc

S = "${WORKDIR}/git"

do_install_append() {
    SRC=${S}/org/open_power
    DEST=${op_yaml_dir}/org/open_power
    install -d ${DEST}
    install ${SRC}/Host.errors.yaml ${DEST}
}
