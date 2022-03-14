SUMMARY = "IBM DBus Interfaces"
DESCRIPTION = "Generated bindings, using sdbus++, for the IBM YAML"
PR = "r1"
PV = "1.0+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

S = "${WORKDIR}/git"

inherit autotools pkgconfig
inherit python3native
inherit phosphor-dbus-yaml

DEPENDS += "autoconf-archive-native"
DEPENDS += "${PYTHON_PN}-sdbus++-native"

SRC_URI += "git://github.com/openbmc/ibm-dbus-interfaces;branch=master;protocol=https"
SRCREV = "89d04feb5d05a8ac01d734eed8889f6800324459"

PACKAGECONFIG ??= "libibm_dbus"
PACKAGECONFIG[libibm_dbus] = " \
        --enable-libibm_dbus, \
        --disable-libibm_dbus, \
        systemd sdbusplus, \
        libsystemd \
        "

PACKAGECONFIG:remove:class-native = "libibm_dbus"
PACKAGECONFIG:remove:class-nativesdk = "libibm_dbus"

BBCLASSEXTEND += "native nativesdk"
