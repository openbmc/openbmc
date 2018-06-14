LICENSE = "BSL-1.0"
LIC_FILES_CHKSUM = "file://LICENSE_1_0.txt;md5=e4224ccaecb14d942c71d31bef20d78c"

SRC_URI = "gitsm://github.com/openbmc/boost-dbus.git"

PV = "1.0+git${SRCPV}"
SRCREV = "2ec80375259500c43f5a7c620cb34cd894f7836c"

S = "${WORKDIR}/git"

DEPENDS = "dbus boost gtest "

inherit cmake

FILES_${PN}-dev += "${libdir}/cmake/boost-dbus/*"
