SUMMARY = "C++ bindings for systemd event APIs"
DESCRIPTION = "C++ bindings for systemd event APIs."
HOMEPAGE = "http://github.com/openbmc/sdeventplus"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"
DEPENDS += " \
        stdplus \
        systemd \
        "
SRCREV = "18db9a3e47e7ebdedae5b698bfb1068366ff6824"
PV = "0.1+git${SRCPV}"
PR = "r1"

SRC_URI = "git://github.com/openbmc/sdeventplus;branch=master;protocol=https"

S = "${WORKDIR}/git"

inherit meson pkgconfig

EXTRA_OEMESON = " \
        -Dexamples=false \
        -Dtests=disabled \
        "
