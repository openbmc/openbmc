SUMMARY = "Phosphor OpenBMC DBUS Permissions"
DESCRIPTION = "Phosphor OpenBMC DBUS Permissions."
HOMEPAGE = "http://github.com/openbmc/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"
PR = "r1"

SRC_URI += "file://org.openbmc.conf"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

inherit allarch
inherit dbus-dir

do_install:append() {
        install -d ${D}${dbus_system_confdir}
        install -m 0644 ${UNPACKDIR}/org.openbmc.conf \
                ${D}${dbus_system_confdir}
}

FILES:${PN}:append = " ${dbus_system_confdir}"
