SUMMARY = "IEISystems OEM IPMI commands"
DESCRIPTION = "IEISystems OEM IPMI commands"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

SRC_URI = "git://github.com/openbmc/iei-ipmi-oem;branch=master;protocol=https"
SRCREV = "45d31233ff298d45dca6694e729351ceefed2618"

S = "${WORKDIR}/git"
PV = "0.1+git${SRCPV}"

DEPENDS += "phosphor-ipmi-host"
DEPENDS += "phosphor-logging"

inherit meson pkgconfig
inherit obmc-phosphor-ipmiprovider-symlink

PACKAGECONFIG ??= ""

FILES:${PN}:append = " ${libdir}/ipmid-providers/lib*${SOLIBS}"
FILES:${PN}:append = " ${libdir}/host-ipmid/lib*${SOLIBS}"
FILES:${PN}:append = " ${libdir}/net-ipmid/lib*${SOLIBS}"
FILES:${PN}-dev:append = " ${libdir}/ipmid-providers/lib*${SOLIBSDEV}"
