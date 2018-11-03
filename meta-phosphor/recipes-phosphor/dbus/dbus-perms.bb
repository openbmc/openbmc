SUMMARY = "Phosphor OpenBMC DBUS Permissions"
DESCRIPTION = "Phosphor OpenBMC DBUS Permissions."
HOMEPAGE = "http://github.com/openbmc/"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${PHOSPHORBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit allarch
inherit dbus-dir

SRC_URI += "file://org.openbmc.conf"

do_install_append() {
        install -d ${D}${dbus_system_confdir}
        install -m 0644 ${WORKDIR}/org.openbmc.conf \
                ${D}${dbus_system_confdir}
}
