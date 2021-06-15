SUMMARY = "Phosphor OpenBMC DBUS Permissions"
DESCRIPTION = "Phosphor OpenBMC DBUS Permissions."
HOMEPAGE = "http://github.com/openbmc/"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch
inherit dbus-dir

SRC_URI += "file://org.openbmc.conf"

do_install_append() {
        install -d ${D}${dbus_system_confdir}
        install -m 0644 ${WORKDIR}/org.openbmc.conf \
                ${D}${dbus_system_confdir}
}
