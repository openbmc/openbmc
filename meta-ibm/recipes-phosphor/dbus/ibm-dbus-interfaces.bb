SUMMARY = "IBM DBus Interfaces"
DESCRIPTION = "Generated bindings, using sdbus++, for the IBM YAML"
PR = "r1"

S = "${WORKDIR}/git"

inherit autotools pkgconfig
inherit obmc-phosphor-license
inherit pythonnative
inherit phosphor-dbus-yaml

DEPENDS += "autoconf-archive-native"
DEPENDS += "sdbus++-native"

SRC_URI += "git://github.com/openbmc/ibm-dbus-interfaces"
SRCREV = "62c7bff037587fe0fbfdffbe622058d86c7d7b7b"

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
