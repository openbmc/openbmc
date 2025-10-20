SUMMARY = "Phosphor IPMI plugin for the Host I/O Mapping Protocol"
HOMEPAGE = "https://github.com/openbmc/openpower-host-ipmi-flash"
PR = "r1"
PV = "0.1+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ff331e820fda701d36a8f0efc98adc58"

inherit meson pkgconfig
inherit obmc-phosphor-ipmiprovider-symlink

DEPENDS += "phosphor-ipmi-host"
DEPENDS += "sdbusplus"
DEPENDS += "phosphor-logging"
DEPENDS += "phosphor-dbus-interfaces"

TARGET_CFLAGS += "-fpic"

HOSTIPMI_PROVIDER_LIBRARY += "libhiomap.so"

S = "${WORKDIR}/git"

SRC_URI = "git://github.com/openbmc/openpower-host-ipmi-flash;branch=master;protocol=https"
SRCREV = "f2e9545580ea4a6d700389a76fad677e6e9281a4"

EXTRA_OEMESON:append = " -Dtests=disabled"

FILES:${PN}:append = " ${libdir}/ipmid-providers/lib*${SOLIBS}"
FILES:${PN}:append = " ${libdir}/host-ipmid/lib*${SOLIBS}"
FILES:${PN}-dev:append = " ${libdir}/ipmid-providers/lib*${SOLIBSDEV} ${libdir}/ipmid-providers/*.la"
