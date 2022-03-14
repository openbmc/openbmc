SUMMARY = "foxconn OEM IPMI commands"
DESCRIPTION = "foxconn OEM IPMI commands"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

S = "${WORKDIR}/git"

DEPENDS = "boost phosphor-ipmi-host phosphor-logging systemd libgpiod"

inherit cmake pkgconfig obmc-phosphor-ipmiprovider-symlink

EXTRA_OECMAKE="-DENABLE_TEST=0 -DYOCTO=1"

LIBRARY_NAMES = "libfiioemcmds.so"

SRC_URI = "git://github.com/openbmc/foxconn-ipmi-oem.git;branch=master;protocol=https"
SRCREV = "9d6c4010b58049070afcb39ef4c2a965633e2e9c"

HOSTIPMI_PROVIDER_LIBRARY += "${LIBRARY_NAMES}"
NETIPMI_PROVIDER_LIBRARY += "${LIBRARY_NAMES}"

FILES:${PN}:append = " ${libdir}/ipmid-providers/lib*${SOLIBS}"
FILES:${PN}:append = " ${libdir}/host-ipmid/lib*${SOLIBS}"
FILES:${PN}:append = " ${libdir}/net-ipmid/lib*${SOLIBS}"
FILES:${PN}-dev:append = " ${libdir}/ipmid-providers/lib*${SOLIBSDEV}"
