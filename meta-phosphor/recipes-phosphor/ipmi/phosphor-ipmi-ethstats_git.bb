SUMMARY = "Phosphor OEM IPMI Ethernet Stats Implementation"
DESCRIPTION = "This package handles receiving OEM IPMI commands to provide ethernet device statistics."
HOMEPAGE = "http://github.com/openbmc/phosphor-ipmi-ethstats"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"
DEPENDS += "autoconf-archive-native"
DEPENDS += "phosphor-ipmi-host"
SRCREV = "89dac6a829d8631f594f4b1f8faf05e389bad4e3"
PV = "0.1+git${SRCPV}"
PR = "r1"

SRC_URI = "git://github.com/openbmc/phosphor-ipmi-ethstats;branch=master;protocol=https"

S = "${WORKDIR}/git"

inherit autotools pkgconfig
inherit obmc-phosphor-ipmiprovider-symlink

EXTRA_OECONF += "--disable-tests"

FILES:${PN}:append = " ${libdir}/ipmid-providers/lib*${SOLIBS}"
FILES:${PN}:append = " ${libdir}/host-ipmid/lib*${SOLIBS}"
FILES:${PN}:append = " ${libdir}/net-ipmid/lib*${SOLIBS}"
FILES:${PN}-dev:append = " ${libdir}/ipmid-providers/lib*${SOLIBSDEV} ${libdir}/ipmid-providers/*.la"

HOSTIPMI_PROVIDER_LIBRARY += "libethstatscmd.so"
