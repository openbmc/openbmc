SUMMARY = "Copy error yaml files to native folder"
PR = "r1"

inherit native
inherit pkgconfig
inherit obmc-phosphor-license
inherit phosphor-dbus-interfaces

PROVIDES += "virtual/openpower-debug-collector-error-yaml"

require openpower-debug-collector.inc

S = "${WORKDIR}/git"

do_install_append() {
    SRC=${S}/org
    DEST=${yaml_dir}
    install -d ${DEST}

    cp -r ${SRC} ${DEST}
}
