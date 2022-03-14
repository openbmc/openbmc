SUMMARY = "Intel DBus Interfaces"
DESCRIPTION = "Generated bindings, using sdbus++, for the Intel YAML"
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

SRC_URI = "git://github.com/openbmc/intel-dbus-interfaces;branch=master;protocol=https"
SRCREV = "2b1255c47fdaaee6fd2db8f38f32abc13a7edf05"

PACKAGECONFIG ??= "libintel_dbus"
PACKAGECONFIG[libintel_dbus] = " \
        --enable-libintel_dbus, \
        --disable-libintel_dbus, \
        systemd sdbusplus, \
        libsystemd \
        "

PACKAGECONFIG:remove:class-native = "libintel_dbus"
PACKAGECONFIG:remove:class-nativesdk = "libintel_dbus"

BBCLASSEXTEND += "native nativesdk"
