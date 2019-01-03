SUMMARY = "Intel DBus Interfaces"
DESCRIPTION = "Generated bindings, using sdbus++, for the Intel YAML"
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

SRC_URI = "git://github.com/openbmc/intel-dbus-interfaces"
SRCREV = "299018f99693771c175980315a3ee54f7abd7303"

DEPENDS_remove_class-native = "sdbus++-native"
DEPENDS_remove_class-nativesdk = "sdbus++-native"

PACKAGECONFIG ??= "libintel_dbus"
PACKAGECONFIG[libintel_dbus] = " \
        --enable-libintel_dbus, \
        --disable-libintel_dbus, \
        systemd sdbusplus, \
        libsystemd sdbusplus \
        "

PACKAGECONFIG_remove_class-native = "libintel_dbus"
PACKAGECONFIG_remove_class-nativesdk = "libintel_dbus"

BBCLASSEXTEND += "native nativesdk"