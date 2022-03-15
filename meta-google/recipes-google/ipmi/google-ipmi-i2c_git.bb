SUMMARY = "Google i2c OEM commands"
DESCRIPTION = "Google i2c OEM commands"
HOMEPAGE = "https://github.com/openbmc/google-ipmi-i2c"
PR = "r1"
PV = "0.1+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

inherit meson pkgconfig

DEPENDS += "phosphor-ipmi-host"

S = "${WORKDIR}/git"
SRC_URI = "git://github.com/openbmc/google-ipmi-i2c;branch=master;protocol=https"
SRCREV = "87a8b11075abe0fa70f9ce20d7dea0f2bdc742ad"

FILES:${PN}:append = " ${libdir}/ipmid-providers"
FILES:${PN}:append = " ${libdir}/host-ipmid"
FILES:${PN}:append = " ${libdir}/net-ipmid"

HOSTIPMI_PROVIDER_LIBRARY += "libi2ccmds.so"
