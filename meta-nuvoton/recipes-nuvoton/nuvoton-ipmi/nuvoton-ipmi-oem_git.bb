SUMMARY = "Nuvoton IPMI OEM command library"
DESCRIPTION = "Nuvoton IPMI OEM command library"
HOMEPAGE = "https://github.com/nuvoton-ipmi-oem"
PR = "r1"
PV = "0.1+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ce3556061e8d4b01638d497053a82dfd"

inherit autotools pkgconfig
inherit obmc-phosphor-ipmiprovider-symlink

DEPENDS += "autoconf-archive-native"
DEPENDS += "sdbusplus"
DEPENDS += "phosphor-logging"
DEPENDS += "phosphor-ipmi-host"
DEPENDS += "nlohmann-json"

S = "${WORKDIR}/git"
SRCBRANCH = "master"
SRC_URI = "git://github.com/Nuvoton-Israel/nuvoton-ipmi-oem;protocol=https;branch=${SRCBRANCH}"
SRCREV = "50d80e64a01d933c10484da63b447d6a27b8b702"

FILES:${PN}:append = " ${libdir}/ipmid-providers/lib*${SOLIBS}"
FILES:${PN}:append = " ${libdir}/host-ipmid/lib*${SOLIBS}"
FILES:${PN}:append = " ${libdir}/net-ipmid/lib*${SOLIBS}"
FILES:${PN}-dev:append = " ${libdir}/ipmid-providers/lib*${SOLIBSDEV} ${libdir}/ipmid-providers/*.la"

HOSTIPMI_PROVIDER_LIBRARY += "libnuvoemcmds.so"
