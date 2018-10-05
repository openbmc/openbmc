SUMMARY = "Open POWER DBus Interfaces"
DESCRIPTION = "Generated bindings, using sdbus++, for the openpower YAML"
PR = "r1"
PV = "1.0+git${SRCPV}"

S = "${WORKDIR}/git"

inherit autotools pkgconfig
inherit obmc-phosphor-license
inherit pythonnative
inherit phosphor-dbus-yaml

DEPENDS += "autoconf-archive-native"
DEPENDS += "sdbus++-native"

SRC_URI += "git://github.com/openbmc/openpower-dbus-interfaces"
SRCREV = "998a82ec587be23bc5c2e59ba858fb099279c5f0"

DEPENDS_remove_class-native = "sdbus++-native"
DEPENDS_remove_class-nativesdk = "sdbus++-native"

PACKAGECONFIG ??= "libopenpower_dbus"
PACKAGECONFIG[libopenpower_dbus] = " \
        --enable-libopenpower_dbus, \
        --disable-libopenpower_dbus, \
        systemd sdbusplus, \
        libsystemd sdbusplus \
        "

PACKAGECONFIG_remove_class-native = "libopenpower_dbus"
PACKAGECONFIG_remove_class-nativesdk = "libopenpower_dbus"

BBCLASSEXTEND += "native nativesdk"
