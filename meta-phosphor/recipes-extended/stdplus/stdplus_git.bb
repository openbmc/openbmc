SUMMARY = "Common C++ functions"
DESCRIPTION = "Common C++ functions."
HOMEPAGE = "http://github.com/openbmc/stdplus"
PR = "r1"
PV = "0.1+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

inherit meson pkgconfig

DEPENDS += "fmt"
DEPENDS += "span-lite"

EXTRA_OEMESON = " \
        -Dexamples=false \
        -Dtests=disabled \
        "

SRC_URI += "git://github.com/openbmc/stdplus"
SRCREV = "5f22a6c260295b5d8553d77893aba01684e4a72c"

S = "${WORKDIR}/git"
