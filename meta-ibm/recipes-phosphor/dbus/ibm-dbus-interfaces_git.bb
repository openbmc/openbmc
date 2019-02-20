SUMMARY = "IBM DBus Interfaces"
DESCRIPTION = "Generated bindings, using sdbus++, for the IBM YAML"
PR = "r1"
PV = "1.0+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

S = "${WORKDIR}/git"

inherit autotools pkgconfig
inherit pythonnative
inherit phosphor-dbus-yaml

DEPENDS += "autoconf-archive-native"
DEPENDS += "sdbus++-native"

SRC_URI += "git://github.com/openbmc/ibm-dbus-interfaces"
SRCREV = "0c073d5f2645fb6e109b877db3f20fe9b01bdf09"

DEPENDS_remove_class-native = "sdbus++-native"
DEPENDS_remove_class-nativesdk = "sdbus++-native"

PACKAGECONFIG ??= "libibm_dbus"
PACKAGECONFIG[libibm_dbus] = " \
        --enable-libibm_dbus, \
        --disable-libibm_dbus, \
        systemd sdbusplus, \
        libsystemd sdbusplus \
        "

PACKAGECONFIG_remove_class-native = "libibm_dbus"
PACKAGECONFIG_remove_class-nativesdk = "libibm_dbus"

BBCLASSEXTEND += "native nativesdk"
