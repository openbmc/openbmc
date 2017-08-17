SUMMARY = "Phosphor IPMI plugin for OpenPOWER OEM Commands"
DESCRIPTION = "Phosphor IPMI plugin for OpenPOWER OEM Commands"
HOMEPAGE = "https://github.com/openbmc/openpower-host-ipmi-oem"
PR = "r1"

inherit autotools pkgconfig
inherit obmc-phosphor-license
inherit obmc-phosphor-ipmiprovider-symlink
inherit obmc-phosphor-utils

DEPENDS += "phosphor-ipmi-host"
DEPENDS += "autoconf-archive-native"
DEPENDS += "sdbusplus sdbusplus-native"
DEPENDS += "phosphor-logging"
DEPENDS += "phosphor-dbus-interfaces phosphor-dbus-interfaces-native"
DEPENDS += "openpower-dbus-interfaces openpower-dbus-interfaces-native"

RDEPENDS_${PN} += " \
        sdbusplus \
        phosphor-logging \
        openpower-dbus-interfaces \
        phosphor-dbus-interfaces \
        "

TARGET_CFLAGS += "-fpic"

SRC_URI += "git://github.com/openbmc/openpower-host-ipmi-oem"
SRCREV = "07655065ee71c7056389a17b86d43d24a1915db7"

HOSTIPMI_PROVIDER_LIBRARY += "liboemhandler.so"

S = "${WORKDIR}/git"

FILES_${PN}_append = " ${libdir}/ipmid-providers/lib*${SOLIBS}"
FILES_${PN}_append = " ${libdir}/host-ipmid/lib*${SOLIBS}"
FILES_${PN}-dev_append = " ${libdir}/ipmid-providers/lib*${SOLIBSDEV} ${libdir}/ipmid-providers/*.la"
