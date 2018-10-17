SUMMARY = "IBM DBus Interfaces"
DESCRIPTION = "Generated bindings, using sdbus++, for the IBM YAML"
PR = "r1"
PV = "1.0+git${SRCPV}"

S = "${WORKDIR}/git"

inherit autotools pkgconfig
inherit obmc-phosphor-license
inherit pythonnative
inherit phosphor-dbus-yaml

DEPENDS += "autoconf-archive-native"
DEPENDS += "sdbus++-native"

SRC_URI += "git://github.com/openbmc/ibm-dbus-interfaces"
SRCREV = "c9ae6bf297b40391256c8f21a01d72e049036017"

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
