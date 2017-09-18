SUMMARY = "Copy error yaml files to known path for elog parsing"
PR = "r1"

inherit native
inherit obmc-phosphor-license
inherit phosphor-dbus-yaml

require op-proc-control.inc

S = "${WORKDIR}/git"

do_install_append() {
    SRC=${S}/org/open_power/Proc
    DEST=${yaml_dir}/org/open_power/Proc
    install -d ${DEST}
    install ${SRC}/*.yaml ${DEST}
}
