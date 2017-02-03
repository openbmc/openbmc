SUMMARY = "Phosphor DBus Interfaces"
DESCRIPTION = "Generated bindings, using sdbus++, for the phosphor YAML"
PR = "r1"

S = "${WORKDIR}/git"

inherit autotools pkgconfig
inherit obmc-phosphor-license
inherit pythonnative

DEPENDS += "autoconf-archive-native"
DEPENDS += "sdbus++-native"

PACKAGE_BEFORE_PN = "${PN}-yaml"

FILES_${PN}-yaml = "${datadir}/${PN}/yaml"

SRC_URI += "git://github.com/bradbishop/phosphor-dbus-interfaces"
SRCREV = "609189b6ddf144d2e4babfb7a05978acd129881f"

DEPENDS_remove_class-native = "sdbus++-native"
DEPENDS_remove_class-nativesdk = "sdbus++-native"

PACKAGECONFIG ??= "libphosphor_dbus"
PACKAGECONFIG[libphosphor_dbus] = "--enable-libphosphor_dbus,--disable-libphosphor_dbus,sdbusplus,sdbusplus"

PACKAGECONFIG_remove_class-native = "libphosphor_dbus"
PACKAGECONFIG_remove_class-nativesdk = "libphosphor_dbus"

BBCLASSEXTEND += "native nativesdk"
