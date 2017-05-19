SUMMARY = "Copy error yaml files to shared folder for elog parsing"

inherit native
inherit pkgconfig
inherit obmc-phosphor-license
inherit phosphor-ipmi-host
inherit phosphor-dbus-interfaces

PROVIDES += "virtual/phosphor-ipmi-host-error"

require phosphor-ipmi-host.inc

S = "${WORKDIR}/git"

do_install_append() {
    SRC=${S}/xyz/openbmc_project/Host
    DEST=${yaml_dir}/xyz/openbmc_project/Host
    install -d ${DEST}
    install ${SRC}/Event.errors.yaml ${DEST}
    install ${SRC}/Event.metadata.yaml ${DEST}
}
