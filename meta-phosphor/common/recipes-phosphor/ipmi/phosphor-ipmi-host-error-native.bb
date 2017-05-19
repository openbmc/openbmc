SUMMARY = "Copy error yaml files to shared folder for elog parsing"

inherit native
inherit obmc-phosphor-license
inherit phosphor-dbus-interfaces

PROVIDES += "virtual/phosphor-ipmi-host-error"

require phosphor-ipmi-host.inc

S = "${WORKDIR}/git"

do_install_append() {
    SRC=${S}/org/open_power/Host
    DEST=${yaml_dir}/org/open_power/Host
    install -d ${DEST}
    install ${SRC}/Event.errors.yaml ${DEST}
    install ${SRC}/Event.metadata.yaml ${DEST}
}
