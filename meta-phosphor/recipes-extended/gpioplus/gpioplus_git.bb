SUMMARY = "C++ bindings for linux gpio APIs"
DESCRIPTION = "C++ bindings for linux gpio APIs."
HOMEPAGE = "http://github.com/openbmc/gpioplus"
PR = "r1"
PV = "0.1+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

inherit autotools pkgconfig

DEPENDS += " \
        autoconf-archive-native \
        "

EXTRA_OECONF_append += " \
        --disable-examples \
        --disable-tests \
        "

SRC_URI += "git://github.com/openbmc/gpioplus"
SRCREV = "15658977ccf42078fcf021494d10400666c7a884"

S = "${WORKDIR}/git"
